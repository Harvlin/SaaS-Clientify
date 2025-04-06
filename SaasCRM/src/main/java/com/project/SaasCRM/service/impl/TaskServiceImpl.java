package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.TaskPriority;
import com.project.SaasCRM.domain.entity.Task;
import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.domain.dto.TaskDTO;
import com.project.SaasCRM.exception.TaskNotFoundException;
import com.project.SaasCRM.repository.TaskRepository;
import com.project.SaasCRM.repository.UserRepository;
import com.project.SaasCRM.service.TaskService;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }

        Task savedTask = taskRepository.save(task);
        auditLogService.logSystemActivity("TASK_CREATED", "TASK", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskDTO updateTask(TaskDTO taskDTO) {
        Task existingTask = taskRepository.findById(taskDTO.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        Task task = taskMapper.toEntity(taskDTO);
        Task updatedTask = taskRepository.save(task);
        auditLogService.logSystemActivity("TASK_UPDATED", "TASK", updatedTask.getId());
        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskDTO> findById(Long taskId) {
        return taskRepository.findById(taskId)
                .map(taskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> findAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findTasksByStatus(TaskStatus status) {
        return taskMapper.toDtoList(taskRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findTasksByPriority(TaskPriority priority) {
        return taskMapper.toDtoList(taskRepository.findByPriority(priority));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findTasksByAssignee(Long userId) {
        return taskMapper.toDtoList(taskRepository.findByAssigneeId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findTasksByDeal(Long dealId) {
        return taskMapper.toDtoList(taskRepository.findByDealId(dealId));
    }

    @Override
    @Transactional
    public TaskDTO assignTaskToUser(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        task.setAssignee(user);
        Task updatedTask = taskRepository.save(task);
        auditLogService.logUserActivity(userId, "TASK_ASSIGNED", "TASK", taskId);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional
    public TaskDTO updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        task.setStatus(newStatus);
        if (newStatus == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        Task updatedTask = taskRepository.save(task);
        auditLogService.logSystemActivity("TASK_STATUS_UPDATED", "TASK", taskId);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional
    public TaskDTO updateTaskPriority(Long taskId, TaskPriority newPriority) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        task.setPriority(newPriority);
        Task updatedTask = taskRepository.save(task);
        auditLogService.logSystemActivity("TASK_PRIORITY_UPDATED", "TASK", taskId);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<TaskStatus, Long> getTaskCountsByStatus() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    Task::getStatus,
                    Collectors.counting()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findOverdueTasks() {
        return taskMapper.toDtoList(taskRepository.findByDueDateBeforeAndStatusNot(
            LocalDateTime.now(),
            TaskStatus.COMPLETED
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findUpcomingTasks(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        return taskMapper.toDtoList(taskRepository.findByDueDateBetweenAndStatusNot(
            now,
            future,
            TaskStatus.COMPLETED
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findTasksDueToday() {
        return taskMapper.toDtoList(taskRepository.findTasksDueToday());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findTasksDueThisWeek() {
        return taskMapper.toDtoList(taskRepository.findTasksDueThisWeek());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findPendingTasksByUser(Long userId) {
        return taskMapper.toDtoList(taskRepository.findPendingTasksByUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<TaskStatus, Long> getTaskStatusCounts(Long userId) {
        List<Object[]> results = taskRepository.getTaskStatusCounts(userId);
        Map<TaskStatus, Long> statusCounts = new HashMap<>();
        for (Object[] result : results) {
            TaskStatus status = (TaskStatus) result[0];
            Long count = (Long) result[1];
            statusCounts.put(status, count);
        }
        return statusCounts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findTasksByDueDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return taskMapper.toDtoList(taskRepository.findTasksByDueDateRange(startDate, endDate));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getTaskCountsByDueDate(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = taskRepository.getTaskCountsByDueDate(startDate, endDate);
        Map<String, Long> countsByDate = new HashMap<>();
        for (Object[] result : results) {
            String date = result[0].toString();
            Long count = (Long) result[1];
            countsByDate.put(date, count);
        }
        return countsByDate;
    }

    @Override
    @Transactional
    public TaskDTO markTaskAsCompleted(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        Task updatedTask = taskRepository.save(task);
        auditLogService.logSystemActivity("TASK_COMPLETED", "TASK", taskId);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findAllTasks() {
        return taskMapper.toDtoList(taskRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> findAllTasksPaginated(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::toDto);
    }

    @Override
    @Transactional
    public TaskDTO saveTask(TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        Task savedTask = taskRepository.save(task);
        auditLogService.logSystemActivity("TASK_SAVED", "TASK", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findTasksByCustomer(Long customerId) {
        return taskMapper.toDtoList(taskRepository.findByCustomerId(customerId));
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        taskRepository.delete(task);
        auditLogService.logSystemActivity("TASK_DELETED", "TASK", taskId);
    }
} 