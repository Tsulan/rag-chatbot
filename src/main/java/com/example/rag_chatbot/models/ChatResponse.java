package com.example.rag_chatbot.models;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class ChatResponse {

    private String chatResponse;

    private String userQuery;
}
