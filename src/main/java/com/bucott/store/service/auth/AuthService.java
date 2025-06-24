package com.bucott.store.service.auth;

import com.bucott.store.dto.auth.LoginRequestDTO;
import com.bucott.store.dto.auth.LoginResponseDTO;
import com.bucott.store.dto.auth.RegisterRequestDTO;
import com.bucott.store.dto.auth.RegisterResponseDTO;
import com.bucott.store.dto.user.UserInfoDTO;
import com.bucott.store.exception.auth.InvalidCredentialsException;
import com.bucott.store.exception.general.InvalidInputException;
import com.bucott.store.exception.user.UserNotFoundException;

public interface AuthService {
    
    LoginResponseDTO authenticate(LoginRequestDTO requestDto) throws UserNotFoundException, InvalidCredentialsException;
    
    RegisterResponseDTO register(RegisterRequestDTO requestDto) throws InvalidInputException;
    
    UserInfoDTO getCurrentUser(String token);
    
    boolean validateToken(String token);
    
    UserInfoDTO getUserInfoByUsername(String username);
}