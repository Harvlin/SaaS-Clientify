package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.dto.CustomerDTO;
import com.project.SaasCRM.domain.dto.DealDTO;
import com.project.SaasCRM.domain.dto.EmailCommunicationDTO;
import com.project.SaasCRM.domain.dto.InteractionDTO;
import com.project.SaasCRM.domain.dto.TaskDTO;
import com.project.SaasCRM.domain.dto.UserDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.CustomerService;
import com.project.SaasCRM.service.DealService;
import com.project.SaasCRM.service.EmailCommunicationService;
import com.project.SaasCRM.service.InteractionService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {
    private final CustomerService customerService;
    private final SecurityService securityService;
    private final InteractionService interactionService;
    private final DealService dealService;
    private final TaskService taskService;
    private final EmailCommunicationService emailService;

    @Operation(summary = "Get all customers with pagination", description = "Returns a paginated list of all customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customer list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(customerService.findAllCustomersPaginated(pageable));
    }
    
    @Operation(summary = "Create a new customer", description = "Creates a new customer in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return new ResponseEntity<>(customerService.createCustomer(customerDTO), HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get customer by ID", description = "Returns a customer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customer",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@Parameter(description = "ID of the customer") @PathVariable Long id) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to access this customer");
        }
        
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Update a customer", description = "Updates customer information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "ID of the customer") @PathVariable Long id,
            @Valid @RequestBody CustomerDTO customerDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(customerDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to update this customer");
        }
        
        return ResponseEntity.ok(customerService.updateCustomer(customerDTO));
    }
    
    @Operation(summary = "Delete a customer", description = "Deletes a customer from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Customer successfully deleted"),
        @ApiResponse(responseCode = "403", description = "Not authorized to delete this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteCustomer(@Parameter(description = "ID of the customer") @PathVariable Long id) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to delete this customer");
        }
        
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Search for customers", description = "Search for customers by name, email, or address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<Page<CustomerDTO>> searchCustomers(
            @Parameter(description = "Search query term") @RequestParam String query,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(customerService.searchCustomers(query, pageable));
    }
    
    @Operation(summary = "Get customers by status", description = "Returns customers filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customer list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<CustomerDTO>> getCustomersByStatus(
            @Parameter(description = "Customer status") @PathVariable CustomerStatus status,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(customerService.findCustomersByStatusPaginated(status, pageable));
    }
    
    @Operation(summary = "Get customers assigned to a user", description = "Returns customers assigned to the specified user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customer list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these customers")
    })
    @GetMapping("/assigned/{userId}")
    public ResponseEntity<Page<CustomerDTO>> getCustomersByAssignedUser(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        // Ensure user can only access their own assigned customers unless they're an admin/manager
        if (!securityService.isAdmin() && !securityService.isCurrentUser(userId)) {
            throw new UnauthorizedException("You are not authorized to access these customers");
        }
        
        return ResponseEntity.ok(customerService.findCustomersByAssignedUserPaginated(userId, pageable));
    }
    
    @Operation(summary = "Assign customer to user", description = "Assigns a customer to a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer successfully assigned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to assign this customer"),
        @ApiResponse(responseCode = "404", description = "Customer or user not found")
    })
    @PutMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomerDTO> assignCustomerToUser(
            @Parameter(description = "ID of the customer") @PathVariable Long id,
            @Parameter(description = "ID of the user") @PathVariable Long userId) {
        // Verify the customer exists
        if (!customerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(customerService.assignUserToCustomer(id, userId));
    }
    
    @Operation(summary = "Update customer status", description = "Updates a customer's status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer status successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<CustomerDTO> updateCustomerStatus(
            @Parameter(description = "ID of the customer") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam CustomerStatus status) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to update this customer");
        }
        
        // Verify the customer exists
        if (!customerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(customerService.updateCustomerStatus(id, status));
    }
    
    @Operation(summary = "Get customer interactions", description = "Returns all interactions for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved interactions",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}/interactions")
    public ResponseEntity<List<InteractionDTO>> getCustomerInteractions(
            @Parameter(description = "ID of the customer") @PathVariable Long id) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to access this customer");
        }
        
        // Verify the customer exists
        if (!customerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(interactionService.findInteractionsByCustomer(id));
    }
    
    @Operation(summary = "Get customer deals", description = "Returns all deals for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deals",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}/deals")
    public ResponseEntity<List<DealDTO>> getCustomerDeals(
            @Parameter(description = "ID of the customer") @PathVariable Long id) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to access this customer");
        }
        
        // Verify the customer exists
        if (!customerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(dealService.findDealsByCustomer(id));
    }
    
    @Operation(summary = "Get customer tasks", description = "Returns all tasks for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskDTO>> getCustomerTasks(
            @Parameter(description = "ID of the customer") @PathVariable Long id) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to access this customer");
        }
        
        // Verify the customer exists
        if (!customerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(taskService.findTasksByCustomer(id));
    }
    
    @Operation(summary = "Get customer emails", description = "Returns all emails for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved emails",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}/emails")
    public ResponseEntity<List<EmailCommunicationDTO>> getCustomerEmails(
            @Parameter(description = "ID of the customer") @PathVariable Long id) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to access this customer");
        }
        
        // Verify the customer exists
        if (!customerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(emailService.findEmailsByCustomer(id));
    }
    
    @Operation(summary = "Get recently added customers", description = "Returns a list of recently added customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved recent customers",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/recent")
    public ResponseEntity<List<CustomerDTO>> getRecentCustomers(
            @Parameter(description = "Limit of customers to return") @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(customerService.findRecentCustomers(limit));
    }
    
    @Operation(summary = "Get customer statistics", description = "Returns statistics about customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customer statistics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/stats")
    public ResponseEntity<Map<CustomerStatus, Long>> getCustomerStatistics() {
        return ResponseEntity.ok(customerService.getCustomerStatusCounts());
    }
    
    @Operation(summary = "Get assigned users for a customer", description = "Returns users assigned to a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved assigned users",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Set.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}/assigned-users")
    public ResponseEntity<Set<UserDTO>> getAssignedUsers(
            @Parameter(description = "ID of the customer") @PathVariable Long id) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to access this customer");
        }
        
        // Verify the customer exists
        if (!customerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(customerService.getAssignedUsers(id));
    }
    
    @Operation(summary = "Remove user assignment from customer", description = "Removes a user assignment from a customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User assignment successfully removed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to modify this customer"),
        @ApiResponse(responseCode = "404", description = "Customer or user not found")
    })
    @DeleteMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomerDTO> removeUserFromCustomer(
            @Parameter(description = "ID of the customer") @PathVariable Long id,
            @Parameter(description = "ID of the user") @PathVariable Long userId) {
        // Verify the customer exists
        if (!customerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(customerService.removeUserFromCustomer(id, userId));
    }
} 