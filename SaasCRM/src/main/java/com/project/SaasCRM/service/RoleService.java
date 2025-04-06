package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.dto.RoleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    RoleDTO saveRole(RoleDTO role);

    RoleDTO updateRole(RoleDTO role);

    void deleteRole(Long roleId);

    Optional<RoleDTO> findById(Long roleId);

    Optional<RoleDTO> findByName(String name);

    List<RoleDTO> findAllRoles();

    Page<RoleDTO> findAllRolesPaginated(Pageable pageable);

    List<RoleDTO> findRolesByUser(Long userId);

    boolean existsByName(String name);
}
