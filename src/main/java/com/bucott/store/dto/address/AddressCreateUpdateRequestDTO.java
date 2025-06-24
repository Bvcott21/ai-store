package com.bucott.store.dto.address;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AddressCreateUpdateRequestDTO {
    
    private Long addressId; // Optional for updates
    
    @NotBlank(message = "Street address is required")
    @Size(min = 5, max = 100, message = "Street address must be between 5 and 100 characters")
    private String streetLine1;
    
    @Size(max = 100, message = "Secondary address line must not exceed 100 characters")
    private String streetLine2;
    
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "City can only contain letters, spaces, hyphens, and apostrophes")
    private String city;
    
    @NotBlank(message = "State is required")
    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "State can only contain letters, spaces, hyphens, and apostrophes")
    private String state;
    
    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$", message = "ZIP code must be in format 12345 or 12345-6789")
    private String zipCode;
    
    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "Country can only contain letters, spaces, hyphens, and apostrophes")
    private String country;
    
    @NotBlank(message = "Address type is required")
    @Pattern(regexp = "^(HOME|WORK|BILLING|SHIPPING)$", message = "Address type must be one of: HOME, WORK, BILLING, SHIPPING")
    private String addressType;
}
