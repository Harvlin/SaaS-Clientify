package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role saveRole(Role role);

    Role updateRole(Role role);

    void deleteRole(Long roleId);

    Optional<Role> findById(Long roleId);

    Optional<Role> findByName(String name);

    List<Role> findAllRoles();
}
