package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.TaskPriority;
import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.TaskType;
import com.project.SaasCRM.domain.dto.TaskDTO;
import com.project.SaasCRM.exception.TaskNotFoundException;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {
    private final TaskService taskService;
    private final SecurityService securityService;

    @Operation(summary = "Get all tasks with pagination", description = "Returns a paginated list of all tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved task list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<TaskDTO>> getAllTasks(
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(taskService.findAllTasksPaginated(pageable));
    }

    @Operation(summary = "Create a new task", description = "Creates a new task in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        return new ResponseEntity<>(taskService.createTask(taskDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get task by ID", description = "Returns a task by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved task",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@Parameter(description = "ID of the task") @PathVariable Long id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a task", description = "Updates task information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @Parameter(description = "ID of the task") @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(taskDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(taskService.updateTask(taskDTO));
    }

    @Operation(summary = "Delete a task", description = "Deletes a task from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "ID of the task") @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get tasks by assigned user", description = "Returns tasks assigned to a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these tasks")
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<List<TaskDTO>> getTasksByAssignedUser(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        // Ensure user can only access their own tasks unless they're an admin
        if (!securityService.isAdmin() && !securityService.isCurrentUser(id)) {
            throw new UnauthorizedException("You are not authorized to access these tasks");
        }
        
        return ResponseEntity.ok(taskService.findTasksByAssignee(id));
    }

    @Operation(summary = "Get pending tasks by user", description = "Returns pending tasks assigned to a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pending tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these tasks")
    })
    @GetMapping("/user/{id}/pending")
    public ResponseEntity<List<TaskDTO>> getPendingTasksByUser(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        // Ensure user can only access their own tasks unless they're an admin
        if (!securityService.isAdmin() && !securityService.isCurrentUser(id)) {
            throw new UnauthorizedException("You are not authorized to access these tasks");
        }
        
        return ResponseEntity.ok(taskService.findPendingTasksByUser(id));
    }

    @Operation(summary = "Get tasks by customer", description = "Returns tasks related to a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<TaskDTO>> getTasksByCustomer(
            @Parameter(description = "ID of the customer") @PathVariable Long id) {
        return ResponseEntity.ok(taskService.findTasksByCustomer(id));
    }

    @Operation(summary = "Get tasks by deal", description = "Returns tasks related to a specific deal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/deal/{id}")
    public ResponseEntity<List<TaskDTO>> getTasksByDeal(
            @Parameter(description = "ID of the deal") @PathVariable Long id) {
        return ResponseEntity.ok(taskService.findTasksByDeal(id));
    }

    @Operation(summary = "Get tasks by status", description = "Returns tasks filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(
            @Parameter(description = "Task status") @PathVariable TaskStatus status) {
        return ResponseEntity.ok(taskService.findTasksByStatus(status));
    }

    @Operation(summary = "Get tasks by priority", description = "Returns tasks filtered by priority")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDTO>> getTasksByPriority(
            @Parameter(description = "Task priority") @PathVariable TaskPriority priority) {
        return ResponseEntity.ok(taskService.findTasksByPriority(priority));
    }

    @Operation(summary = "Assign task to user", description = "Assigns a task to a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task successfully assigned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "404", description = "Task or user not found")
    })
    @PutMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TaskDTO> assignTaskToUser(
            @Parameter(description = "ID of the task") @PathVariable Long id,
            @Parameter(description = "ID of the user") @PathVariable Long userId) {
        return ResponseEntity.ok(taskService.assignTaskToUser(id, userId));
    }

    @Operation(summary = "Update task status", description = "Updates the status of a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @Parameter(description = "ID of the task") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, status));
    }

    @Operation(summary = "Update task priority", description = "Updates the priority of a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task priority successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}/priority")
    public ResponseEntity<TaskDTO> updateTaskPriority(
            @Parameter(description = "ID of the task") @PathVariable Long id,
            @Parameter(description = "New priority") @RequestParam TaskPriority priority) {
        return ResponseEntity.ok(taskService.updateTaskPriority(id, priority));
    }

    @Operation(summary = "Mark task as completed", description = "Marks a task as completed")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task successfully marked as completed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskDTO> markTaskAsCompleted(
            @Parameter(description = "ID of the task") @PathVariable Long id) {
        return ResponseEntity.ok(taskService.markTaskAsCompleted(id));
    }

    @Operation(summary = "Get tasks due today", description = "Returns tasks that are due today")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/due-today")
    public ResponseEntity<List<TaskDTO>> getTasksDueToday() {
        return ResponseEntity.ok(taskService.findTasksDueToday());
    }

    @Operation(summary = "Get tasks due this week", description = "Returns tasks that are due this week")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/due-this-week")
    public ResponseEntity<List<TaskDTO>> getTasksDueThisWeek() {
        return ResponseEntity.ok(taskService.findTasksDueThisWeek());
    }

    @Operation(summary = "Get overdue tasks", description = "Returns tasks that are overdue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.findOverdueTasks());
    }

    @Operation(summary = "Get upcoming tasks", description = "Returns tasks that are due in the specified number of days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<TaskDTO>> getUpcomingTasks(
            @Parameter(description = "Number of days") @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(taskService.findUpcomingTasks(days));
    }

    @Operation(summary = "Get task statistics", description = "Returns statistics about tasks by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved task statistics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/stats")
    public ResponseEntity<Map<TaskStatus, Long>> getTaskStatistics() {
        return ResponseEntity.ok(taskService.getTaskCountsByStatus());
    }

    @Operation(summary = "Get tasks by due date range", description = "Returns tasks with due dates in the specified range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/by-due-date")
    public ResponseEntity<List<TaskDTO>> getTasksByDueDateRange(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(taskService.findTasksByDueDateRange(startDate, endDate));
    }

    @Operation(summary = "Get task counts by due date", description = "Returns counts of tasks by due date in the specified range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved task counts",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/stats/by-due-date")
    public ResponseEntity<Map<String, Long>> getTaskCountsByDueDate(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(taskService.getTaskCountsByDueDate(startDate, endDate));
    }
} 