package com.insurance.user_service.mapper;

import com.insurance.user_service.dto.UserRequest;
import com.insurance.user_service.dto.UserResponse;
import com.insurance.user_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .policyNumber(user.getPolicyNumber())
                .build();
    }

    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .policyNumber(request.getPolicyNumber())
                .build();
    }
}