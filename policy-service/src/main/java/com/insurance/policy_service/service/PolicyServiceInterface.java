package com.insurance.policy_service.service;

import com.insurance.policy_service.dto.PolicyRequest;
import com.insurance.policy_service.dto.PolicyResponse;
import java.util.List;

public interface PolicyServiceInterface {
    List<PolicyResponse> getAllPolicies();
    PolicyResponse getPolicyById(Long id);
    List<PolicyResponse> getPoliciesByUserId(Long userId);
    PolicyResponse createPolicy(PolicyRequest request);
    PolicyResponse updatePolicy(Long id, PolicyRequest request);
    void deletePolicy(Long id);
}