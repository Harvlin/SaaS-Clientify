package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.RoleDTO;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import com.project.SaasCRM.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;
    
    @InjectMocks
    private RoleController roleController;
    
    private RoleDTO testRole;
    private List<RoleDTO> roleList;
    
    @BeforeEach
    void setUp() {
        testRole = new RoleDTO();
        testRole.setId(1L);
        testRole.setName("TEST_ROLE");
        testRole.setDescription("Test role description");
        testRole.setPermissions(new HashSet<>());
        
        roleList = new ArrayList<>();
        roleList.add(testRole);
    }
    
    @Test
    void getAllRoles_ShouldReturnAllRoles() {
        when(roleService.findAllRoles()).thenReturn(roleList);
        
        ResponseEntity<List<RoleDTO>> response = roleController.getAllRoles();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleList, response.getBody());
        verify(roleService).findAllRoles();
    }
    
    @Test
    void createRole_WithValidData_ShouldCreateRole() {
        when(roleService.existsByName(anyString())).thenReturn(false);
        when(roleService.saveRole(any(RoleDTO.class))).thenReturn(testRole);
        
        ResponseEntity<RoleDTO> response = roleController.createRole(testRole);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testRole, response.getBody());
        verify(roleService).existsByName(testRole.getName());
        verify(roleService).saveRole(testRole);
    }
    
    @Test
    void createRole_WithExistingName_ShouldReturnBadRequest() {
        when(roleService.existsByName(anyString())).thenReturn(true);
        
        ResponseEntity<RoleDTO> response = roleController.createRole(testRole);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(roleService).existsByName(testRole.getName());
        verify(roleService, never()).saveRole(any(RoleDTO.class));
    }
    
    @Test
    void getRoleById_WhenRoleExists_ShouldReturnRole() {
        when(roleService.findById(1L)).thenReturn(Optional.of(testRole));
        
        ResponseEntity<RoleDTO> response = roleController.getRoleById(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testRole, response.getBody());
        verify(roleService).findById(1L);
    }
    
    @Test
    void getRoleById_WhenRoleDoesNotExist_ShouldThrowException() {
        when(roleService.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> roleController.getRoleById(99L));
        
        verify(roleService).findById(99L);
    }
    
    @Test
    void updateRole_WhenRoleExists_ShouldUpdateRole() {
        when(roleService.findById(1L)).thenReturn(Optional.of(testRole));
        when(roleService.updateRole(any(RoleDTO.class))).thenReturn(testRole);
        
        ResponseEntity<RoleDTO> response = roleController.updateRole(1L, testRole);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testRole, response.getBody());
        verify(roleService).findById(1L);
        verify(roleService).updateRole(testRole);
    }
    
    @Test
    void updateRole_WithMismatchedIds_ShouldReturnBadRequest() {
        RoleDTO differentRole = new RoleDTO();
        differentRole.setId(2L);
        
        ResponseEntity<RoleDTO> response = roleController.updateRole(1L, differentRole);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(roleService, never()).findById(anyLong());
        verify(roleService, never()).updateRole(any(RoleDTO.class));
    }
    
    @Test
    void updateRole_WithNameConflict_ShouldReturnBadRequest() {
        RoleDTO existingNameRole = new RoleDTO();
        existingNameRole.setId(1L);
        existingNameRole.setName("EXISTING_ROLE");
        
        when(roleService.findById(1L)).thenReturn(Optional.of(testRole));
        when(roleService.existsByName("EXISTING_ROLE")).thenReturn(true);
        
        ResponseEntity<RoleDTO> response = roleController.updateRole(1L, existingNameRole);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(roleService).findById(1L);
        verify(roleService).existsByName("EXISTING_ROLE");
        verify(roleService, never()).updateRole(any(RoleDTO.class));
    }
    
    @Test
    void deleteRole_WhenRoleExists_ShouldDeleteRole() {
        when(roleService.findById(1L)).thenReturn(Optional.of(testRole));
        doNothing().when(roleService).deleteRole(1L);
        
        ResponseEntity<Void> response = roleController.deleteRole(1L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roleService).findById(1L);
        verify(roleService).deleteRole(1L);
    }
    
    @Test
    void deleteRole_WhenRoleDoesNotExist_ShouldThrowException() {
        when(roleService.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> roleController.deleteRole(99L));
        
        verify(roleService).findById(99L);
        verify(roleService, never()).deleteRole(anyLong());
    }
    
    @Test
    void getRolesByUser_ShouldReturnRolesForUser() {
        when(roleService.findRolesByUser(1L)).thenReturn(roleList);
        
        ResponseEntity<List<RoleDTO>> response = roleController.getRolesByUser(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleList, response.getBody());
        verify(roleService).findRolesByUser(1L);
    }
    
    @Test
    void getRoleByName_WhenRoleExists_ShouldReturnRole() {
        when(roleService.findByName("TEST_ROLE")).thenReturn(Optional.of(testRole));
        
        ResponseEntity<RoleDTO> response = roleController.getRoleByName("TEST_ROLE");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testRole, response.getBody());
        verify(roleService).findByName("TEST_ROLE");
    }
    
    @Test
    void getRoleByName_WhenRoleDoesNotExist_ShouldThrowException() {
        when(roleService.findByName("NONEXISTENT_ROLE")).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> roleController.getRoleByName("NONEXISTENT_ROLE"));
        
        verify(roleService).findByName("NONEXISTENT_ROLE");
    }
} 