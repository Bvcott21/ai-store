package com.bucott.store.auth.dto;

public record LoginResponseDTO (
    String username,
    String email,
    String token
) {}
