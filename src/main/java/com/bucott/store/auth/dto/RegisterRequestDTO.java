package com.bucott.store.auth.dto;

import com.bucott.store.address.dto.AddressCreateUpdateRequestDTO;
import jakarta.validation.constraints.*;

public record RegisterRequestDTO(
        @Email
        String email,
        @NotNull @NotEmpty
        String username,
        @NotNull @Size(min=2, max=50)
        String firstName,
        @NotNull @Size(min=2, max=50)
        String lastName,
        @NotNull @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,
        @NotNull @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String confirmPassword,
        @Size(min=10, max=15) @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits long and can start with a '+'")
        String phoneNumber,
        AddressCreateUpdateRequestDTO address
) { }
