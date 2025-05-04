package com.example.rag_chatbot.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatHistory {

    private Boolean isUser;

    private String content;
}
