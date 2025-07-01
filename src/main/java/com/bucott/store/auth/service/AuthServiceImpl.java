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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AddressMapper addressMapper;

    public AuthServiceImpl(UserRepository userRepo, RoleRepository roleRepo, JwtUtil jwtUtil, 
                          PasswordEncoder passwordEncoder, UserMapper userMapper, AddressMapper addressMapper) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.addressMapper = addressMapper;
    }

    @Override
    public LoginResponseDTO authenticate(LoginRequestDTO requestDto) throws UserNotFoundException, InvalidCredentialsException {
        User user = userRepo.findByUsernameOrEmail(requestDto.getUsernameOrEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username or email: " + requestDto.getUsernameOrEmail()));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password for user: " + requestDto.getUsernameOrEmail());
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail());

        return LoginResponseDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO requestDto) throws InvalidInputException {
        log.debug("Registering user with username: {} - email {}", requestDto.getUsername(), requestDto.getEmail());
        
        validateRegistrationRequest(requestDto);

        // Use MapStruct to convert DTO to entity
        User user = userMapper.toEntity(requestDto);
        
        // Set encoded password
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        
        // Map address separately
        Address address = addressMapper.toEntity(requestDto.getAddress());
        user.setAddress(address);

        // Set user role
        Role userRole = findOrCreateUserRole();
        user.getRoles().add(userRole);

        // Save user
        user = userRepo.save(user);
        log.info("User saved successfully with ID: {}, Username: {}", user.getId(), user.getUsername());

        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail());

        // Use MapStruct to convert entity to response DTO
        RegisterResponseDTO response = userMapper.toRegisterResponseDTO(user);
        response.setToken(token);
        
        return response;
    }

    @Override
    public UserInfoDTO getCurrentUser(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                String username = jwtUtil.extractUsername(token);
                String email = jwtUtil.extractEmail(token);

                if (jwtUtil.validateToken(token, username)) {
                    return UserInfoDTO.builder()
                            .username(username)
                            .email(email)
                            .authenticated(true)
                            .build();
                }
            } catch (Exception e) {
                log.error("Error extracting user info from token: {}", e.getMessage());
            }
        }
        return UserInfoDTO.builder()
                .authenticated(false)
                .build();
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

            UserInfoDTO userInfo = userMapper.toUserInfoDTO(user);
            userInfo.setAuthenticated(true);
            return userInfo;
        } catch (Exception e) {
            log.error("Error fetching user info for username: {}", username);
            return UserInfoDTO.builder()
                    .authenticated(false)
                    .build();
        }
    }

    private void validateRegistrationRequest(RegisterRequestDTO requestDto) throws InvalidInputException {
        if (userRepo.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new InvalidInputException("Username already exists");
        }
        if (userRepo.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new InvalidInputException("Email already exists");
        }
        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
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