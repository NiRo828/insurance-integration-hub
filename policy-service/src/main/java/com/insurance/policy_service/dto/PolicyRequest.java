package com.insurance.policy_service.dto;

import com.insurance.policy_service.model.PolicyStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Policy type is required")
    private String policyType;

    @NotNull(message = "Premium is required")
    private Double premium;

    private LocalDate startDate;
    private PolicyStatus status;
}