package com.bucott.store.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RegisterRequestDTO {
    private String email;
    private String username;
    private String password;
    private String confirmPassword; 
}