package com.bucott.store.address.dto;

import lombok.Builder;
import lombok.Data;

public record AddressInfoDTO (
    String streetLine1,
    String streetLine2,
    String city,
    String state,
    String zipCode,
    String country,
    String addressType // e.g., HOME, WORK, BILLING, SHIPPING
) {

}
