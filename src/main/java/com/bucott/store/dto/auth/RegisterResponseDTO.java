package com.bucott.store.dto.auth;

import com.bucott.store.dto.address.AddressInfoDTO;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RegisterResponseDTO {
    private String username;
    private String email;
    private String token;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private AddressInfoDTO address;
}
