package com.insurance.policy_service.client;

import com.insurance.policy_service.dto.UserDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient userWebClient;

    public Optional<UserDetailsResponse> getUserById(Long userId) {
        try {
            UserDetailsResponse user = userWebClient
                    .get()
                    .uri("/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserDetailsResponse.class)
                    .block();
            return Optional.ofNullable(user);
        } catch (WebClientResponseException.NotFound e) {
            log.warn("User not found with id: {}", userId);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error calling user-service for userId {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }
}