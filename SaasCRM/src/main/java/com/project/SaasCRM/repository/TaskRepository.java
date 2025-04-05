package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.TaskType;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.Deal;
import com.project.SaasCRM.domain.entity.Task;
import com.project.SaasCRM.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedUser(User user);

    List<Task> findByCustomer(Customer customer);

    List<Task> findByDeal(Deal deal);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByType(TaskType type);

    @Query("SELECT t FROM Task t WHERE t.dueDate <= :date AND t.status <> 'COMPLETED'")
    List<Task> findUpcomingTasks(@Param("date") LocalDateTime date);

    @Query("SELECT t FROM Task t WHERE " +
            "t.assignedUser.id = :userId AND t.status <> 'COMPLETED' " +
            "ORDER BY t.dueDate ASC")
    List<Task> findPendingTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE " +
            "t.dueDate >= :startDate AND t.dueDate <= :endDate")
    List<Task> findByDueDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}