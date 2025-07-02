package com.bucott.store.user.dto;

public record UserInfoDTO (
    String username,
    String email,
    boolean authenticated
) { }
