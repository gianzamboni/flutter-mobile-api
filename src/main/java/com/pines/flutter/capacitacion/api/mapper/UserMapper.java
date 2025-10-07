package com.pines.flutter.capacitacion.api.mapper;

import org.mapstruct.Mapper;

import com.pines.flutter.capacitacion.api.dto.UserDTO;
import com.pines.flutter.capacitacion.api.model.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
}