package com.example.rag_chatbot.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RephrasedUserInputResponse {

    private String summary;

    private Boolean isFollowUpRelevant;

    private String rephrasedUserInput;
}
