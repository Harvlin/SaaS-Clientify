package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.CustomerDTO;
import com.project.SaasCRM.domain.dto.DealDTO;
import com.project.SaasCRM.domain.dto.TaskDTO;
import com.project.SaasCRM.domain.dto.UserDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.CustomerService;
import com.project.SaasCRM.service.DealService;
import com.project.SaasCRM.service.TaskService;
import com.project.SaasCRM.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    private final UserService userService;
    private final TaskService taskService;
    private final CustomerService customerService;
    private final DealService dealService;
    private final SecurityService securityService;
    
    @Operation(summary = "Get all users with pagination", description = "Returns a paginated list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.findAllUsersPaginated(pageable));
    }
    
    @Operation(summary = "Create a new user", description = "Creates a new user in the system (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to create users")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.saveUser(userDTO), HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get user by ID", description = "Returns a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@Parameter(description = "ID of the user") @PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Update a user", description = "Updates user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this user"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID of the user") @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(userDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        // Only ADMIN or the user themselves should be able to update
        if (!securityService.isAdmin() && !securityService.isCurrentUser(id)) {
            throw new UnauthorizedException("You are not authorized to update this user");
        }
        
        return ResponseEntity.ok(userService.updateUser(userDTO));
    }
    
    @Operation(summary = "Delete a user", description = "Deletes a user from the system (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User successfully deleted"),
        @ApiResponse(responseCode = "403", description = "Not authorized to delete users"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID of the user") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Search for users", description = "Search for users by username, email, or full name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @Parameter(description = "Search query term") @RequestParam String query,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(query, pageable));
    }
    
    @Operation(summary = "Get users by role", description = "Returns a list of all users with the specified role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(
            @Parameter(description = "Name of the role") @PathVariable String roleName) {
        return ResponseEntity.ok(userService.findUsersByRole(roleName));
    }
    
    @Operation(summary = "Assign a role to a user", description = "Assigns a role to a user (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role successfully assigned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to assign roles"),
        @ApiResponse(responseCode = "404", description = "User or role not found")
    })
    @PostMapping("/{id}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> assignRoleToUser(
            @Parameter(description = "ID of the user") @PathVariable Long id,
            @Parameter(description = "ID of the role") @PathVariable Long roleId) {
        return ResponseEntity.ok(userService.addRoleToUser(id, roleId));
    }
    
    @Operation(summary = "Remove a role from a user", description = "Removes a role from a user (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role successfully removed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to remove roles"),
        @ApiResponse(responseCode = "404", description = "User or role not found")
    })
    @DeleteMapping("/{id}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> removeRoleFromUser(
            @Parameter(description = "ID of the user") @PathVariable Long id, 
            @Parameter(description = "ID of the role") @PathVariable Long roleId) {
        return ResponseEntity.ok(userService.removeRoleFromUser(id, roleId));
    }
    
    @Operation(summary = "Get tasks assigned to a user", description = "Returns a list of tasks assigned to the specified user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved task list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these tasks"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskDTO>> getTasksAssignedToUser(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        // Ensure user can only access their own tasks unless they're an admin
        if (!securityService.isAdmin() && !securityService.isCurrentUser(id)) {
            throw new UnauthorizedException("You are not authorized to access these tasks");
        }
        
        return ResponseEntity.ok(taskService.findTasksByAssignee(id));
    }
    
    @Operation(summary = "Get customers assigned to a user", description = "Returns a list of customers assigned to the specified user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customer list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these customers"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}/customers") 
    public ResponseEntity<List<CustomerDTO>> getCustomersAssignedToUser(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        // Ensure user can only access their own customers unless they're an admin
        if (!securityService.isAdmin() && !securityService.isCurrentUser(id)) {
            throw new UnauthorizedException("You are not authorized to access these customers");
        }
        
        return ResponseEntity.ok(customerService.findCustomersByAssignedUser(id));
    }
    
    @Operation(summary = "Get deals assigned to a user", description = "Returns a list of deals assigned to the specified user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these deals"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}/deals")
    public ResponseEntity<List<DealDTO>> getDealsAssignedToUser(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        // Ensure user can only access their own deals unless they're an admin
        if (!securityService.isAdmin() && !securityService.isCurrentUser(id)) {
            throw new UnauthorizedException("You are not authorized to access these deals");
        }
        
        return ResponseEntity.ok(dealService.findDealsByAssignedUser(id));
    }
    
    /**
     * Enable or disable a user account
     */
    @Operation(summary = "Enable or disable user account", description = "Enables or disables a user account (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User account status successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to update user status"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserStatus(
            @Parameter(description = "ID of the user") @PathVariable Long id,
            @Parameter(description = "Active status") @RequestParam boolean active) {
        return ResponseEntity.ok(userService.updateUserStatus(id, active));
    }
    
    /**
     * Get currently authenticated user information
     */
    @Operation(summary = "Get current user", description = "Returns information about the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved current user",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
}