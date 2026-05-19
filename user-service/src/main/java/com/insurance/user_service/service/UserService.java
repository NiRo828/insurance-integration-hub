package com.insurance.user_service.service;

import com.insurance.user_service.dto.UserRequest;
import com.insurance.user_service.dto.UserResponse;
import com.insurance.user_service.exception.UserNotFoundException;
import com.insurance.user_service.model.User;
import com.insurance.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return toResponse(findUserById(id));
    }

    public UserResponse createUser(UserRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .policyNumber(request.getPolicyNumber())
                .build();
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User existing = findUserById(id);
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPolicyNumber(request.getPolicyNumber());
        return toResponse(userRepository.save(existing));
    }

    public void deleteUser(Long id) {
        findUserById(id);
        userRepository.deleteById(id);
    }

    // Private helpers
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .policyNumber(user.getPolicyNumber())
                .build();
    }
}