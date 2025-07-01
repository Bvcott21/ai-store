package com.bucott.store.auth.service;

import com.bucott.store.auth.dto.LoginRequestDTO;
import com.bucott.store.auth.dto.LoginResponseDTO;
import com.bucott.store.auth.dto.RegisterRequestDTO;
import com.bucott.store.auth.dto.RegisterResponseDTO;
import com.bucott.store.user.dto.UserInfoDTO;
import com.bucott.store.auth.exception.InvalidCredentialsException;
import com.bucott.store.common.exception.InvalidInputException;
import com.bucott.store.user.exception.UserNotFoundException;

public interface AuthService {
    
    LoginResponseDTO authenticate(LoginRequestDTO requestDto) throws UserNotFoundException, InvalidCredentialsException;
    
    RegisterResponseDTO register(RegisterRequestDTO requestDto) throws InvalidInputException;
    
    UserInfoDTO getCurrentUser(String token);
    
    boolean validateToken(String token);
    
    UserInfoDTO getUserInfoByUsername(String username);
}