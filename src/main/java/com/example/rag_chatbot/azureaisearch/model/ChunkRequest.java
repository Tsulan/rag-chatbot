package com.example.rag_chatbot.azureaisearch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChunkRequest {
    private String pageName;
    private String pageLink;
    private String markdownContent;
}
