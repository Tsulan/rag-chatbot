package com.example.rag_chatbot.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatException extends RuntimeException {

    private String chatResponse;

}
