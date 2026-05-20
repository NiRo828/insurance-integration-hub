package com.insurance.policy_service.dto;

import com.insurance.policy_service.model.PolicyStatus;
import java.time.LocalDate;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyResponse {
    private Long id;
    private Long userId;
    private String policyType;
    private Double premium;
    private LocalDate startDate;
    private PolicyStatus status;
}