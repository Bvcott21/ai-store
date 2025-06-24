package com.bucott.store.dto.auth;

import com.bucott.store.dto.address.AddressCreateUpdateRequestDTO;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RegisterRequestDTO {
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private AddressCreateUpdateRequestDTO address;
    private String phoneNumber;
}