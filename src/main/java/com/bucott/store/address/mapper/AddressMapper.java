package com.bucott.store.address.mapper;

import com.bucott.store.address.dto.AddressCreateUpdateRequestDTO;
import com.bucott.store.address.dto.AddressInfoDTO;
import com.bucott.store.address.model.Address;
import com.bucott.store.address.model.AddressType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "addressType", source = "addressType", qualifiedByName = "stringToAddressType")
    @Mapping(target = "addressId", ignore = true)
    Address toEntity(AddressCreateUpdateRequestDTO dto);

    @Mapping(target = "addressType", source = "addressType", qualifiedByName = "addressTypeToString")
    AddressInfoDTO toInfoDTO(Address address);

    @Named("stringToAddressType")
    default AddressType stringToAddressType(String addressType) {
        if (addressType == null) {
            return null;
        }
        try {
            return AddressType.valueOf(addressType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid address type: " + addressType);
        }
    }

    @Named("addressTypeToString")
    default String addressTypeToString(AddressType addressType) {
        return addressType != null ? addressType.name() : null;
    }
}