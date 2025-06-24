package com.bucott.store.mapper;

import com.bucott.store.dto.address.AddressCreateUpdateRequestDTO;
import com.bucott.store.dto.address.AddressInfoDTO;
import com.bucott.store.model.address.Address;
import com.bucott.store.model.address.AddressType;
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