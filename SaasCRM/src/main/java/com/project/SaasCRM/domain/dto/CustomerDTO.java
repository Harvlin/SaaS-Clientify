package com.project.SaasCRM.domain.dto;

import com.project.SaasCRM.domain.CustomerStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private CustomerStatus status;
    private String address;
    private String notes;
    private LocalDateTime lastContact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Long> assignedUserIds;
    private Set<Long> dealIds;
} 