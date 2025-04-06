package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.dto.AuthDTO;
import com.project.SaasCRM.domain.dto.UserDTO;

public interface AuthService {
    AuthDTO authenticate(String username, String password);

    UserDTO getUserInfo(String token);

    boolean validateToken(String token);

    void logout(String token);

    AuthDTO refreshToken(String refreshToken);

    void changePassword(Long userId, String currentPassword, String newPassword);

    void requestPasswordReset(String email);

    boolean resetPassword(String resetToken, String newPassword);
}
