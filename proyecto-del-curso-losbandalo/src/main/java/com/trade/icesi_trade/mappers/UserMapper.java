// This code snippet is defining a Java interface named `UserMapper` that uses MapStruct for mapping
// between entity objects and DTOs (Data Transfer Objects) in a Spring application. Here's a breakdown
// of what the code is doing:
package com.trade.icesi_trade.mappers;

import com.trade.icesi_trade.dtos.UserResponseDto;
import com.trade.icesi_trade.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "email", source = "email"),
        @Mapping(target = "name", source = "name"),
        @Mapping(target = "phone", source = "phone")
    })
    UserResponseDto entityToDto(User user);

    @InheritInverseConfiguration
    @Mapping(target = "password", ignore = true)    
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User dtoToEntity(UserResponseDto dto);

}