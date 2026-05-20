package com.insurance.policy_service.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPolicyDetailsResponse {
    private UserDetailsResponse user;
    private List<PolicyResponse> policies;
    private int totalPolicies;
    private double totalPremium;
}