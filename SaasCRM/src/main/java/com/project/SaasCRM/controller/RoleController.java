package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.RoleDTO;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import com.project.SaasCRM.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for managing user roles")
public class RoleController {
    private final RoleService roleService;

    @Operation(summary = "Get all roles", description = "Returns a list of all roles in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved role list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.findAllRoles());
    }

    @Operation(summary = "Create a new role", description = "Creates a new role in the system (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Role successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid role data"),
        @ApiResponse(responseCode = "403", description = "Not authorized to create roles")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        // Check if a role with the same name already exists
        if (roleService.existsByName(roleDTO.getName())) {
            return ResponseEntity.badRequest().build();
        }
        
        RoleDTO savedRole = roleService.saveRole(roleDTO);
        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
    }

    @Operation(summary = "Get role by ID", description = "Returns a role by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved role",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@Parameter(description = "ID of the role") @PathVariable Long id) {
        return roleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Operation(summary = "Update a role", description = "Updates an existing role (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid role data"),
        @ApiResponse(responseCode = "403", description = "Not authorized to update roles"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDTO> updateRole(
            @Parameter(description = "ID of the role") @PathVariable Long id,
            @Valid @RequestBody RoleDTO roleDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(roleDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if the role exists
        if (!roleService.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        
        // Check if updating the name would conflict with an existing role
        if (roleService.existsByName(roleDTO.getName()) && 
            !roleService.findById(id).get().getName().equals(roleDTO.getName())) {
            return ResponseEntity.badRequest().build();
        }
        
        RoleDTO updatedRole = roleService.updateRole(roleDTO);
        return ResponseEntity.ok(updatedRole);
    }

    @Operation(summary = "Delete a role", description = "Deletes a role from the system (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Role successfully deleted"),
        @ApiResponse(responseCode = "403", description = "Not authorized to delete roles"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@Parameter(description = "ID of the role") @PathVariable Long id) {
        // Check if the role exists
        if (!roleService.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get roles by user", description = "Returns all roles assigned to a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved roles",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoleDTO>> getRolesByUser(
            @Parameter(description = "ID of the user") @PathVariable Long userId) {
        return ResponseEntity.ok(roleService.findRolesByUser(userId));
    }
    
    @Operation(summary = "Get role by name", description = "Returns a role by its name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved role",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<RoleDTO> getRoleByName(
            @Parameter(description = "Name of the role") @PathVariable String name) {
        return roleService.findByName(name)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }
} 