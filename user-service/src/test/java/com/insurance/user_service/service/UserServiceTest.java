package com.insurance.user_service.service;

import com.insurance.user_service.dto.UserRequest;
import com.insurance.user_service.dto.UserResponse;
import com.insurance.user_service.exception.UserNotFoundException;
import com.insurance.user_service.mapper.UserMapper;
import com.insurance.user_service.model.User;
import com.insurance.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Niro Test", "niro@test.com", "POL-001");
        userResponse = new UserResponse(1L, "Niro Test", "niro@test.com", "POL-001");
        userRequest = new UserRequest("Niro Test", "niro@test.com", "POL-001");
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("niro@test.com");
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Niro Test");
    }

    @Test
    void getUserById_shouldThrowException_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createUser_shouldSaveAndReturnUser() {
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(userRequest);

        assertThat(result.getEmail()).isEqualTo("niro@test.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUser_shouldDelete_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}