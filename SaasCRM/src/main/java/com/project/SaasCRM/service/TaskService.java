package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.TaskPriority;
import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.dto.TaskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskService {
    TaskDTO createTask(TaskDTO task);
    
    TaskDTO updateTask(TaskDTO task);
    
    TaskDTO saveTask(TaskDTO task);
    
    void deleteTask(Long taskId);
    
    Optional<TaskDTO> findById(Long taskId);
    
    List<TaskDTO> findAllTasks();
    
    Page<TaskDTO> findAllTasks(Pageable pageable);
    
    Page<TaskDTO> findAllTasksPaginated(Pageable pageable);
    
    List<TaskDTO> findTasksByStatus(TaskStatus status);
    
    List<TaskDTO> findTasksByPriority(TaskPriority priority);
    
    List<TaskDTO> findTasksByAssignee(Long userId);
    
    List<TaskDTO> findTasksByDeal(Long dealId);
    
    List<TaskDTO> findTasksByCustomer(Long customerId);
    
    TaskDTO assignTaskToUser(Long taskId, Long userId);
    
    TaskDTO updateTaskStatus(Long taskId, TaskStatus newStatus);
    
    TaskDTO updateTaskPriority(Long taskId, TaskPriority newPriority);
    
    Map<TaskStatus, Long> getTaskCountsByStatus();
    
    List<TaskDTO> findOverdueTasks();
    
    List<TaskDTO> findUpcomingTasks(int days);
    
    List<TaskDTO> findTasksDueToday();
    
    List<TaskDTO> findTasksDueThisWeek();
    
    List<TaskDTO> findPendingTasksByUser(Long userId);
    
    Map<TaskStatus, Long> getTaskStatusCounts(Long userId);
    
    List<TaskDTO> findTasksByDueDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Long> getTaskCountsByDueDate(LocalDateTime startDate, LocalDateTime endDate);
    
    TaskDTO markTaskAsCompleted(Long taskId);
}