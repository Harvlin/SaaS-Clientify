package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.domain.entity.Role;
import com.project.SaasCRM.domain.dto.UserDTO;
import com.project.SaasCRM.exception.UserNotFoundException;
import com.project.SaasCRM.exception.InvalidCredentialsException;
import com.project.SaasCRM.repository.UserRepository;
import com.project.SaasCRM.repository.RoleRepository;
import com.project.SaasCRM.security.UserPrincipal;
import com.project.SaasCRM.service.UserService;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User savedUser = userRepository.save(user);
        auditLogService.logUserActivity(savedUser.getId(), "USER_CREATED", "USER", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDTO createUser(String username, String email, String password, String fullName, String phoneNumber, Set<String> roleNames) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .active(true)
                .roles(new HashSet<>())
                .build();
                
        // Assign default role if no roles specified
        if (roleNames == null || roleNames.isEmpty()) {
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new IllegalStateException("Default role not found"));
            user.getRoles().add(userRole);
        } else {
            // Assign specified roles
            Set<Role> roles = roleNames.stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.getRoles().addAll(roles);
        }
        
        User savedUser = userRepository.save(user);
        auditLogService.logUserActivity(savedUser.getId(), "USER_REGISTERED", "USER", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        User existingUser = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        User user = userMapper.toEntity(userDTO);
        user.setPassword(existingUser.getPassword()); // Preserve existing password
        User updatedUser = userRepository.save(user);
        auditLogService.logUserActivity(updatedUser.getId(), "USER_UPDATED", "USER", updatedUser.getId());
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
        auditLogService.logUserActivity(userId, "USER_DELETED", "USER", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.findByUsernameContainingOrEmailContainingOrFullNameContaining(
                searchTerm, searchTerm, searchTerm, pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findUsersByRole(String roleName) {
        return userMapper.toDtoList(userRepository.findByRolesName(roleName));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserDTO addRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        
        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        auditLogService.logUserActivity(userId, "ROLE_ADDED_TO_USER", "USER", userId);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDTO removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        
        user.getRoles().remove(role);
        User updatedUser = userRepository.save(user);
        auditLogService.logUserActivity(userId, "ROLE_REMOVED_FROM_USER", "USER", userId);
        return userMapper.toDto(updatedUser);
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
        auditLogService.logUserActivity(userId, "PASSWORD_CHANGED", "USER", userId);
    }

    @Override
    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        String resetToken = generateResetToken();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        auditLogService.logUserActivity(user.getId(), "PASSWORD_RESET_REQUESTED", "USER", user.getId());
    }

    @Override
    @Transactional
    public void updateLastLogin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTO updateUserStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        user.setActive(active);
        User updatedUser = userRepository.save(user);
        
        String actionType = active ? "USER_ENABLED" : "USER_DISABLED";
        auditLogService.logUserActivity(userId, actionType, "USER", userId);
        
        return userMapper.toDto(updatedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        // Get the currently authenticated user from security context
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
        
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserPrincipal.create(user);
    }

    private String generateResetToken() {
        // Implement token generation logic
        return java.util.UUID.randomUUID().toString();
    }
} 