package com.bucott.store.dto.address;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AddressInfoDTO {
    private String streetLine1;
    private String streetLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String addressType; // e.g., HOME, WORK, BILLING, SHIPPING
}
