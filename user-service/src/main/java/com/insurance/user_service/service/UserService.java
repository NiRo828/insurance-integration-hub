package com.insurance.user_service.service;

import com.insurance.user_service.dto.UserRequest;
import com.insurance.user_service.dto.UserResponse;
import com.insurance.user_service.exception.UserNotFoundException;
import com.insurance.user_service.mapper.UserMapper;
import com.insurance.user_service.model.User;
import com.insurance.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(findUserById(id));
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        User user = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User existing = findUserById(id);
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPolicyNumber(request.getPolicyNumber());
        return userMapper.toResponse(userRepository.save(existing));
    }

    @Override
    public void deleteUser(Long id) {
        findUserById(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<UserResponse> getUsersByPolicyNumber(String policyNumber) {
        return userRepository.findByPolicyNumber(policyNumber)
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}