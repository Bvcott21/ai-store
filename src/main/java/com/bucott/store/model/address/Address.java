package com.bucott.store.model.address;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity @Data
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotNull
    private String streetLine1;
    private String streetLine2;

    @NotNull
    private String city;

    @NotNull
    private String state;

    @NotNull
    private String zipCode;

    @NotNull
    private String country;
}
