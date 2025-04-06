package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.dto.AuthDTO;
import com.project.SaasCRM.domain.dto.UserDTO;
import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.exception.InvalidCredentialsException;
import com.project.SaasCRM.exception.InvalidTokenException;
import com.project.SaasCRM.exception.TokenRefreshException;
import com.project.SaasCRM.exception.UserNotFoundException;
import com.project.SaasCRM.mapper.AuthMapper;
import com.project.SaasCRM.mapper.UserMapper;
import com.project.SaasCRM.repository.UserRepository;
import com.project.SaasCRM.security.JwtTokenProvider;
import com.project.SaasCRM.security.LoginAttemptService;
import com.project.SaasCRM.security.TokenBlacklist;
import com.project.SaasCRM.service.AuthService;
import com.project.SaasCRM.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final TokenBlacklist tokenBlacklist;
    private final AuditLogService auditLogService;
    private final HttpServletRequest request;
    private final AuthMapper authMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthDTO authenticate(String username, String password) {
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            throw new InvalidCredentialsException("Too many failed login attempts. Please try again later.");
        }

        try {
            Authentication initialAuth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

            List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);
            LocalDateTime tokenExpiry = LocalDateTime.now().plus(tokenProvider.getTokenExpirationInMinutes(), ChronoUnit.MINUTES);
            LocalDateTime refreshTokenExpiry = LocalDateTime.now().plus(tokenProvider.getRefreshTokenExpirationInDays(), ChronoUnit.DAYS);
            
            loginAttemptService.loginSucceeded(ip);
            auditLogService.logUserActivity(user.getId(), "LOGIN", "USER", user.getId());
            
            return authMapper.toDto(user, token, refreshToken, tokenExpiry, refreshTokenExpiry);
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(getClientIP());
            auditLogService.logSystemActivity("LOGIN_FAILED", "AUTH", null);
            throw new InvalidCredentialsException("Invalid username or password");
        } catch (Exception e) {
            auditLogService.logSystemActivity("LOGIN_ERROR", "AUTH", null);
            throw new RuntimeException("An error occurred during authentication", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserInfo(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new InvalidCredentialsException("Invalid token");
        }

        String username = tokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toDto(user);
    }

    @Override
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    @Override
    @Transactional
    public void logout(String token) {
        if (token != null && validateToken(token)) {
            String username = tokenProvider.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

            tokenBlacklist.blacklist(token);
            auditLogService.logUserActivity(user.getId(), "LOGOUT", "USER", user.getId());
        }
    }

    @Override
    @Transactional
    public AuthDTO refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new TokenRefreshException("Invalid refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (tokenBlacklist.isBlacklisted(refreshToken)) {
            throw new InvalidTokenException("Refresh token has been invalidated");
        }

        String newToken = tokenProvider.generateToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);
        LocalDateTime tokenExpiry = LocalDateTime.now().plus(tokenProvider.getTokenExpirationInMinutes(), ChronoUnit.MINUTES);
        LocalDateTime refreshTokenExpiry = LocalDateTime.now().plus(tokenProvider.getRefreshTokenExpirationInDays(), ChronoUnit.DAYS);

        auditLogService.logUserActivity(user.getId(), "TOKEN_REFRESH", "AUTH", user.getId());
        
        return authMapper.toDto(user, newToken, newRefreshToken, tokenExpiry, refreshTokenExpiry);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        auditLogService.logUserActivity(userId, "PASSWORD_CHANGE", "USER", userId);
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        String resetToken = tokenProvider.generatePasswordResetToken(user);
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // TODO: Send password reset email with token
        auditLogService.logUserActivity(user.getId(), "PASSWORD_RESET_REQUEST", "USER", user.getId());
    }

    @Override
    @Transactional
    public boolean resetPassword(String resetToken, String newPassword) {
        String username = tokenProvider.getUsernameFromToken(resetToken);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        auditLogService.logUserActivity(user.getId(), "PASSWORD_RESET", "USER", user.getId());
        return true;
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
} 