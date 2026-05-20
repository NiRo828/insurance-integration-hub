package com.insurance.policy_service.service;

import com.insurance.policy_service.dto.PolicyRequest;
import com.insurance.policy_service.dto.PolicyResponse;
import com.insurance.policy_service.exception.PolicyNotFoundException;
import com.insurance.policy_service.mapper.PolicyMapper;
import com.insurance.policy_service.model.Policy;
import com.insurance.policy_service.model.PolicyStatus;
import com.insurance.policy_service.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyService Tests")
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyMapper policyMapper;

    @InjectMocks
    private PolicyService policyService;

    private Policy policy;
    private PolicyResponse policyResponse;
    private PolicyRequest policyRequest;

    @BeforeEach
    void setUp() {
        policy = new Policy(
                1L, 1L, "Car Insurance", 500.0,
                LocalDate.of(2026, 1, 1), PolicyStatus.ACTIVE);
        policyResponse = new PolicyResponse(
                1L, 1L, "Car Insurance", 500.0,
                LocalDate.of(2026, 1, 1), PolicyStatus.ACTIVE);
        policyRequest = new PolicyRequest(
                1L, "Car Insurance", 500.0,
                LocalDate.of(2026, 1, 1), PolicyStatus.ACTIVE);
    }

    @Test
    @DisplayName("should return list of all policies")
    void getAllPolicies_shouldReturnListOfPolicies() {
        when(policyRepository.findAll()).thenReturn(List.of(policy));
        when(policyMapper.toResponse(policy)).thenReturn(policyResponse);

        List<PolicyResponse> result = policyService.getAllPolicies();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPolicyType()).isEqualTo("Car Insurance");
    }

    @Test
    @DisplayName("should return policy when found by id")
    void getPolicyById_shouldReturnPolicy_whenExists() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyMapper.toResponse(policy)).thenReturn(policyResponse);

        PolicyResponse result = policyService.getPolicyById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPolicyType()).isEqualTo("Car Insurance");
    }

    @Test
    @DisplayName("should throw PolicyNotFoundException when id not found")
    void getPolicyById_shouldThrowException_whenNotFound() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.getPolicyById(99L))
                .isInstanceOf(PolicyNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("should return policies for a given userId")
    void getPoliciesByUserId_shouldReturnUserPolicies() {
        when(policyRepository.findByUserId(1L)).thenReturn(List.of(policy));
        when(policyMapper.toResponse(policy)).thenReturn(policyResponse);

        List<PolicyResponse> result = policyService.getPoliciesByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should save and return policy on create")
    void createPolicy_shouldSaveAndReturnPolicy() {
        when(policyMapper.toEntity(policyRequest)).thenReturn(policy);
        when(policyRepository.save(policy)).thenReturn(policy);
        when(policyMapper.toResponse(policy)).thenReturn(policyResponse);

        PolicyResponse result = policyService.createPolicy(policyRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPolicyType()).isEqualTo("Car Insurance");
        verify(policyRepository, times(1)).save(policy);
    }

    @Test
    @DisplayName("should delete policy when exists")
    void deletePolicy_shouldDeletePolicy_whenExists() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        policyService.deletePolicy(1L);

        verify(policyRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("should throw PolicyNotFoundException when deleting non-existent policy")
    void deletePolicy_shouldThrowException_whenNotFound() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.deletePolicy(99L))
                .isInstanceOf(PolicyNotFoundException.class)
                .hasMessageContaining("99");
    }
}