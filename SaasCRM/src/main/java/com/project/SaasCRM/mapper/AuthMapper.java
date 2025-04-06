package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.dto.AuthDTO;
import com.project.SaasCRM.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(uses = UserMapper.class)
public interface AuthMapper {
    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    @Mapping(target = "user", source = "user")
    AuthDTO toDto(User user, String token, String refreshToken, 
                 LocalDateTime tokenExpiry, LocalDateTime refreshTokenExpiry);
} 