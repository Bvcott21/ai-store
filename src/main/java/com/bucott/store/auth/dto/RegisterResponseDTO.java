package com.bucott.store.auth.dto;

import com.bucott.store.address.dto.AddressInfoDTO;
import lombok.Builder;
import lombok.Data;

public record RegisterResponseDTO (
    String username,
    String email,
    String token,
    String firstName,
    String lastName,
    String phoneNumber,
    AddressInfoDTO address
) {}
