package com.example.rag_chatbot.azureaisearch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MasterChunk {
    public final String id;
    public final String text;
    public final List<SubChunk> subChunks;
}
