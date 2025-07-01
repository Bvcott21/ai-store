package com.bucott.store.auth.dto;

import com.bucott.store.address.dto.AddressInfoDTO;
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
