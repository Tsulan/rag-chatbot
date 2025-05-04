package com.example.rag_chatbot.azureaisearch.controller;

import com.example.rag_chatbot.azureaisearch.model.ChunkRequest;
import com.example.rag_chatbot.azureaisearch.model.ChunkedDocument;
import com.example.rag_chatbot.azureaisearch.model.QueryChunk;
import com.example.rag_chatbot.azureaisearch.services.ChunkSplitter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@RestController
@RequestMapping("/util/chunker")
public class ChunkController {

    private final ChunkSplitter chunkSplitter;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChunkController(ChunkSplitter chunkSplitter, ObjectMapper objectMapper) {
        this.chunkSplitter = chunkSplitter;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/splitPageIntoChunks")
    public ResponseEntity<ChunkedDocument> chunkMarkdown(@RequestBody ChunkRequest request) {
        String pageName = request.getPageName();
        String pageLink = request.getPageLink();
        String markdownContent = request.getMarkdownContent();

        ChunkedDocument chunks = chunkSplitter.splitMarkdownIntoChunks(markdownContent, pageName, pageLink);

        Path dir = Paths.get("src/main/resources/documentation");
        Path jsonPath = dir.resolve(pageName + ".json");
        Path mdPath = dir.resolve(pageName + ".md");

        try {
            Files.createDirectories(dir);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonPath.toFile(), chunks);
            Files.write(mdPath, markdownContent.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(chunks);
    }

    @PostMapping("/queryChunksFromDoc")
    public ResponseEntity<List<QueryChunk>> cleanHtml(@RequestBody ChunkRequest request) {
        String pageName = request.getPageName();
        String pageLink = request.getPageLink();
        String markdownContent = request.getMarkdownContent();
        ChunkedDocument chunks = chunkSplitter.splitMarkdownIntoChunks(markdownContent, pageName, pageLink);

        List<QueryChunk> queryChunks = QueryChunk.toQueryChunks(chunks);
        return ResponseEntity.ok(queryChunks);
    }
}

