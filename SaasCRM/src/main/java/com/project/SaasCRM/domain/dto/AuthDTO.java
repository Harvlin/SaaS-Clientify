package com.project.SaasCRM.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    private String token;
    private String refreshToken;
    private LocalDateTime tokenExpiry;
    private LocalDateTime refreshTokenExpiry;
    private UserDTO user;
} 