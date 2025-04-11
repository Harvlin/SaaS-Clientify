package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.EmailTemplateDTO;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.EmailTemplateService;
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
public class EmailTemplateControllerTest {

    @Mock
    private EmailTemplateService emailTemplateService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private EmailTemplateController emailTemplateController;

    private EmailTemplateDTO testTemplate;
    private List<EmailTemplateDTO> templateList;
    private Page<EmailTemplateDTO> templatePage;

    @BeforeEach
    void setUp() {
        testTemplate = new EmailTemplateDTO();
        testTemplate.setId(1L);
        testTemplate.setName("Test Template");
        testTemplate.setSubjectTemplate("Test Subject");
        testTemplate.setContentTemplate("Hello, {{customerName}}!");
        testTemplate.setDescription("Test template description");
        testTemplate.setActive(true);
        testTemplate.setCreatedAt(LocalDateTime.now());
        testTemplate.setUpdatedAt(LocalDateTime.now());
        
        templateList = new ArrayList<>();
        templateList.add(testTemplate);
        
        templatePage = new PageImpl<>(templateList);
    }

    @Test
    void getAllEmailTemplates_ShouldReturnAllTemplates() {
        when(emailTemplateService.findAllTemplatesPaginated(any(Pageable.class))).thenReturn(templatePage);

        ResponseEntity<Page<EmailTemplateDTO>> response = emailTemplateController.getAllEmailTemplates(Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(templatePage, response.getBody());
        verify(emailTemplateService).findAllTemplatesPaginated(any(Pageable.class));
    }

    @Test
    void createEmailTemplate_WithValidData_ShouldCreateTemplate() {
        when(emailTemplateService.createTemplate(any(EmailTemplateDTO.class))).thenReturn(testTemplate);

        ResponseEntity<EmailTemplateDTO> response = emailTemplateController.createEmailTemplate(testTemplate);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testTemplate, response.getBody());
        verify(emailTemplateService).createTemplate(testTemplate);
    }

    @Test
    void getEmailTemplateById_WhenTemplateExists_ShouldReturnTemplate() {
        when(emailTemplateService.findById(1L)).thenReturn(Optional.of(testTemplate));

        ResponseEntity<EmailTemplateDTO> response = emailTemplateController.getEmailTemplateById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTemplate, response.getBody());
        verify(emailTemplateService).findById(1L);
    }

    @Test
    void getEmailTemplateById_WhenTemplateDoesNotExist_ShouldReturnNotFound() {
        when(emailTemplateService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<EmailTemplateDTO> response = emailTemplateController.getEmailTemplateById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(emailTemplateService).findById(99L);
    }

    @Test
    void updateEmailTemplate_WithValidData_ShouldUpdateTemplate() {
        when(emailTemplateService.updateTemplate(any(EmailTemplateDTO.class))).thenReturn(testTemplate);

        ResponseEntity<EmailTemplateDTO> response = emailTemplateController.updateEmailTemplate(1L, testTemplate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTemplate, response.getBody());
        verify(emailTemplateService).updateTemplate(testTemplate);
    }

    @Test
    void updateEmailTemplate_WithMismatchedIds_ShouldReturnBadRequest() {
        EmailTemplateDTO differentTemplate = new EmailTemplateDTO();
        differentTemplate.setId(2L);

        ResponseEntity<EmailTemplateDTO> response = emailTemplateController.updateEmailTemplate(1L, differentTemplate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(emailTemplateService, never()).updateTemplate(any(EmailTemplateDTO.class));
    }

    @Test
    void deleteEmailTemplate_ShouldDeleteTemplate() {
        doNothing().when(emailTemplateService).deleteTemplate(1L);

        ResponseEntity<Void> response = emailTemplateController.deleteEmailTemplate(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(emailTemplateService).deleteTemplate(1L);
    }

    @Test
    void getEmailTemplatesByType_ShouldReturnTemplates() {
        when(emailTemplateService.findEmailTemplatesByType("WELCOME")).thenReturn(templateList);

        ResponseEntity<List<EmailTemplateDTO>> response = emailTemplateController.getEmailTemplatesByType("WELCOME");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(templateList, response.getBody());
        verify(emailTemplateService).findEmailTemplatesByType("WELCOME");
    }

    @Test
    void getEmailTemplatesByUser_WhenAuthorized_ShouldReturnTemplates() {
        when(securityService.isAdmin()).thenReturn(true);
        when(emailTemplateService.findEmailTemplatesByCreatedBy(1L)).thenReturn(templateList);

        ResponseEntity<List<EmailTemplateDTO>> response = emailTemplateController.getEmailTemplatesByUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(templateList, response.getBody());
        verify(securityService).isAdmin();
        verify(emailTemplateService).findEmailTemplatesByCreatedBy(1L);
    }

    @Test
    void getEmailTemplatesByUser_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> emailTemplateController.getEmailTemplatesByUser(1L));

        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(1L);
        verify(emailTemplateService, never()).findEmailTemplatesByCreatedBy(anyLong());
    }

    @Test
    void processTemplateForCustomer_WhenAuthorized_ShouldProcessTemplate() {
        when(securityService.isAdmin()).thenReturn(true);
        when(emailTemplateService.processTemplateForCustomer(1L, 1L)).thenReturn("Hello, John!");

        ResponseEntity<String> response = emailTemplateController.processTemplateForCustomer(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello, John!", response.getBody());
        verify(securityService).isAdmin();
        verify(emailTemplateService).processTemplateForCustomer(1L, 1L);
    }

    @Test
    void processTemplateForCustomer_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessCustomer(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> emailTemplateController.processTemplateForCustomer(1L, 1L));

        verify(securityService).isAdmin();
        verify(securityService).canAccessCustomer(1L);
        verify(emailTemplateService, never()).processTemplateForCustomer(anyLong(), anyLong());
    }

    @Test
    void getActiveEmailTemplates_ShouldReturnActiveTemplates() {
        when(emailTemplateService.findActiveTemplates()).thenReturn(templateList);

        ResponseEntity<List<EmailTemplateDTO>> response = emailTemplateController.getActiveEmailTemplates();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(templateList, response.getBody());
        verify(emailTemplateService).findActiveTemplates();
    }

    @Test
    void activateEmailTemplate_ShouldActivateTemplate() {
        doNothing().when(emailTemplateService).activateTemplate(1L);

        ResponseEntity<Void> response = emailTemplateController.activateEmailTemplate(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(emailTemplateService).activateTemplate(1L);
    }

    @Test
    void deactivateEmailTemplate_ShouldDeactivateTemplate() {
        doNothing().when(emailTemplateService).deactivateTemplate(1L);

        ResponseEntity<Void> response = emailTemplateController.deactivateEmailTemplate(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(emailTemplateService).deactivateTemplate(1L);
    }

    @Test
    void getMostUsedEmailTemplates_ShouldReturnTemplates() {
        when(emailTemplateService.findMostUsedTemplates()).thenReturn(templateList);

        ResponseEntity<List<EmailTemplateDTO>> response = emailTemplateController.getMostUsedEmailTemplates();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(templateList, response.getBody());
        verify(emailTemplateService).findMostUsedTemplates();
    }
} 