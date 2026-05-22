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

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentService {

    private final UserServiceInterface userService;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key}")
    private String anthropicApiKey;

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-haiku-4-5-20251001";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public AgentResponse query(AgentRequest request) {
        // Step 1 - Build context from real data
        String context = buildContext(request.getUserId());

        // Step 2 - Build prompt
        String prompt = buildPrompt(context, request.getQuestion());

        // Step 3 - Call Claude API
        String answer = callClaude(prompt);

        return AgentResponse.builder()
                .answer(answer)
                .context(context)
                .userId(request.getUserId())
                .build();
    }

    private String buildContext(Long userId) {
        if (userId == null) {
            List<UserResponse> users = userService.getAllUsers();
            return "All users in the system: " + users.toString();
        }
        try {
            UserResponse user = userService.getUserById(userId);
            return "User details: " + user.toString();
        } catch (UserNotFoundException e) {
            return "No user found with id: " + userId;
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
            OkHttpClient client = new OkHttpClient();

            String requestBody = objectMapper.writeValueAsString(
                    new java.util.HashMap<>() {{
                        put("model", MODEL);
                        put("max_tokens", 1024);
                        put("messages", List.of(
                                new java.util.HashMap<>() {{
                                    put("role", "user");
                                    put("content", prompt);
                                }}
                        ));
                    }}
            );

            Request httpRequest = new Request.Builder()
                    .url(CLAUDE_API_URL)
                    .post(RequestBody.create(requestBody, JSON))
                    .addHeader("x-api-key", anthropicApiKey)
                    .addHeader("anthropic-version", "2023-06-01")
                    .addHeader("content-type", "application/json")
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Claude API error: {}", response.code());
                    return "AI service temporarily unavailable";
                }
                String responseBody = response.body().string();
                JsonNode root = objectMapper.readTree(responseBody);
                return root.path("content").get(0).path("text").asText();
            }
        } catch (Exception e) {
            log.error("Error calling Claude API: {}", e.getMessage());
            return "Error processing your question: " + e.getMessage();
        }
    }
}