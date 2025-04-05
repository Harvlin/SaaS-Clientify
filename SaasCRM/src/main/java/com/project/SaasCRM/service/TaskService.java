package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.TaskType;
import com.project.SaasCRM.domain.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskService {
    Task saveTask(Task task);

    Task updateTask(Task task);

    void deleteTask(Long taskId);

    Optional<Task> findById(Long taskId);

    List<Task> findAllTasks();

    Page<Task> findAllTasksPaginated(Pageable pageable);

    List<Task> findTasksByAssignedUser(Long userId);

    List<Task> findPendingTasksByUser(Long userId);

    List<Task> findTasksByCustomer(Long customerId);

    List<Task> findTasksByDeal(Long dealId);

    List<Task> findTasksByStatus(TaskStatus status);

    List<Task> findTasksByType(TaskType type);

    Task assignUserToTask(Long taskId, Long userId);

    Task updateTaskStatus(Long taskId, TaskStatus status);

    Task markTaskAsCompleted(Long taskId);

    List<Task> findTasksDueToday();

    List<Task> findTasksDueThisWeek();

    List<Task> findOverdueTasks();

    List<Task> findTasksByDueDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Task> findUpcomingTasks(int days);

    Map<TaskStatus, Long> getTaskStatusCounts(Long userId);

    Map<String, Long> getTaskCountsByDueDate(LocalDateTime startDate, LocalDateTime endDate);
}