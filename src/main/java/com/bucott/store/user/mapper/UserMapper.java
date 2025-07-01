package com.bucott.store.user.mapper;

import com.bucott.store.auth.dto.RegisterRequestDTO;
import com.bucott.store.auth.dto.RegisterResponseDTO;
import com.bucott.store.user.dto.UserInfoDTO;
import com.bucott.store.address.mapper.AddressMapper;
import com.bucott.store.user.model.User;
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
    @Mapping(target = "authorities", ignore = true) // Authorities derived from roles
    @Mapping(target = "address", source = "address")
    User toEntity(RegisterRequestDTO dto);

    @Mapping(target = "token", ignore = true) // Token will be set separately
    @Mapping(target = "address", source = "address")
    RegisterResponseDTO toRegisterResponseDTO(User user);

    @Mapping(target = "authenticated", ignore = true) // Will be set separately
    UserInfoDTO toUserInfoDTO(User user);
}