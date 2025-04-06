package com.project.SaasCRM.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private boolean active;
    private LocalDateTime lastLogin;
    private Set<RoleDTO> roles;
    private Set<Long> assignedCustomerIds;
    private Set<Long> assignedDealIds;
    private Set<Long> assignedTaskIds;
} 