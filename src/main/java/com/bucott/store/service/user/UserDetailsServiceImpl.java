package com.bucott.store.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bucott.store.exception.user.UserNotFoundException;
import com.bucott.store.model.user.User;
import com.bucott.store.repository.user.UserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService, UserService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserRepository userRepo;

    public UserDetailsServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
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



}
