package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.exception.TokenRefreshException;
import com.project.SaasCRM.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset.token.expiration-minutes}")
    private int tokenExpirationMinutes;

    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        String token = generateResetToken();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(tokenExpirationMinutes);

        user.setResetToken(token);
        user.setResetTokenExpiry(expiryTime);
        userRepository.save(user);

        // Send password reset email
        String resetLink = createPasswordResetLink(token);
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    @Transactional
    public void completePasswordReset(String token, String newPassword) {
        User user = userRepository.findByResetTokenAndResetTokenExpiryAfter(token, LocalDateTime.now())
                .orElseThrow(() -> new TokenRefreshException("Invalid or expired password reset token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    private String createPasswordResetLink(String token) {
        // TODO: Replace with actual frontend URL from configuration
        return "https://your-frontend-url/reset-password?token=" + token;
    }
} 