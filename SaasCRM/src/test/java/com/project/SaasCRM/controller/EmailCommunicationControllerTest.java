package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.SendStatus;
import com.project.SaasCRM.domain.dto.EmailCommunicationDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.EmailCommunicationService;
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
public class EmailCommunicationControllerTest {

    @Mock
    private EmailCommunicationService emailCommunicationService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private EmailCommunicationController emailCommunicationController;

    private EmailCommunicationDTO testEmail;
    private List<EmailCommunicationDTO> emailList;
    private Page<EmailCommunicationDTO> emailPage;

    @BeforeEach
    void setUp() {
        testEmail = new EmailCommunicationDTO();
        testEmail.setId(1L);
        testEmail.setSubject("Test Subject");
        testEmail.setContent("<p>Email content</p>");
        testEmail.setSenderEmail("sender@example.com");
        testEmail.setRecipientEmail("recipient@example.com");
        testEmail.setCustomerId(1L);
        testEmail.setSentByUserId(1L);
        testEmail.setEmailTemplateId(1L);
        testEmail.setSendStatus(SendStatus.DRAFT);
        testEmail.setCreatedAt(LocalDateTime.now());
        
        emailList = new ArrayList<>();
        emailList.add(testEmail);
        
        emailPage = new PageImpl<>(emailList);
    }

    @Test
    void getAllEmails_ShouldReturnAllEmails() {
        when(emailCommunicationService.findAllEmailCommunicationsPaginated(any(Pageable.class))).thenReturn(emailPage);

        ResponseEntity<Page<EmailCommunicationDTO>> response = emailCommunicationController.getAllEmails(Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emailPage, response.getBody());
        verify(emailCommunicationService).findAllEmailCommunicationsPaginated(any(Pageable.class));
    }

    @Test
    void createEmail_WithValidData_ShouldCreateEmail() {
        when(emailCommunicationService.saveEmailCommunication(any(EmailCommunicationDTO.class))).thenReturn(testEmail);

        ResponseEntity<EmailCommunicationDTO> response = emailCommunicationController.createEmail(testEmail);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testEmail, response.getBody());
        verify(emailCommunicationService).saveEmailCommunication(testEmail);
    }

    @Test
    void getEmailById_WhenEmailExists_ShouldReturnEmail() {
        when(emailCommunicationService.findById(1L)).thenReturn(Optional.of(testEmail));

        ResponseEntity<EmailCommunicationDTO> response = emailCommunicationController.getEmailById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testEmail, response.getBody());
        verify(emailCommunicationService).findById(1L);
    }

    @Test
    void getEmailById_WhenEmailDoesNotExist_ShouldReturnNotFound() {
        when(emailCommunicationService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<EmailCommunicationDTO> response = emailCommunicationController.getEmailById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(emailCommunicationService).findById(99L);
    }

    @Test
    void updateEmail_WithValidData_ShouldUpdateEmail() {
        when(emailCommunicationService.updateEmailCommunication(any(EmailCommunicationDTO.class))).thenReturn(testEmail);

        ResponseEntity<EmailCommunicationDTO> response = emailCommunicationController.updateEmail(1L, testEmail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testEmail, response.getBody());
        verify(emailCommunicationService).updateEmailCommunication(testEmail);
    }

    @Test
    void updateEmail_WithMismatchedIds_ShouldReturnBadRequest() {
        EmailCommunicationDTO differentEmail = new EmailCommunicationDTO();
        differentEmail.setId(2L);

        ResponseEntity<EmailCommunicationDTO> response = emailCommunicationController.updateEmail(1L, differentEmail);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(emailCommunicationService, never()).updateEmailCommunication(any(EmailCommunicationDTO.class));
    }

    @Test
    void deleteEmail_ShouldDeleteEmail() {
        doNothing().when(emailCommunicationService).deleteEmailCommunication(1L);

        ResponseEntity<Void> response = emailCommunicationController.deleteEmail(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(emailCommunicationService).deleteEmailCommunication(1L);
    }

    @Test
    void getEmailsByCustomer_WhenAuthorized_ShouldReturnEmails() {
        when(securityService.isAdmin()).thenReturn(true);
        when(emailCommunicationService.findEmailsByCustomer(1L)).thenReturn(emailList);

        ResponseEntity<List<EmailCommunicationDTO>> response = emailCommunicationController.getEmailsByCustomer(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emailList, response.getBody());
        verify(securityService).isAdmin();
        verify(emailCommunicationService).findEmailsByCustomer(1L);
    }

    @Test
    void getEmailsByCustomer_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessCustomer(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> emailCommunicationController.getEmailsByCustomer(1L));

        verify(securityService).isAdmin();
        verify(securityService).canAccessCustomer(1L);
        verify(emailCommunicationService, never()).findEmailsByCustomer(anyLong());
    }

    @Test
    void getEmailsByUser_WhenAuthorized_ShouldReturnEmails() {
        when(securityService.isAdmin()).thenReturn(true);
        when(emailCommunicationService.findEmailsBySentBy(1L)).thenReturn(emailList);

        ResponseEntity<List<EmailCommunicationDTO>> response = emailCommunicationController.getEmailsByUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emailList, response.getBody());
        verify(securityService).isAdmin();
        verify(emailCommunicationService).findEmailsBySentBy(1L);
    }

    @Test
    void getEmailsByUser_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> emailCommunicationController.getEmailsByUser(1L));

        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(1L);
        verify(emailCommunicationService, never()).findEmailsBySentBy(anyLong());
    }

    @Test
    void getEmailsByTemplate_ShouldReturnEmails() {
        when(emailCommunicationService.findEmailsByTemplate(1L)).thenReturn(emailList);

        ResponseEntity<List<EmailCommunicationDTO>> response = emailCommunicationController.getEmailsByTemplate(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emailList, response.getBody());
        verify(emailCommunicationService).findEmailsByTemplate(1L);
    }

    @Test
    void getEmailsByStatus_ShouldReturnEmails() {
        when(emailCommunicationService.findEmailsByStatus(SendStatus.SENT)).thenReturn(emailList);

        ResponseEntity<List<EmailCommunicationDTO>> response = emailCommunicationController.getEmailsByStatus(SendStatus.SENT);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emailList, response.getBody());
        verify(emailCommunicationService).findEmailsByStatus(SendStatus.SENT);
    }

    @Test
    void getOpenedEmails_ShouldReturnEmails() {
        when(emailCommunicationService.findOpenedEmails()).thenReturn(emailList);

        ResponseEntity<List<EmailCommunicationDTO>> response = emailCommunicationController.getOpenedEmails();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emailList, response.getBody());
        verify(emailCommunicationService).findOpenedEmails();
    }

    @Test
    void sendEmail_ShouldSendEmail() {
        when(emailCommunicationService.sendEmail(1L)).thenReturn(testEmail);

        ResponseEntity<EmailCommunicationDTO> response = emailCommunicationController.sendEmail(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testEmail, response.getBody());
        verify(emailCommunicationService).sendEmail(1L);
    }

    @Test
    void scheduleEmail_ShouldScheduleEmail() {
        LocalDateTime scheduleTime = LocalDateTime.now().plusDays(1);
        when(emailCommunicationService.scheduleEmail(eq(1L), any(LocalDateTime.class))).thenReturn(testEmail);

        ResponseEntity<EmailCommunicationDTO> response = emailCommunicationController.scheduleEmail(1L, scheduleTime);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testEmail, response.getBody());
        verify(emailCommunicationService).scheduleEmail(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void markEmailAsOpened_ShouldMarkEmailAsOpened() {
        doNothing().when(emailCommunicationService).markEmailAsOpened(1L);

        ResponseEntity<Void> response = emailCommunicationController.markEmailAsOpened(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(emailCommunicationService).markEmailAsOpened(1L);
    }

    @Test
    void incrementEmailClickCount_ShouldIncrementClickCount() {
        doNothing().when(emailCommunicationService).incrementEmailClickCount(1L);

        ResponseEntity<Void> response = emailCommunicationController.incrementEmailClickCount(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(emailCommunicationService).incrementEmailClickCount(1L);
    }

    @Test
    void createEmailFromTemplate_WhenAuthorized_ShouldCreateEmailFromTemplate() {
        when(securityService.isAdmin()).thenReturn(true);
        when(emailCommunicationService.createEmailFromTemplate(eq(1L), eq(1L), anyString())).thenReturn(testEmail);

        ResponseEntity<EmailCommunicationDTO> response = emailCommunicationController.createEmailFromTemplate(1L, 1L, "Custom Subject");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testEmail, response.getBody());
        verify(securityService).isAdmin();
        verify(emailCommunicationService).createEmailFromTemplate(eq(1L), eq(1L), anyString());
    }

    @Test
    void createEmailFromTemplate_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessCustomer(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> emailCommunicationController.createEmailFromTemplate(1L, 1L, "Custom Subject"));

        verify(securityService).isAdmin();
        verify(securityService).canAccessCustomer(1L);
        verify(emailCommunicationService, never()).createEmailFromTemplate(anyLong(), anyLong(), anyString());
    }

    @Test
    void getEmailStats_ShouldReturnStats() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Map<String, Long> stats = new HashMap<>();
        stats.put("2023-06-01", 10L);
        stats.put("2023-06-02", 15L);
        
        when(emailCommunicationService.getEmailCountsByDate(startDate, endDate)).thenReturn(stats);

        ResponseEntity<Map<String, Long>> response = emailCommunicationController.getEmailStats(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(stats, response.getBody());
        verify(emailCommunicationService).getEmailCountsByDate(startDate, endDate);
    }

    @Test
    void getEmailOpenRate_ShouldReturnOpenRate() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        double openRate = 0.75;
        
        when(emailCommunicationService.calculateEmailOpenRate(startDate, endDate)).thenReturn(openRate);

        ResponseEntity<Double> response = emailCommunicationController.getEmailOpenRate(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(openRate, response.getBody());
        verify(emailCommunicationService).calculateEmailOpenRate(startDate, endDate);
    }

    @Test
    void getEmailClickRate_ShouldReturnClickRate() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        double clickRate = 0.45;
        
        when(emailCommunicationService.calculateEmailClickRate(startDate, endDate)).thenReturn(clickRate);

        ResponseEntity<Double> response = emailCommunicationController.getEmailClickRate(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clickRate, response.getBody());
        verify(emailCommunicationService).calculateEmailClickRate(startDate, endDate);
    }
} 