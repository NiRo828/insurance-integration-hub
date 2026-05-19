package com.insurance.policy_service.controller;

import com.insurance.policy_service.model.Policy;
import com.insurance.policy_service.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    @GetMapping("/{id}")
    public Policy getPolicyById(@PathVariable Long id) {
        return policyService.getPolicyById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Policy> getPoliciesByUserId(@PathVariable Long userId) {
        return policyService.getPoliciesByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Policy createPolicy(@Valid @RequestBody Policy policy) {
        return policyService.createPolicy(policy);
    }

    @PutMapping("/{id}")
    public Policy updatePolicy(@PathVariable Long id, @Valid @RequestBody Policy policy) {
        return policyService.updatePolicy(id, policy);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
    }
}