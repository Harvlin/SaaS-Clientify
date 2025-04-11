package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.DealStatus;
import com.project.SaasCRM.domain.dto.DealDTO;
import com.project.SaasCRM.domain.dto.TaskDTO;
import com.project.SaasCRM.domain.dto.UserDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.DealService;
import com.project.SaasCRM.service.TaskService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
@Tag(name = "Deal Management", description = "APIs for managing deals")
public class DealController {
    private final DealService dealService;
    private final SecurityService securityService;
    private final TaskService taskService;

    @Operation(summary = "Get all deals with pagination", description = "Returns a paginated list of all deals")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<DealDTO>> getAllDeals(
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(dealService.findAllDealsPaginated(pageable));
    }
    
    @Operation(summary = "Create a new deal", description = "Creates a new deal in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Deal successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<DealDTO> createDeal(@Valid @RequestBody DealDTO dealDTO) {
        return new ResponseEntity<>(dealService.createDeal(dealDTO), HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get deal by ID", description = "Returns a deal by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this deal"),
        @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DealDTO> getDealById(@Parameter(description = "ID of the deal") @PathVariable Long id) {
        // Check if user has access to this deal
        if (!securityService.isAdmin() && !securityService.canAccessDeal(id)) {
            throw new UnauthorizedException("You are not authorized to access this deal");
        }
        
        return dealService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Update a deal", description = "Updates deal information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deal successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this deal"),
        @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DealDTO> updateDeal(
            @Parameter(description = "ID of the deal") @PathVariable Long id,
            @Valid @RequestBody DealDTO dealDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(dealDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if user has access to this deal
        if (!securityService.isAdmin() && !securityService.canAccessDeal(id)) {
            throw new UnauthorizedException("You are not authorized to update this deal");
        }
        
        return ResponseEntity.ok(dealService.updateDeal(dealDTO));
    }
    
    @Operation(summary = "Delete a deal", description = "Deletes a deal from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deal successfully deleted"),
        @ApiResponse(responseCode = "403", description = "Not authorized to delete this deal"),
        @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteDeal(@Parameter(description = "ID of the deal") @PathVariable Long id) {
        // Check if user has access to this deal
        if (!securityService.isAdmin() && !securityService.canAccessDeal(id)) {
            throw new UnauthorizedException("You are not authorized to delete this deal");
        }
        
        dealService.deleteDeal(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Search for deals", description = "Search for deals by name or description")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<Page<DealDTO>> searchDeals(
            @Parameter(description = "Search query term") @RequestParam String query,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(dealService.searchDeals(query, pageable));
    }
    
    @Operation(summary = "Get deals by stage", description = "Returns deals filtered by stage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/stage/{stage}")
    public ResponseEntity<List<DealDTO>> getDealsByStage(
            @Parameter(description = "Deal stage") @PathVariable DealStage stage) {
        return ResponseEntity.ok(dealService.findDealsByStage(stage));
    }
    
    @Operation(summary = "Get deals assigned to a user", description = "Returns deals assigned to the specified user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these deals")
    })
    @GetMapping("/assigned/{userId}")
    public ResponseEntity<Page<DealDTO>> getDealsByAssignedUser(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        // Ensure user can only access their own assigned deals unless they're an admin/manager
        if (!securityService.isAdmin() && !securityService.isCurrentUser(userId)) {
            throw new UnauthorizedException("You are not authorized to access these deals");
        }
        
        return ResponseEntity.ok(dealService.findDealsByAssignedUserPaginated(userId, pageable));
    }
    
    @Operation(summary = "Assign deal to user", description = "Assigns a deal to a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deal successfully assigned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to assign this deal"),
        @ApiResponse(responseCode = "404", description = "Deal or user not found")
    })
    @PutMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DealDTO> assignDealToUser(
            @Parameter(description = "ID of the deal") @PathVariable Long id,
            @Parameter(description = "ID of the user") @PathVariable Long userId) {
        // Verify the deal exists
        if (!dealService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(dealService.assignUserToDeal(id, userId));
    }
    
    @Operation(summary = "Update deal stage", description = "Updates a deal's stage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deal stage successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this deal"),
        @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @PutMapping("/{id}/stage")
    public ResponseEntity<DealDTO> updateDealStage(
            @Parameter(description = "ID of the deal") @PathVariable Long id,
            @Parameter(description = "New stage") @RequestParam DealStage stage) {
        // Check if user has access to this deal
        if (!securityService.isAdmin() && !securityService.canAccessDeal(id)) {
            throw new UnauthorizedException("You are not authorized to update this deal");
        }
        
        // Verify the deal exists
        if (!dealService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(dealService.updateDealStage(id, stage));
    }
    
    @Operation(summary = "Close deal as won", description = "Marks a deal as won")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deal successfully marked as won",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this deal"),
        @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @PutMapping("/{id}/close-won")
    public ResponseEntity<DealDTO> closeDealAsWon(
            @Parameter(description = "ID of the deal") @PathVariable Long id,
            @Parameter(description = "Close date") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime closeDate) {
        // Check if user has access to this deal
        if (!securityService.isAdmin() && !securityService.canAccessDeal(id)) {
            throw new UnauthorizedException("You are not authorized to update this deal");
        }
        
        // Verify the deal exists
        if (!dealService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Use current date time if not provided
        LocalDateTime actualCloseDate = closeDate != null ? closeDate : LocalDateTime.now();
        
        return ResponseEntity.ok(dealService.closeDealAsWon(id, actualCloseDate));
    }
    
    @Operation(summary = "Close deal as lost", description = "Marks a deal as lost")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deal successfully marked as lost",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this deal"),
        @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @PutMapping("/{id}/close-lost")
    public ResponseEntity<DealDTO> closeDealAsLost(
            @Parameter(description = "ID of the deal") @PathVariable Long id,
            @Parameter(description = "Reason for loss") @RequestParam(required = false) String reason,
            @Parameter(description = "Close date") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime closeDate) {
        // Check if user has access to this deal
        if (!securityService.isAdmin() && !securityService.canAccessDeal(id)) {
            throw new UnauthorizedException("You are not authorized to update this deal");
        }
        
        // Verify the deal exists
        if (!dealService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Use current date time if not provided
        LocalDateTime actualCloseDate = closeDate != null ? closeDate : LocalDateTime.now();
        
        return ResponseEntity.ok(dealService.closeDealAsLost(id, actualCloseDate, reason));
    }
    
    @Operation(summary = "Get deal statistics", description = "Returns statistics about deals by stage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal statistics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/stats/by-stage")
    public ResponseEntity<Map<DealStage, Long>> getDealStatisticsByStage() {
        return ResponseEntity.ok(dealService.getDealCountsByStage());
    }
    
    @Operation(summary = "Get deal value statistics", description = "Returns statistics about deal values by stage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal value statistics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/stats/values-by-stage")
    public ResponseEntity<Map<DealStage, BigDecimal>> getDealValueStatisticsByStage() {
        return ResponseEntity.ok(dealService.getDealValuesByStage());
    }
    
    @Operation(summary = "Get assigned users for a deal", description = "Returns users assigned to a specific deal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved assigned users",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Set.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this deal"),
        @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @GetMapping("/{id}/assigned-users")
    public ResponseEntity<Set<UserDTO>> getAssignedUsers(
            @Parameter(description = "ID of the deal") @PathVariable Long id) {
        // Check if user has access to this deal
        if (!securityService.isAdmin() && !securityService.canAccessDeal(id)) {
            throw new UnauthorizedException("You are not authorized to access this deal");
        }
        
        // Verify the deal exists
        if (!dealService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(dealService.getAssignedUsers(id));
    }
    
    @Operation(summary = "Remove user assignment from deal", description = "Removes a user assignment from a deal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User assignment successfully removed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to modify this deal"),
        @ApiResponse(responseCode = "404", description = "Deal or user not found")
    })
    @DeleteMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DealDTO> removeUserFromDeal(
            @Parameter(description = "ID of the deal") @PathVariable Long id,
            @Parameter(description = "ID of the user") @PathVariable Long userId) {
        // Verify the deal exists
        if (!dealService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(dealService.removeUserFromDeal(id, userId));
    }
    
    @Operation(summary = "Get deals by date range", description = "Returns deals with expected close date in the specified range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deals",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/by-date-range")
    public ResponseEntity<List<DealDTO>> getDealsByDateRange(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(dealService.findDealsByExpectedCloseDateRange(startDate, endDate));
    }
    
    @Operation(summary = "Get recent deals", description = "Returns a list of recently added deals")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved recent deals",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/recent")
    public ResponseEntity<List<DealDTO>> getRecentDeals(
            @Parameter(description = "Limit of deals to return") @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(dealService.findRecentDeals(limit));
    }

    @Operation(summary = "Get deals by customer", description = "Returns deals for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deals",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<DealDTO>> getDealsByCustomer(
            @Parameter(description = "ID of the customer") @PathVariable Long id) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to access this customer's deals");
        }
        
        return ResponseEntity.ok(dealService.findDealsByCustomer(id));
    }

    @Operation(summary = "Get deals by status", description = "Returns deals filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deals",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DealDTO>> getDealsByStatus(
            @Parameter(description = "Deal status") @PathVariable DealStatus status) {
        // This endpoint would require adding a method to DealService to filter deals by status
        // For now, let's assume we have that method
        // return ResponseEntity.ok(dealService.findDealsByStatus(status));
        
        // As a fallback, we can return an empty list or a not implemented error
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Operation(summary = "Update deal status", description = "Updates a deal's status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deal status successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this deal"),
        @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<DealDTO> updateDealStatus(
            @Parameter(description = "ID of the deal") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam DealStatus status) {
        // Check if user has access to this deal
        if (!securityService.isAdmin() && !securityService.canAccessDeal(id)) {
            throw new UnauthorizedException("You are not authorized to update this deal");
        }
        
        // Verify the deal exists
        if (!dealService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // This endpoint would require adding a method to DealService to update deal status
        // For now, let's assume we have that method
        // return ResponseEntity.ok(dealService.updateDealStatus(id, status));
        
        // As a fallback, we can return a not implemented error
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    @Operation(summary = "Get tasks related to deal", description = "Returns tasks related to a specific deal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this deal"),
        @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskDTO>> getTasksByDeal(
            @Parameter(description = "ID of the deal") @PathVariable Long id) {
        // Check if user has access to this deal
        if (!securityService.isAdmin() && !securityService.canAccessDeal(id)) {
            throw new UnauthorizedException("You are not authorized to access this deal");
        }
        
        // Verify the deal exists
        if (!dealService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(taskService.findTasksByDeal(id));
    }
    
    @Operation(summary = "Create task for deal", description = "Creates a new task for a specific deal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this deal"),
        @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @PostMapping("/{id}/tasks")
    public ResponseEntity<TaskDTO> createTaskForDeal(
            @Parameter(description = "ID of the deal") @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        // Check if user has access to this deal
        if (!securityService.isAdmin() && !securityService.canAccessDeal(id)) {
            throw new UnauthorizedException("You are not authorized to access this deal");
        }
        
        // Verify the deal exists
        if (!dealService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Set the deal ID on the task
        taskDTO.setDealId(id);
        
        return new ResponseEntity<>(taskService.createTask(taskDTO), HttpStatus.CREATED);
    }
} 