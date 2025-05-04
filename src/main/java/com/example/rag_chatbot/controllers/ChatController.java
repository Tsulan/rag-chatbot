package com.example.rag_chatbot.controllers;

import com.example.rag_chatbot.models.ChatRequest;
import com.example.rag_chatbot.models.ChatResponse;
import com.example.rag_chatbot.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aiAssistant")
@CrossOrigin(origins = "${cors.allowed.origin:*}")
@RequiredArgsConstructor
@Validated
public class ChatController {

    private final ChatService chatService;

    @PostMapping(value = "/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.chat(request));
    }
}
