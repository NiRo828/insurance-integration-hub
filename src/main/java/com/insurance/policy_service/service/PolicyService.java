package com.insurance.policy_service.service;

import com.insurance.policy_service.model.Policy;
import com.insurance.policy_service.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Policy getPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
    }

    public List<Policy> getPoliciesByUserId(Long userId) {
        return policyRepository.findByUserId(userId);
    }

    public Policy createPolicy(Policy policy) {
        return policyRepository.save(policy);
    }

    public Policy updatePolicy(Long id, Policy updated) {
        Policy existing = getPolicyById(id);
        existing.setPolicyType(updated.getPolicyType());
        existing.setPremium(updated.getPremium());
        existing.setStartDate(updated.getStartDate());
        existing.setStatus(updated.getStatus());
        return policyRepository.save(existing);
    }

    public void deletePolicy(Long id) {
        getPolicyById(id);
        policyRepository.deleteById(id);
    }
}