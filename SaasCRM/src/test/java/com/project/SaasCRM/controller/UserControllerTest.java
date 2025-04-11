package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.UserDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.CustomerService;
import com.project.SaasCRM.service.DealService;
import com.project.SaasCRM.service.TaskService;
import com.project.SaasCRM.service.UserService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    
    @Mock
    private TaskService taskService;
    
    @Mock
    private CustomerService customerService;
    
    @Mock
    private DealService dealService;
    
    @Mock
    private SecurityService securityService;
    
    @InjectMocks
    private UserController userController;
    
    private UserDTO testUser;
    private List<UserDTO> userList;
    private Page<UserDTO> userPage;
    
    @BeforeEach
    void setUp() {
        testUser = new UserDTO();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        userList = new ArrayList<>();
        userList.add(testUser);
        
        userPage = new PageImpl<>(userList);
    }
    
    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        when(userService.findAllUsersPaginated(any(Pageable.class))).thenReturn(userPage);
        
        ResponseEntity<Page<UserDTO>> response = userController.getAllUsers(Pageable.unpaged());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userPage, response.getBody());
        verify(userService).findAllUsersPaginated(any(Pageable.class));
    }
    
    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        
        ResponseEntity<UserDTO> response = userController.getUserById(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).findById(1L);
    }
    
    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnNotFound() {
        when(userService.findById(99L)).thenReturn(Optional.empty());
        
        ResponseEntity<UserDTO> response = userController.getUserById(99L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).findById(99L);
    }
    
    @Test
    void updateUser_WhenAuthorized_ShouldUpdateUser() {
        when(securityService.isAdmin()).thenReturn(true);
        when(userService.updateUser(any(UserDTO.class))).thenReturn(testUser);
        
        ResponseEntity<UserDTO> response = userController.updateUser(1L, testUser);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(securityService).isAdmin();
        verify(userService).updateUser(testUser);
    }
    
    @Test
    void updateUser_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(anyLong())).thenReturn(false);
        
        assertThrows(UnauthorizedException.class, () -> userController.updateUser(1L, testUser));
        
        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(1L);
        verify(userService, never()).updateUser(any(UserDTO.class));
    }
    
    @Test
    void deleteUser_ShouldDeleteUser() {
        doNothing().when(userService).deleteUser(1L);
        
        ResponseEntity<Void> response = userController.deleteUser(1L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(1L);
    }
} 