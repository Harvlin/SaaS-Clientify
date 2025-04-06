package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.Role;
import com.project.SaasCRM.domain.dto.RoleDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper extends BaseMapper<Role, RoleDTO> {
    
    @Mapping(target = "users", ignore = true)
    @Override
    Role toEntity(RoleDTO dto);
} 