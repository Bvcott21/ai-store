package com.bucott.store.mapper;

import com.bucott.store.dto.auth.RegisterRequestDTO;
import com.bucott.store.dto.auth.RegisterResponseDTO;
import com.bucott.store.dto.user.UserInfoDTO;
import com.bucott.store.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Password will be encoded separately
    @Mapping(target = "expired", constant = "false")
    @Mapping(target = "locked", constant = "false")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "roles", ignore = true) // Roles will be set separately
    @Mapping(target = "address", source = "address")
    User toEntity(RegisterRequestDTO dto);

    @Mapping(target = "token", ignore = true) // Token will be set separately
    @Mapping(target = "address", source = "address")
    RegisterResponseDTO toRegisterResponseDTO(User user);

    UserInfoDTO toUserInfoDTO(User user);
}