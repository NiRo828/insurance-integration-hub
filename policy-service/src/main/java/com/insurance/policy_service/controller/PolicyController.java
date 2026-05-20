package com.insurance.policy_service.controller;

import com.insurance.policy_service.dto.PolicyRequest;
import com.insurance.policy_service.dto.PolicyResponse;
import com.insurance.policy_service.service.PolicyServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyServiceInterface policyService;

    @GetMapping
    public List<PolicyResponse> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    @GetMapping("/{id}")
    public PolicyResponse getPolicyById(@PathVariable Long id) {
        return policyService.getPolicyById(id);
    }

    @GetMapping("/user/{userId}")
    public List<PolicyResponse> getPoliciesByUserId(@PathVariable Long userId) {
        return policyService.getPoliciesByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyResponse createPolicy(@Valid @RequestBody PolicyRequest request) {
        return policyService.createPolicy(request);
    }

    @PutMapping("/{id}")
    public PolicyResponse updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyRequest request) {
        return policyService.updatePolicy(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
    }
}