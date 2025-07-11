package com.bucott.store.auth.service;

import com.bucott.store.auth.dto.LoginRequestDTO;
import com.bucott.store.auth.dto.LoginResponseDTO;
import com.bucott.store.auth.dto.RegisterRequestDTO;
import com.bucott.store.auth.dto.RegisterResponseDTO;
import com.bucott.store.user.dto.UserInfoDTO;
import com.bucott.store.auth.exception.InvalidCredentialsException;
import com.bucott.store.common.exception.InvalidInputException;
import com.bucott.store.user.exception.UserNotFoundException;
import com.bucott.store.address.mapper.AddressMapper;
import com.bucott.store.user.mapper.UserMapper;
import com.bucott.store.address.model.Address;
import com.bucott.store.user.model.Authority;
import com.bucott.store.user.model.Role;
import com.bucott.store.user.model.User;
import com.bucott.store.user.repository.RoleRepository;
import com.bucott.store.user.repository.UserRepository;
import com.bucott.store.security.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AddressMapper addressMapper;

    @Override
    public LoginResponseDTO authenticate(LoginRequestDTO requestDto) throws UserNotFoundException, InvalidCredentialsException {
        User user = userRepo.findByUsernameOrEmail(requestDto.usernameOrEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username or email: " + requestDto.usernameOrEmail()));

        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password for user: " + requestDto.usernameOrEmail());
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail());

        LoginResponseDTO response = new LoginResponseDTO(user.getUsername(), user.getEmail(), token);
        return response;
    }

    @Override
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO requestDto) throws InvalidInputException {
        log.debug("Registering user with username: {} - email {}", requestDto.username(), requestDto.email());
        
        validateRegistrationRequest(requestDto);

        // Use MapStruct to convert DTO to entity
        User user = userMapper.toEntity(requestDto);
        
        // Set encoded password
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        
        // Map address separately
        Address address = addressMapper.toEntity(requestDto.address());
        user.setAddress(address);

        // Set user role
        Role userRole = findOrCreateUserRole();
        user.getRoles().add(userRole);

        // Save user
        user = userRepo.save(user);
        log.info("User saved successfully with ID: {}, Username: {}", user.getId(), user.getUsername());

        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail());

       return new RegisterResponseDTO(
                        user.getUsername(),
                        user.getEmail(),
                        token,
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber(),
                        addressMapper.toInfoDTO(address));

    }

    @Override
    public UserInfoDTO getCurrentUser(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                String username = jwtUtil.extractUsername(token);
                String email = jwtUtil.extractEmail(token);

                if (jwtUtil.validateToken(token, username)) {
                    return new UserInfoDTO(
                            username,
                            email,
                            true);
                }
            } catch (Exception e) {
                log.error("Error extracting user info from token: {}", e.getMessage());
            }
        }
        return new UserInfoDTO(null, null, false);
    }

    @Override
    public boolean validateToken(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                String username = jwtUtil.extractUsername(token);
                return jwtUtil.validateToken(token, username);
            } catch (Exception e) {
                log.error("Error validating token: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public UserInfoDTO getUserInfoByUsername(String username) {
        try {
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

            return new UserInfoDTO(user.getUsername(), user.getEmail(), true);
        } catch (Exception e) {
            log.error("Error fetching user info for username: {}", username);
            return new UserInfoDTO(null, null, false);
        }
    }

    private void validateRegistrationRequest(RegisterRequestDTO requestDto) throws InvalidInputException {
        if (userRepo.findByUsername(requestDto.username()).isPresent()) {
            throw new InvalidInputException("Username already exists");
        }
        if (userRepo.findByEmail(requestDto.email()).isPresent()) {
            throw new InvalidInputException("Email already exists");
        }
        if (!requestDto.password().equals(requestDto.confirmPassword())) {
            throw new InvalidInputException("Passwords do not match");
        }
    }


    private Role findOrCreateUserRole() {
        log.info("Looking for role: {}", Authority.ROLE_USER.name());
        Role userRole = roleRepo.findByAuthority(Authority.ROLE_USER);
        if (userRole == null) {
            log.info("Role not found, creating new role");
            userRole = roleRepo.save(new Role(Authority.ROLE_USER));
            log.info("Role created with ID: {}", userRole.getId());
        } else {
            log.info("Found existing role with ID: {}", userRole.getId());
        }
        return userRole;
    }
}