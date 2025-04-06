package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.TaskPriority;
import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.Deal;
import com.project.SaasCRM.domain.entity.Task;
import com.project.SaasCRM.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<Task> findByPriority(TaskPriority priority);

    List<Task> findByAssigneeId(Long userId);

    List<Task> findByDealId(Long dealId);

    List<Task> findByCustomerId(Long customerId);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :date AND t.status != :status")
    List<Task> findByDueDateBeforeAndStatusNot(
            @Param("date") LocalDateTime date,
            @Param("status") TaskStatus status
    );

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate AND t.status != :status")
    List<Task> findByDueDateBetweenAndStatusNot(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") TaskStatus status
    );

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId AND t.status = :status")
    List<Task> findByAssigneeIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status
    );

    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(@Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t WHERE DATE(t.dueDate) = CURRENT_DATE")
    List<Task> findTasksDueToday();

    @Query("SELECT t FROM Task t WHERE " +
            "t.dueDate BETWEEN CURRENT_DATE AND (CURRENT_DATE + 7)")
    List<Task> findTasksDueThisWeek();

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId AND t.status = 'PENDING'")
    List<Task> findPendingTasksByUser(@Param("userId") Long userId);

    @Query("SELECT t.status as status, COUNT(t) as count FROM Task t " +
            "WHERE t.assignee.id = :userId GROUP BY t.status")
    List<Object[]> getTaskStatusCounts(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findTasksByDueDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT DATE(t.dueDate) as date, COUNT(t) as count FROM Task t " +
            "WHERE t.dueDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(t.dueDate)")
    List<Object[]> getTaskCountsByDueDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Task t WHERE t.assignedUser.id = :userId")
    List<Task> findByAssignedUser_Id(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedUser.id = :userId")
    long countByAssignedUser_Id(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedUser.id = :userId AND t.status = :status")
    long countByAssignedUser_IdAndStatus(
        @Param("userId") Long userId,
        @Param("status") TaskStatus status
    );

    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status AND t.dueDate <= :endDate")
    long countByStatusAndDueDateBefore(
        @Param("status") TaskStatus status,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Task t WHERE t.assignedUser.id = :userId AND t.dueDate <= :endDate")
    List<Task> findByAssignedUser_IdAndDueDateBefore(
        @Param("userId") Long userId,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Task t WHERE t.assignedUser.id = :userId AND t.status = :status")
    List<Task> findByAssignedUser_IdAndStatus(
        @Param("userId") Long userId,
        @Param("status") TaskStatus status
    );

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedUser.id = :userId AND t.dueDate < :date")
    long countByAssignedUser_IdAndDueDateBefore(
        @Param("userId") Long userId,
        @Param("date") LocalDateTime date
    );
}