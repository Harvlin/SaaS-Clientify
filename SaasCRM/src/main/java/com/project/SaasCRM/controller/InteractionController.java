package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.InteractionType;
import com.project.SaasCRM.domain.dto.InteractionDTO;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.InteractionService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
@Tag(name = "Interaction Management", description = "APIs for managing customer interactions")
public class InteractionController {
    private final InteractionService interactionService;
    private final SecurityService securityService;

    @Operation(summary = "Get all interactions with pagination", description = "Returns a paginated list of all interactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved interaction list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<InteractionDTO>> getAllInteractions(
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(interactionService.findAllInteractionsPaginated(pageable));
    }

    @Operation(summary = "Create a new interaction", description = "Creates a new interaction in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Interaction successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InteractionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<InteractionDTO> createInteraction(@Valid @RequestBody InteractionDTO interactionDTO) {
        return new ResponseEntity<>(interactionService.saveInteraction(interactionDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get interaction by ID", description = "Returns an interaction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved interaction",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InteractionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Interaction not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InteractionDTO> getInteractionById(@Parameter(description = "ID of the interaction") @PathVariable Long id) {
        return interactionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update an interaction", description = "Updates interaction information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interaction successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InteractionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Interaction not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<InteractionDTO> updateInteraction(
            @Parameter(description = "ID of the interaction") @PathVariable Long id,
            @Valid @RequestBody InteractionDTO interactionDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(interactionDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(interactionService.updateInteraction(interactionDTO));
    }

    @Operation(summary = "Delete an interaction", description = "Deletes an interaction from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Interaction successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Interaction not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInteraction(@Parameter(description = "ID of the interaction") @PathVariable Long id) {
        interactionService.deleteInteraction(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get interactions by customer", description = "Returns interactions for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved interactions",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/customer/{id}")
    public ResponseEntity<Page<InteractionDTO>> getInteractionsByCustomer(
            @Parameter(description = "ID of the customer") @PathVariable Long id,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        // Check if user has access to this customer (implement this logic based on your security requirements)
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to access this customer's interactions");
        }
        
        return ResponseEntity.ok(interactionService.findInteractionsByCustomerPaginated(id, pageable));
    }

    @Operation(summary = "Get interactions by user", description = "Returns interactions created by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved interactions",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these interactions")
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<List<InteractionDTO>> getInteractionsByUser(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        // Ensure user can only access their own interactions unless they're an admin
        if (!securityService.isAdmin() && !securityService.isCurrentUser(id)) {
            throw new UnauthorizedException("You are not authorized to access these interactions");
        }
        
        return ResponseEntity.ok(interactionService.findInteractionsByUser(id));
    }

    @Operation(summary = "Get interactions by type", description = "Returns interactions filtered by type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved interactions",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<List<InteractionDTO>> getInteractionsByType(
            @Parameter(description = "Interaction type") @PathVariable InteractionType type) {
        return ResponseEntity.ok(interactionService.findInteractionsByType(type));
    }

    @Operation(summary = "Get recent interactions", description = "Returns a list of recently added interactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved recent interactions",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/recent")
    public ResponseEntity<List<InteractionDTO>> getRecentInteractions(
            @Parameter(description = "Limit of interactions to return") @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(interactionService.findRecentInteractions(limit));
    }

    @Operation(summary = "Get interaction statistics by type", description = "Returns statistics about interactions by type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved interaction statistics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/stats/by-type")
    public ResponseEntity<Map<InteractionType, Long>> getInteractionStatsByType(
            @Parameter(description = "ID of the customer") @RequestParam Long customerId) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(customerId)) {
            throw new UnauthorizedException("You are not authorized to access this customer's interaction statistics");
        }
        
        return ResponseEntity.ok(interactionService.getInteractionTypeCounts(customerId));
    }

    @Operation(summary = "Get interaction statistics by date", description = "Returns statistics about interactions by date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved interaction statistics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/stats/by-date")
    public ResponseEntity<Map<String, Long>> getInteractionStatsByDate(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(interactionService.getInteractionCountsByDate(startDate, endDate));
    }

    @Operation(summary = "Get interactions by date range", description = "Returns interactions created within the specified date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved interactions",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/by-date-range")
    public ResponseEntity<List<InteractionDTO>> getInteractionsByDateRange(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(interactionService.findInteractionsByDateRange(startDate, endDate));
    }
} 