package com.project.SaasCRM.domain.dto;

import lombok.Data;
import java.util.Set;

@Data
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private Set<String> permissions;
} 