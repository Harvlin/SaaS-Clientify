package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.entity.Role;
import com.project.SaasCRM.domain.dto.RoleDTO;
import com.project.SaasCRM.exception.RoleNotFoundException;
import com.project.SaasCRM.repository.RoleRepository;
import com.project.SaasCRM.service.RoleService;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final AuditLogService auditLogService;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleDTO saveRole(RoleDTO roleDTO) {
        if (roleRepository.existsByName(roleDTO.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }
        Role role = roleMapper.toEntity(roleDTO);
        Role savedRole = roleRepository.save(role);
        auditLogService.logSystemActivity("ROLE_CREATED", "ROLE", savedRole.getId());
        return roleMapper.toDto(savedRole);
    }

    @Override
    @Transactional
    public RoleDTO updateRole(RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(roleDTO.getId())
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        if (!existingRole.getName().equals(roleDTO.getName()) && 
            roleRepository.existsByName(roleDTO.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }

        Role role = roleMapper.toEntity(roleDTO);
        Role updatedRole = roleRepository.save(role);
        auditLogService.logSystemActivity("ROLE_UPDATED", "ROLE", updatedRole.getId());
        return roleMapper.toDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        roleRepository.delete(role);
        auditLogService.logSystemActivity("ROLE_DELETED", "ROLE", roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleDTO> findById(Long roleId) {
        return roleRepository.findById(roleId)
                .map(roleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleDTO> findByName(String name) {
        return roleRepository.findByName(name)
                .map(roleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> findAllRoles() {
        return roleMapper.toDtoList(roleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleDTO> findAllRolesPaginated(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(roleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> findRolesByUser(Long userId) {
        return roleMapper.toDtoList(roleRepository.findByUsersId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
} 