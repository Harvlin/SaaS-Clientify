package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.TaskPriority;
import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.dto.TaskDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private TaskController taskController;

    private TaskDTO testTask;
    private List<TaskDTO> taskList;
    private Page<TaskDTO> taskPage;

    @BeforeEach
    void setUp() {
        testTask = new TaskDTO();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test task description");
        testTask.setStatus(TaskStatus.PENDING);
        testTask.setPriority(TaskPriority.MEDIUM);
        testTask.setAssigneeId(1L);
        testTask.setDueDate(LocalDateTime.now().plusDays(3));
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        taskList = new ArrayList<>();
        taskList.add(testTask);

        taskPage = new PageImpl<>(taskList);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        when(taskService.findAllTasksPaginated(any(Pageable.class))).thenReturn(taskPage);

        ResponseEntity<Page<TaskDTO>> response = taskController.getAllTasks(Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskPage, response.getBody());
        verify(taskService).findAllTasksPaginated(any(Pageable.class));
    }

    @Test
    void createTask_WithValidData_ShouldCreateTask() {
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(testTask);

        ResponseEntity<TaskDTO> response = taskController.createTask(testTask);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testTask, response.getBody());
        verify(taskService).createTask(testTask);
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        when(taskService.findById(1L)).thenReturn(Optional.of(testTask));

        ResponseEntity<TaskDTO> response = taskController.getTaskById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTask, response.getBody());
        verify(taskService).findById(1L);
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturnNotFound() {
        when(taskService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<TaskDTO> response = taskController.getTaskById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService).findById(99L);
    }

    @Test
    void updateTask_WithValidData_ShouldUpdateTask() {
        when(taskService.updateTask(any(TaskDTO.class))).thenReturn(testTask);

        ResponseEntity<TaskDTO> response = taskController.updateTask(1L, testTask);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTask, response.getBody());
        verify(taskService).updateTask(testTask);
    }

    @Test
    void updateTask_WithMismatchedIds_ShouldReturnBadRequest() {
        TaskDTO differentTask = new TaskDTO();
        differentTask.setId(2L);

        ResponseEntity<TaskDTO> response = taskController.updateTask(1L, differentTask);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(taskService, never()).updateTask(any(TaskDTO.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        doNothing().when(taskService).deleteTask(1L);

        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService).deleteTask(1L);
    }

    @Test
    void getTasksByAssignedUser_WhenAuthorized_ShouldReturnTasks() {
        when(securityService.isAdmin()).thenReturn(true);
        when(taskService.findTasksByAssignee(1L)).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getTasksByAssignedUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(securityService).isAdmin();
        verify(taskService).findTasksByAssignee(1L);
    }

    @Test
    void getTasksByAssignedUser_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> taskController.getTasksByAssignedUser(1L));

        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(1L);
        verify(taskService, never()).findTasksByAssignee(anyLong());
    }

    @Test
    void getPendingTasksByUser_WhenAuthorized_ShouldReturnTasks() {
        when(securityService.isAdmin()).thenReturn(true);
        when(taskService.findPendingTasksByUser(1L)).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getPendingTasksByUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(securityService).isAdmin();
        verify(taskService).findPendingTasksByUser(1L);
    }

    @Test
    void getPendingTasksByUser_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> taskController.getPendingTasksByUser(1L));

        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(1L);
        verify(taskService, never()).findPendingTasksByUser(anyLong());
    }

    @Test
    void getTasksByCustomer_ShouldReturnTasks() {
        when(taskService.findTasksByCustomer(1L)).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getTasksByCustomer(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(taskService).findTasksByCustomer(1L);
    }

    @Test
    void getTasksByDeal_ShouldReturnTasks() {
        when(taskService.findTasksByDeal(1L)).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getTasksByDeal(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(taskService).findTasksByDeal(1L);
    }

    @Test
    void getTasksByStatus_ShouldReturnTasks() {
        when(taskService.findTasksByStatus(TaskStatus.PENDING)).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getTasksByStatus(TaskStatus.PENDING);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(taskService).findTasksByStatus(TaskStatus.PENDING);
    }

    @Test
    void getTasksByPriority_ShouldReturnTasks() {
        when(taskService.findTasksByPriority(TaskPriority.MEDIUM)).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getTasksByPriority(TaskPriority.MEDIUM);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(taskService).findTasksByPriority(TaskPriority.MEDIUM);
    }

    @Test
    void assignTaskToUser_ShouldAssignTask() {
        when(taskService.assignTaskToUser(1L, 1L)).thenReturn(testTask);

        ResponseEntity<TaskDTO> response = taskController.assignTaskToUser(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTask, response.getBody());
        verify(taskService).assignTaskToUser(1L, 1L);
    }

    @Test
    void updateTaskStatus_ShouldUpdateStatus() {
        when(taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS)).thenReturn(testTask);

        ResponseEntity<TaskDTO> response = taskController.updateTaskStatus(1L, TaskStatus.IN_PROGRESS);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTask, response.getBody());
        verify(taskService).updateTaskStatus(1L, TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateTaskPriority_ShouldUpdatePriority() {
        when(taskService.updateTaskPriority(1L, TaskPriority.HIGH)).thenReturn(testTask);

        ResponseEntity<TaskDTO> response = taskController.updateTaskPriority(1L, TaskPriority.HIGH);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTask, response.getBody());
        verify(taskService).updateTaskPriority(1L, TaskPriority.HIGH);
    }

    @Test
    void markTaskAsCompleted_ShouldMarkTaskAsCompleted() {
        when(taskService.markTaskAsCompleted(1L)).thenReturn(testTask);

        ResponseEntity<TaskDTO> response = taskController.markTaskAsCompleted(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTask, response.getBody());
        verify(taskService).markTaskAsCompleted(1L);
    }

    @Test
    void getTasksDueToday_ShouldReturnTasksDueToday() {
        when(taskService.findTasksDueToday()).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getTasksDueToday();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(taskService).findTasksDueToday();
    }

    @Test
    void getTasksDueThisWeek_ShouldReturnTasksDueThisWeek() {
        when(taskService.findTasksDueThisWeek()).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getTasksDueThisWeek();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(taskService).findTasksDueThisWeek();
    }

    @Test
    void getOverdueTasks_ShouldReturnOverdueTasks() {
        when(taskService.findOverdueTasks()).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getOverdueTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(taskService).findOverdueTasks();
    }

    @Test
    void getUpcomingTasks_ShouldReturnUpcomingTasks() {
        when(taskService.findUpcomingTasks(7)).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getUpcomingTasks(7);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(taskService).findUpcomingTasks(7);
    }

    @Test
    void getTaskStatistics_ShouldReturnTaskStatistics() {
        Map<TaskStatus, Long> taskStats = new EnumMap<>(TaskStatus.class);
        taskStats.put(TaskStatus.PENDING, 5L);
        taskStats.put(TaskStatus.IN_PROGRESS, 3L);
        
        when(taskService.getTaskCountsByStatus()).thenReturn(taskStats);

        ResponseEntity<Map<TaskStatus, Long>> response = taskController.getTaskStatistics();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskStats, response.getBody());
        verify(taskService).getTaskCountsByStatus();
    }

    @Test
    void getTasksByDueDateRange_ShouldReturnTasksInRange() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        
        when(taskService.findTasksByDueDateRange(startDate, endDate)).thenReturn(taskList);

        ResponseEntity<List<TaskDTO>> response = taskController.getTasksByDueDateRange(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskList, response.getBody());
        verify(taskService).findTasksByDueDateRange(startDate, endDate);
    }

    @Test
    void getTaskCountsByDueDate_ShouldReturnTaskCounts() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        Map<String, Long> taskCounts = new HashMap<>();
        taskCounts.put("2023-07-01", 3L);
        taskCounts.put("2023-07-02", 5L);
        
        when(taskService.getTaskCountsByDueDate(startDate, endDate)).thenReturn(taskCounts);

        ResponseEntity<Map<String, Long>> response = taskController.getTaskCountsByDueDate(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskCounts, response.getBody());
        verify(taskService).getTaskCountsByDueDate(startDate, endDate);
    }
} 