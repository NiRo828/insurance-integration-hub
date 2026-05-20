package com.insurance.policy_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    private Long id;
    private String name;
    private String email;
    private String policyNumber;
}