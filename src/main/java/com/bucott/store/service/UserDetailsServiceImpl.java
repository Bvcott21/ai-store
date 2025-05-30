package com.bucott.store.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import com.bucott.store.repository.UserRepository;
import com.bucott.store.util.JwtUtil;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private static final String AUTH_COOKIE_NAME;
    private static final int COOKIE_MAX_AGE;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    public UserDetailsServiceImpl(UserRepository userRepo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    static {
        Dotenv dotenv = Dotenv.load();
        AUTH_COOKIE_NAME = dotenv.get("AUTH_COOKIE_NAME");
        COOKIE_MAX_AGE = Integer.parseInt(dotenv.get("COOKIE_MAX_AGE"));
    }

    @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                log.info("Attempting to load user by username: {}", username);
                User user = userRepo.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with username: " + username));
                log.info("User found: {}", user);

                return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUsername())
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
                                .authorities(user.getRoles().stream()
                                                .map(role -> role.getAuthority().name())
                                                .toArray(String[]::new))
                                .build();
        }


    @Override
        public LoginResponseDTO authenticate(LoginRequestDTO requestDto, HttpServletResponse response) throws UserNotFoundException, InvalidCredentialsException {
                User user = userRepo.findByUsernameOrEmail(requestDto.getUsernameOrEmail())
                                .orElseThrow(() -> new UserNotFoundException(
                                                "User not found with username or email: "
                                                                + requestDto.getUsernameOrEmail()));

                if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
                        throw new InvalidCredentialsException("Invalid password for user: " + requestDto.getUsernameOrEmail());
                }

                String token = jwtUtil.generateToken(user.getUsername(), user.getEmail());
                setAuthCookie(response, token);

                return LoginResponseDTO.builder()
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .token(token)
                                .build();
        }

     @Override
        public RegisterResponseDTO register(RegisterRequestDTO requestDto, HttpServletResponse response) throws InvalidInputException {
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

                user.getRoles().add(new Role(Authority.ROLE_USER));
                user = userRepo.save(user);

                String token = jwtUtil.generateToken(user.getUsername(), user.getEmail());

                setAuthCookie(response, token);

                return RegisterResponseDTO.builder()
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .token(token)
                                .build();
        }

        public void logout(HttpServletResponse response) {
                clearAuthCookie(response);
        }

        public UserInfoDTO getCurrentUser(HttpServletRequest request) {
                String token = extractTokenFromCookie(request.getCookies());
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

        public void setAuthCookie(HttpServletResponse response, String token) {
                Cookie cookie = new Cookie(AUTH_COOKIE_NAME, token);
                cookie.setHttpOnly(true);
                cookie.setSecure(false);
                cookie.setPath("/");
                cookie.setMaxAge(COOKIE_MAX_AGE);

                response.addCookie(cookie);
                log.debug("Authentication cookie cleared");
        }

        private void clearAuthCookie(HttpServletResponse response) {
                Cookie cookie = new Cookie(AUTH_COOKIE_NAME, "");
                cookie.setHttpOnly(true);
                cookie.setSecure(false);
                cookie.setPath("/");
                cookie.setMaxAge(0);

                response.addCookie(cookie);
                log.debug("Authentication cookie cleared");
        }

        public String extractTokenFromCookie(Cookie[] cookies) {
                if (cookies != null) {
                        for (Cookie cookie : cookies) {
                                if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
                                        return cookie.getValue();
                                }
                        }
                }
                return null;
        }

        public boolean validateTokenFromCookie(Cookie[] cookies) {
                String token = extractTokenFromCookie(cookies);
                if (token != null && !token.isEmpty()) {
                        try {
                                String username = jwtUtil.extractUsername(token);
                                return jwtUtil.validateToken(token, username);
                        } catch (Exception e) {
                                log.error("Error validating token from cookie: {}", e.getMessage());
                                return false;
                        }
                }
                return false;
        }
    
}
