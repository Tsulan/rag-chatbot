package com.example.rag_chatbot.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatRequest {

    private String userQuery;

    private List<ChatHistory> chatHistory;

}
