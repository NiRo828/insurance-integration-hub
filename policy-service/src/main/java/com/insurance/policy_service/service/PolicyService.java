package com.insurance.policy_service.service;

import com.insurance.policy_service.dto.PolicyRequest;
import com.insurance.policy_service.dto.PolicyResponse;
import com.insurance.policy_service.exception.PolicyNotFoundException;
import com.insurance.policy_service.mapper.PolicyMapper;
import com.insurance.policy_service.model.Policy;
import com.insurance.policy_service.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService implements PolicyServiceInterface {

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;

    @Override
    public List<PolicyResponse> getAllPolicies() {
        return policyRepository.findAll()
                .stream()
                .map(policyMapper::toResponse)
                .toList();
    }

    @Override
    public PolicyResponse getPolicyById(Long id) {
        return policyMapper.toResponse(findPolicyById(id));
    }

    @Override
    public List<PolicyResponse> getPoliciesByUserId(Long userId) {
        return policyRepository.findByUserId(userId)
                .stream()
                .map(policyMapper::toResponse)
                .toList();
    }

    @Override
    public PolicyResponse createPolicy(PolicyRequest request) {
        Policy policy = policyMapper.toEntity(request);
        return policyMapper.toResponse(policyRepository.save(policy));
    }

    @Override
    public PolicyResponse updatePolicy(Long id, PolicyRequest request) {
        Policy existing = findPolicyById(id);
        existing.setPolicyType(request.getPolicyType());
        existing.setPremium(request.getPremium());
        existing.setStartDate(request.getStartDate());
        existing.setStatus(request.getStatus());
        return policyMapper.toResponse(policyRepository.save(existing));
    }

    @Override
    public void deletePolicy(Long id) {
        findPolicyById(id);
        policyRepository.deleteById(id);
    }

    private Policy findPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
    }
}