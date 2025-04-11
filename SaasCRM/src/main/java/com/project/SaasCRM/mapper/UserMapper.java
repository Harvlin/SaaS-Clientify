package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.domain.dto.UserDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    
    @Mapping(target = "id", source = "id")
    // Add other mappings as needed
    UserDTO toDto(User user);

    @Mapping(target = "id", source = "id")
    // Add other mappings as needed
    User toEntity(UserDTO userDTO);
    
    // Add this method to handle lists
    List<UserDTO> toDtoList(List<User> users);
}