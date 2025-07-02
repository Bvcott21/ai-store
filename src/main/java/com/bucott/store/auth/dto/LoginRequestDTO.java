package com.bucott.store.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

public record LoginRequestDTO (
    @NotBlank(message = "Username or email is required")
    @Size(min = 3, max = 100, message = "Username or email must be between 3 and 100 characters")
    String usernameOrEmail,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    String password
) { }
