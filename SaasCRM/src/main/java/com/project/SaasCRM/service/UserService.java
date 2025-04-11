package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService extends UserDetailsService {
    UserDTO saveUser(UserDTO user);

    UserDTO createUser(String username, String email, String password, String fullName, String phoneNumber, Set<String> roles);

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
    
    /**
     * Update a user's active status
     *
     * @param userId User ID
     * @param active Active status (true = active, false = disabled)
     * @return Updated user DTO
     */
    UserDTO updateUserStatus(Long userId, boolean active);
    
    /**
     * Get the currently authenticated user
     *
     * @return Current user DTO
     */
    UserDTO getCurrentUser();
}