package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);

    Optional<User> findById(Long userId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllUsers();

    Page<User> findAllUsersPaginated(Pageable pageable);

    Page<User> searchUsers(String searchTerm, Pageable pageable);

    List<User> findUsersByRole(String roleName);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User addRoleToUser(Long userId, Long roleId);

    User removeRoleFromUser(Long userId, Long roleId);

    void changePassword(Long userId, String currentPassword, String newPassword);

    void resetPassword(String email);

    void updateLastLogin(Long userId);
}