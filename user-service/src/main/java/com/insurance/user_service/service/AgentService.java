package com.insurance.user_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.user_service.dto.AgentRequest;
import com.insurance.user_service.dto.AgentResponse;
import com.insurance.user_service.dto.UserResponse;
import com.insurance.user_service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentService {

    private final UserServiceInterface userService;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;

    @Value("${anthropic.api.key}")
    private String anthropicApiKey;

    @Value("${services.policy-service.url}")
    private String policyServiceUrl;

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-haiku-4-5-20251001";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public AgentResponse query(AgentRequest request) {
        String context = buildContext(request.getUserId());
        String prompt = buildPrompt(context, request.getQuestion());
        String answer = callClaude(prompt);

        return AgentResponse.builder()
                .answer(answer)
                .context(context)
                .userId(request.getUserId())
                .build();
    }

    private String buildContext(Long userId) {
        if (userId == null) {
            return userService.getAllUsers().stream()
                    .map(user -> "User: %s\nPolicies: %s".formatted(
                            user, fetchPoliciesForUser(user.getId())))
                    .collect(Collectors.joining("\n---\n", "All users:\n", ""));
        }
        try {
            UserResponse user = userService.getUserById(userId);
            return "User: %s\nPolicies: %s".formatted(user, fetchPoliciesForUser(userId));
        } catch (UserNotFoundException e) {
            return "No user found with id: " + userId;
        }
    }

    private String fetchPoliciesForUser(Long userId) {
        try {
            Request request = new Request.Builder()
                    .url(policyServiceUrl + "/policies/user/" + userId)
                    .get()
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful() && response.body() != null
                        ? response.body().string()
                        : "No policies found";
            }
        } catch (Exception e) {
            log.warn("Could not fetch policies for userId {}: {}", userId, e.getMessage());
            return "Policy service unavailable";
        }
    }

    private String buildPrompt(String context, String question) {
        return """
                You are an AI assistant for an insurance management system.
                Use only the following data to answer the question.
                If the answer cannot be found in the data, say so clearly.
                
                DATA:
                %s
                
                QUESTION:
                %s
                
                Answer concisely and professionally.
                """.formatted(context, question);
    }

    private String callClaude(String prompt) {
        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "model", MODEL,
                    "max_tokens", 1024,
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", prompt
                    ))
            ));

            Request httpRequest = new Request.Builder()
                    .url(CLAUDE_API_URL)
                    .post(RequestBody.create(requestBody, JSON))
                    .addHeader("x-api-key", anthropicApiKey)
                    .addHeader("anthropic-version", "2023-06-01")
                    .addHeader("content-type", "application/json")
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    log.error("Claude API error: {}", response.code());
                    return "AI service temporarily unavailable";
                }
                JsonNode root = objectMapper.readTree(response.body().string());
                return root.path("content").get(0).path("text").asText();
            }
        } catch (Exception e) {
            log.error("Error calling Claude API: {}", e.getMessage());
            return "Error processing your question: " + e.getMessage();
        }
    }
}