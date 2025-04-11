package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.*;
import com.project.SaasCRM.exception.InvalidCredentialsException;
import com.project.SaasCRM.exception.InvalidTokenException;
import com.project.SaasCRM.security.JwtTokenProvider;
import com.project.SaasCRM.service.AuthService;
import com.project.SaasCRM.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and account management")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Login user", description = "Authenticate a user and return JWT tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(
            @Parameter(description = "Login credentials") @Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthDTO authDTO = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(authDTO);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Register new user", description = "Register a new user (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Not authorized to register users")
    })
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> register(
            @Parameter(description = "User registration details") @Valid @RequestBody RegisterRequest registerRequest) {
        UserDTO userDTO = userService.createUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getFullName(),
                registerRequest.getPhoneNumber(),
                registerRequest.getRoles()
        );
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Refresh token", description = "Get a new access token using a valid refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token successfully refreshed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthDTO> refreshToken(
            @Parameter(description = "Refresh token request") @Valid @RequestBody TokenRefreshRequest request) {
        try {
            AuthDTO authDTO = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(authDTO);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Get current user info", description = "Returns information about the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user info",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    @GetMapping("/user-info")
    public ResponseEntity<UserDTO> getCurrentUser(
            @Parameter(description = "JWT token in Authorization header") @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            UserDTO userDTO = authService.getUserInfo(token);
            return ResponseEntity.ok(userDTO);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Logout user", description = "Invalidate the current JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully logged out"),
        @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "JWT token in Authorization header") @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            authService.logout(token);
            return ResponseEntity.ok().build();
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @Operation(summary = "Change password", description = "Change password for the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password successfully changed"),
        @ApiResponse(responseCode = "400", description = "Invalid current password"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/password/change")
    public ResponseEntity<com.project.SaasCRM.domain.dto.ApiResponse> changePassword(
            @Parameter(description = "Password change request") @Valid @RequestBody PasswordChangeRequest request,
            @Parameter(description = "Authenticated user") @AuthenticationPrincipal UserDTO userDTO) {
        try {
            authService.changePassword(userDTO.getId(), request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(new com.project.SaasCRM.domain.dto.ApiResponse(true, "Password changed successfully"));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity
                .badRequest()
                .body(new com.project.SaasCRM.domain.dto.ApiResponse(false, "Current password is incorrect"));
        }
    }
    
    @Operation(summary = "Request password reset", description = "Request a password reset by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent if the email exists")
    })
    @PostMapping("/password/reset-request")
    public ResponseEntity<com.project.SaasCRM.domain.dto.ApiResponse> requestPasswordReset(
            @Parameter(description = "Password reset request") @Valid @RequestBody PasswordResetInitRequest request) {
        authService.requestPasswordReset(request.getEmail());
        // Always return 200 even if email doesn't exist to prevent user enumeration
        return ResponseEntity.ok(new com.project.SaasCRM.domain.dto.ApiResponse(true, "If your email is registered, you will receive a password reset link"));
    }
    
    @Operation(summary = "Reset password", description = "Reset password using a valid reset token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successful"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
    })
    @PostMapping("/password/reset")
    public ResponseEntity<com.project.SaasCRM.domain.dto.ApiResponse> finishPasswordReset(
            @Parameter(description = "Password reset details") @Valid @RequestBody PasswordResetRequest request) {
        boolean result = authService.resetPassword(request.getResetToken(), request.getNewPassword());
        
        if (result) {
            return ResponseEntity.ok(new com.project.SaasCRM.domain.dto.ApiResponse(true, "Password has been successfully reset"));
        } else {
            return ResponseEntity
                .badRequest()
                .body(new com.project.SaasCRM.domain.dto.ApiResponse(false, "Invalid or expired password reset token"));
        }
    }
} 