package com.insurance.user_service.controller;

import com.insurance.user_service.dto.AgentRequest;
import com.insurance.user_service.dto.AgentResponse;
import com.insurance.user_service.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/query")
    public AgentResponse query(@Valid @RequestBody AgentRequest request) {
        return agentService.query(request);
    }
}