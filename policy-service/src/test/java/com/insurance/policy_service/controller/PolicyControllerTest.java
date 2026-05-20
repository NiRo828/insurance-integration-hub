package com.insurance.policy_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.policy_service.dto.PolicyRequest;
import com.insurance.policy_service.dto.PolicyResponse;
import com.insurance.policy_service.exception.PolicyNotFoundException;
import com.insurance.policy_service.model.PolicyStatus;
import com.insurance.policy_service.service.PolicyServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
@DisplayName("PolicyController Tests")
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PolicyServiceInterface policyService;

    private PolicyResponse sampleResponse;
    private PolicyRequest validRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = new PolicyResponse(
                1L, 1L, "Life Insurance", 250.0,
                LocalDate.of(2026, 1, 1), PolicyStatus.ACTIVE);
        validRequest = new PolicyRequest(
                1L, "Life Insurance", 250.0,
                LocalDate.of(2026, 1, 1), PolicyStatus.ACTIVE);
    }

    @Nested
    @DisplayName("GET /policies")
    class GetAllPolicies {

        @Test
        @DisplayName("should return 200 with list of policies")
        void shouldReturn200WithPolicies() throws Exception {
            when(policyService.getAllPolicies()).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/policies"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].policyType").value("Life Insurance"))
                    .andExpect(jsonPath("$[0].status").value("ACTIVE"));
        }

        @Test
        @DisplayName("should return empty list when no policies exist")
        void shouldReturnEmptyList() throws Exception {
            when(policyService.getAllPolicies()).thenReturn(List.of());

            mockMvc.perform(get("/policies"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /policies/{id}")
    class GetPolicyById {

        @Test
        @DisplayName("should return 200 when policy found")
        void shouldReturn200WhenFound() throws Exception {
            when(policyService.getPolicyById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/policies/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.premium").value(250.0));
        }

        @Test
        @DisplayName("should return 404 when policy not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(policyService.getPolicyById(99L))
                    .thenThrow(new PolicyNotFoundException(99L));

            mockMvc.perform(get("/policies/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /policies/user/{userId}")
    class GetPoliciesByUserId {

        @Test
        @DisplayName("should return policies for a given user")
        void shouldReturnUserPolicies() throws Exception {
            when(policyService.getPoliciesByUserId(1L)).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/policies/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].userId").value(1L));
        }
    }

    @Nested
    @DisplayName("POST /policies")
    class CreatePolicy {

        @Test
        @DisplayName("should return 201 when valid request")
        void shouldReturn201WithValidRequest() throws Exception {
            when(policyService.createPolicy(any(PolicyRequest.class))).thenReturn(sampleResponse);

            mockMvc.perform(post("/policies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("should return 400 when userId is null")
        void shouldReturn400WhenUserIdNull() throws Exception {
            PolicyRequest badRequest = new PolicyRequest(
                    null, "Life Insurance", 250.0,
                    LocalDate.of(2026, 1, 1), PolicyStatus.ACTIVE);

            mockMvc.perform(post("/policies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when policyType is blank")
        void shouldReturn400WhenPolicyTypeBlank() throws Exception {
            PolicyRequest badRequest = new PolicyRequest(
                    1L, "", 250.0,
                    LocalDate.of(2026, 1, 1), PolicyStatus.ACTIVE);

            mockMvc.perform(post("/policies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /policies/{id}")
    class DeletePolicy {

        @Test
        @DisplayName("should return 204 when policy deleted")
        void shouldReturn204WhenDeleted() throws Exception {
            doNothing().when(policyService).deletePolicy(1L);

            mockMvc.perform(delete("/policies/1"))
                    .andExpect(status().isNoContent());

            verify(policyService, times(1)).deletePolicy(1L);
        }
    }

    // @Test
    // @DisplayName("should return 404 when deleting non-existent policy")
    // void shouldReturn404WhenDeletingNonExistent() throws Exception {
    //     doThrow(new PolicyNotFoundException(99L)).when(policyService).deletePolicy(99L);

    //     mockMvc.perform(delete("/policies/99"))
    //             .andExpect(status().isNotFound())
    //             .andExpect(jsonPath("$.status").value(404));
    // }
}