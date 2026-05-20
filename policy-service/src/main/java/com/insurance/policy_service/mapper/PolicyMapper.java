package com.insurance.policy_service.mapper;

import com.insurance.policy_service.dto.PolicyResponse;
import com.insurance.policy_service.dto.PolicyRequest;
import com.insurance.policy_service.model.Policy;
import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {

    public PolicyResponse toResponse(Policy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .userId(policy.getUserId())
                .policyType(policy.getPolicyType())
                .premium(policy.getPremium())
                .startDate(policy.getStartDate())
                .status(policy.getStatus())
                .build();
    }

    public Policy toEntity(PolicyRequest request) {
        return Policy.builder()
                .userId(request.getUserId())
                .policyType(request.getPolicyType())
                .premium(request.getPremium())
                .startDate(request.getStartDate())
                .status(request.getStatus())
                .build();
    }
}