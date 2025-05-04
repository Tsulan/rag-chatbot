package com.example.rag_chatbot.azureaisearch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubChunk {
    public final String id;
    public final String text;
    public final String parentMasterChunkId;
}
