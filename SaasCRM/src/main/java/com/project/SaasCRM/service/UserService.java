package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    UserDTO saveUser(UserDTO user);

    UserDTO updateUser(UserDTO user);

    void deleteUser(Long userId);

    Optional<UserDTO> findById(Long userId);

    Optional<UserDTO> findByUsername(String username);

    Optional<UserDTO> findByEmail(String email);

    List<UserDTO> findAllUsers();

    Page<UserDTO> findAllUsersPaginated(Pageable pageable);

    Page<UserDTO> searchUsers(String searchTerm, Pageable pageable);

    List<UserDTO> findUsersByRole(String roleName);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserDTO addRoleToUser(Long userId, Long roleId);

    UserDTO removeRoleFromUser(Long userId, Long roleId);

    void changePassword(Long userId, String currentPassword, String newPassword);

    void resetPassword(String email);

    void updateLastLogin(Long userId);
}