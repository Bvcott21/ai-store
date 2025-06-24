package com.bucott.store.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bucott.store.dto.auth.LoginRequestDTO;
import com.bucott.store.dto.auth.LoginResponseDTO;
import com.bucott.store.dto.auth.RegisterRequestDTO;
import com.bucott.store.dto.auth.RegisterResponseDTO;
import com.bucott.store.exception.auth.InvalidCredentialsException;
import com.bucott.store.exception.general.InvalidInputException;
import com.bucott.store.exception.user.EmailNotFoundException;
import com.bucott.store.exception.user.UserNotFoundException;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    UserDetails loadUserByEmail(String email) throws EmailNotFoundException;
    UserDetails loadUserByUsernameOrEmail(String identifier) throws UserNotFoundException;
    public LoginResponseDTO authenticate(LoginRequestDTO requestDTO) throws UserNotFoundException, InvalidCredentialsException;
    public RegisterResponseDTO register(RegisterRequestDTO requestDTO) throws InvalidInputException;
}
