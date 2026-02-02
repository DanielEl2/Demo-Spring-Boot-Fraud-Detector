package com.project.bankwebapp.service;



import com.project.bankwebapp.model.Role;
import com.project.bankwebapp.Entities.UserEntity;         // Capital 'E'
import com.project.bankwebapp.Repositories.UserRepository; // Capital 'R'
import com.project.bankwebapp.Services.UserService;        // Capital 'S'


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void EncryptPasswordAndSave() {

        UserEntity newUser = new UserEntity();
        newUser.setUsername("testuser");
        newUser.setEmail("test@email.com");
        newUser.setPassword("rawPassword");


        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");


        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));


        UserEntity savedUser = userService.createUser(newUser);


        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void ErrorWhenUsernameExists() {
        UserEntity newUser = new UserEntity();
        newUser.setUsername("existingUser");
        newUser.setEmail("new@email.com");


        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new UserEntity()));

        assertThrows(ResponseStatusException.class, () -> {
            userService.createUser(newUser);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void ReturnUserWhenFound() {
        UUID id = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setUser_id(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserEntity found = userService.findUserById(id);
        assertEquals(id, found.getUser_id());
    }
}
