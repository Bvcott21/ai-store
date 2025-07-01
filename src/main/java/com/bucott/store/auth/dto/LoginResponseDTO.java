package com.bucott.store.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class LoginResponseDTO {
    private String username;
    private String email;
    private String token;
}
