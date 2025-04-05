package com.project.SaasCRM.service;

import java.util.Map;

public interface AuthService {
    String authenticate(String username, String password);

    Map<String, Object> getUserInfo(String token);

    boolean validateToken(String token);

    void logout(String token);

    String refreshToken(String refreshToken);

    void changePassword(Long userId, String currentPassword, String newPassword);

    void requestPasswordReset(String email);

    boolean resetPassword(String resetToken, String newPassword);
}
