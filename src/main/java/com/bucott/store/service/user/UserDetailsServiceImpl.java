package com.bucott.store.service.user;

import com.bucott.store.dto.address.AddressInfoDTO;
import com.bucott.store.model.address.Address;
import com.bucott.store.model.address.AddressType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bucott.store.dto.auth.LoginRequestDTO;
import com.bucott.store.dto.auth.LoginResponseDTO;
import com.bucott.store.dto.auth.RegisterRequestDTO;
import com.bucott.store.dto.auth.RegisterResponseDTO;
import com.bucott.store.dto.user.UserInfoDTO;
import com.bucott.store.exception.auth.InvalidCredentialsException;
import com.bucott.store.exception.general.InvalidInputException;
import com.bucott.store.exception.user.UserNotFoundException;
import com.bucott.store.model.user.Authority;
import com.bucott.store.model.user.Role;
import com.bucott.store.model.user.User;
import com.bucott.store.repository.user.RoleRepository;
import com.bucott.store.repository.user.UserRepository;
import com.bucott.store.util.JwtUtil;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepo, RoleRepository roleRepo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));
        log.info("User found: {}", user);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> role.getAuthority().name())
                        .toArray(String[]::new))
                .build();
    }

    @Override
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        log.info("Attempting to load user by email: {}", email);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));
        log.info("User found: {}", user);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> role.getAuthority().name())
                        .toArray(String[]::new))
                .build();
    }

    @Override
    public UserDetails loadUserByUsernameOrEmail(String identifier) throws UserNotFoundException {
        User user = userRepo.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username or email: " + identifier));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> role.getAuthority().name())
                        .toArray(String[]::new))
                .build();
    }


    @Override
    public LoginResponseDTO authenticate(LoginRequestDTO requestDto) throws UserNotFoundException, InvalidCredentialsException {
        User user = userRepo.findByUsernameOrEmail(requestDto.getUsernameOrEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username or email: "
                                + requestDto.getUsernameOrEmail()));

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
        log.debug("Registering user with username: {} - email {}", requestDto.getUsername(),
                requestDto.getEmail());
        if (userRepo.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new InvalidInputException("Username already exists");
        }
        if (userRepo.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new InvalidInputException("Email already exists");
        }

        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new InvalidInputException("Passwords do not match");
        }
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = new User();

        user.setUsername(requestDto.getUsername());
        user.setEmail(requestDto.getEmail());
        user.setPassword(encodedPassword);
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setPhoneNumber(requestDto.getPhoneNumber());

        Address address = new Address();
        address.setStreetLine1(requestDto.getAddress().getStreetLine1());
        address.setStreetLine2(requestDto.getAddress().getStreetLine2());
        address.setCity(requestDto.getAddress().getCity());
        address.setState(requestDto.getAddress().getState());
        address.setZipCode(requestDto.getAddress().getZipCode());
        address.setCountry(requestDto.getAddress().getCountry());
        address.setAddressType(addressTypeFromString(requestDto.getAddress().getAddressType()));
        user.setAddress(address);

        // Find or create the USER role
        log.info("Looking for role: {}", Authority.ROLE_USER.name());
        Role userRole = roleRepo.findByAuthority(Authority.ROLE_USER);
        if (userRole == null) {
            log.info("Role not found, creating new role");
            userRole = roleRepo.save(new Role(Authority.ROLE_USER));
            log.info("Role created with ID: {}", userRole.getId());
        } else {
            log.info("Found existing role with ID: {}", userRole.getId());
        }

        log.info("Adding role to user");
        user.getRoles().add(userRole);

        log.info("Saving user to database");
        user = userRepo.save(user);
        log.info("User saved successfully with ID: {}, Username: {}", user.getId(), user.getUsername());

        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail());

        return RegisterResponseDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(AddressInfoDTO.builder()
                        .streetLine1(user.getAddress().getStreetLine1())
                        .streetLine2(user.getAddress().getStreetLine2())
                        .city(user.getAddress().getCity())
                        .state(user.getAddress().getState())
                        .zipCode(user.getAddress().getZipCode())
                        .country(user.getAddress().getCountry())
                        .addressType(requestDto.getAddress().getAddressType())
                        .build())
                .build();
    }

    private AddressType addressTypeFromString(String addressType) {
        try {
            return AddressType.valueOf(addressType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid address type: " + addressType);
        }
    }

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

    public UserInfoDTO getUserInfoByUsername(String username) {
        try {
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

            return UserInfoDTO.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .authenticated(true)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching user info for username: {}", username);
            return UserInfoDTO.builder()
                    .authenticated(false)
                    .build();
        }
    }

}
