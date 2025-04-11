package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.AuditLogDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditLogControllerTest {

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private AuditLogController auditLogController;

    private AuditLogDTO testLog;
    private List<AuditLogDTO> logList;

    @BeforeEach
    void setUp() {
        testLog = new AuditLogDTO();
        testLog.setId(1L);
        testLog.setUserId(1L);
        testLog.setActivity("CREATE");
        testLog.setEntityType("CUSTOMER");
        testLog.setEntityId(1L);
        testLog.setTimestamp(LocalDateTime.now());
        testLog.setDetails("Created a new customer");
        testLog.setSystemActivity(false);
        
        logList = new ArrayList<>();
        logList.add(testLog);
    }

    @Test
    void getUserActivityLogs_WhenAuthorizedAndNoDateRange_ShouldReturnLogs() {
        when(securityService.isAdmin()).thenReturn(true);
        when(auditLogService.getUserActivityLogs(eq(1L), eq(20))).thenReturn(logList);

        ResponseEntity<List<AuditLogDTO>> response = auditLogController.getUserActivityLogs(1L, 20, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logList, response.getBody());
        verify(securityService).isAdmin();
        verify(auditLogService).getUserActivityLogs(1L, 20);
    }

    @Test
    void getUserActivityLogs_WhenAuthorizedAndWithDateRange_ShouldReturnLogs() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(securityService.isAdmin()).thenReturn(true);
        when(auditLogService.getUserActivityLogs(eq(1L), eq(startDate), eq(endDate))).thenReturn(logList);

        ResponseEntity<List<AuditLogDTO>> response = auditLogController.getUserActivityLogs(1L, 20, startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logList, response.getBody());
        verify(securityService).isAdmin();
        verify(auditLogService).getUserActivityLogs(1L, startDate, endDate);
    }

    @Test
    void getUserActivityLogs_WhenUserAccessingOwnLogs_ShouldReturnLogs() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(1L)).thenReturn(true);
        when(auditLogService.getUserActivityLogs(eq(1L), eq(20))).thenReturn(logList);

        ResponseEntity<List<AuditLogDTO>> response = auditLogController.getUserActivityLogs(1L, 20, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logList, response.getBody());
        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(1L);
        verify(auditLogService).getUserActivityLogs(1L, 20);
    }

    @Test
    void getUserActivityLogs_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> auditLogController.getUserActivityLogs(1L, 20, null, null));

        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(1L);
        verify(auditLogService, never()).getUserActivityLogs(anyLong(), anyInt());
    }

    @Test
    void getEntityActivityLogs_WhenAuthorizedAndNoLimit_ShouldReturnLogs() {
        when(securityService.isAdmin()).thenReturn(true);
        when(auditLogService.getEntityActivityLogs("CUSTOMER", 1L)).thenReturn(logList);

        ResponseEntity<List<AuditLogDTO>> response = auditLogController.getEntityActivityLogs("CUSTOMER", 1L, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logList, response.getBody());
        verify(securityService).isAdmin();
        verify(auditLogService).getEntityActivityLogs("CUSTOMER", 1L);
    }

    @Test
    void getEntityActivityLogs_WhenAuthorizedAndWithLimit_ShouldReturnLogs() {
        when(securityService.isAdmin()).thenReturn(true);
        when(auditLogService.getEntityActivityLogs("CUSTOMER", 1L, 10)).thenReturn(logList);

        ResponseEntity<List<AuditLogDTO>> response = auditLogController.getEntityActivityLogs("CUSTOMER", 1L, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logList, response.getBody());
        verify(securityService).isAdmin();
        verify(auditLogService).getEntityActivityLogs("CUSTOMER", 1L, 10);
    }

    @Test
    void getEntityActivityLogs_WhenUserHasAccessToEntity_ShouldReturnLogs() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessCustomer(1L)).thenReturn(true);
        when(auditLogService.getEntityActivityLogs("CUSTOMER", 1L)).thenReturn(logList);

        ResponseEntity<List<AuditLogDTO>> response = auditLogController.getEntityActivityLogs("CUSTOMER", 1L, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logList, response.getBody());
        verify(securityService).isAdmin();
        verify(auditLogService).getEntityActivityLogs("CUSTOMER", 1L);
    }

    @Test
    void getEntityActivityLogs_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessCustomer(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> auditLogController.getEntityActivityLogs("CUSTOMER", 1L, 0));

        verify(securityService).isAdmin();
        verify(auditLogService, never()).getEntityActivityLogs(anyString(), anyLong());
    }

    @Test
    void getRecentActivities_WhenAuthorized_ShouldReturnLogs() {
        when(securityService.isAdmin()).thenReturn(true);
        when(auditLogService.getRecentActivities(20)).thenReturn(logList);

        ResponseEntity<List<AuditLogDTO>> response = auditLogController.getRecentActivities(20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logList, response.getBody());
        verify(securityService).isAdmin();
        verify(auditLogService).getRecentActivities(20);
    }

    @Test
    void getRecentActivities_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> auditLogController.getRecentActivities(20));

        verify(securityService).isAdmin();
        verify(auditLogService, never()).getRecentActivities(anyInt());
    }
} 