package com.insurance.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.user_service.dto.UserRequest;
import com.insurance.user_service.dto.UserResponse;
import com.insurance.user_service.exception.UserNotFoundException;
import com.insurance.user_service.service.UserServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserServiceInterface userService;

    private UserResponse sampleResponse;
    private UserRequest validRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = new UserResponse(1L, "Niro Test", "niro@test.com", "POL-001");
        validRequest = new UserRequest("Niro Test", "niro@test.com", "POL-001");
    }

    @Nested
    @DisplayName("GET /users")
    class GetAllUsers {

        @Test
        @DisplayName("should return 200 with list of users")
        void shouldReturn200WithUsers() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].email").value("niro@test.com"))
                    .andExpect(jsonPath("$[0].name").value("Niro Test"));
        }

        @Test
        @DisplayName("should return empty list when no users exist")
        void shouldReturnEmptyList() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of());

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /users/{id}")
    class GetUserById {

        @Test
        @DisplayName("should return 200 with user when found")
        void shouldReturn200WhenFound() throws Exception {
            when(userService.getUserById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.email").value("niro@test.com"));
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(userService.getUserById(99L))
                    .thenThrow(new UserNotFoundException(99L));

            mockMvc.perform(get("/users/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("POST /users")
    class CreateUser {

        @Test
        @DisplayName("should return 201 when valid request")
        void shouldReturn201WithValidRequest() throws Exception {
            when(userService.createUser(any(UserRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("should return 400 when email is invalid")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            UserRequest badRequest = new UserRequest("Niro", "not-an-email", "POL-001");

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            UserRequest badRequest = new UserRequest("", "niro@test.com", "POL-001");

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /users/{id}")
    class DeleteUser {

        @Test
        @DisplayName("should return 204 when user deleted")
        void shouldReturn204WhenDeleted() throws Exception {
            doNothing().when(userService).deleteUser(1L);

            mockMvc.perform(delete("/users/1"))
                    .andExpect(status().isNoContent());

            verify(userService, times(1)).deleteUser(1L);
        }
    }
}