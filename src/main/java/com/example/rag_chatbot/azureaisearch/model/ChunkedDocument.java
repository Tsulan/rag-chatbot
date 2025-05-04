package com.example.rag_chatbot.azureaisearch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChunkedDocument {
    private String documentId;
    private String pageName;
    private String pageLink;
    public final List<MasterChunk> masterChunks;
}
