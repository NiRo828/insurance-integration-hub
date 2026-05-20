package com.insurance.user_service.service;

import com.insurance.user_service.dto.UserRequest;
import com.insurance.user_service.dto.UserResponse;
import java.util.List;

public interface UserServiceInterface {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}