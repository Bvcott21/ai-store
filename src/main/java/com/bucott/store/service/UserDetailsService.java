package com.bucott.store.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bucott.store.exception.auth.InvalidCredentialsException;
import com.bucott.store.exception.general.InvalidInputException;
import com.bucott.store.exception.user.EmailNotFoundException;
import com.bucott.store.exception.user.UserNotFoundException;

import jakarta.servlet.http.HttpServletResponse;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    UserDetails loadUserByEmail(String email) throws EmailNotFoundException;
    UserDetails loadUserByUsernameOrEmail(String identifier) throws UserNotFoundException;
    //public LoginResponseDTO authenticate(LoginRequestDTO requestDTO, HttpServletResponse response) throws UserNotFoundException, InvalidCredentialsException;
    //public RegisterResponseDTO register(RegisterRequestDTO requestDTO, HttpServletResponse response) throws InvalidInputException;
}
