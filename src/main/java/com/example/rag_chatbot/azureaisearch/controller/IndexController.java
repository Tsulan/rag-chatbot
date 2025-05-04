package com.example.rag_chatbot.azureaisearch.controller;

import com.azure.search.documents.indexes.models.SearchIndex;
import com.azure.search.documents.models.IndexingResult;
import com.example.rag_chatbot.azureaisearch.model.ChunkedDocument;
import com.example.rag_chatbot.azureaisearch.model.QueryChunk;
import com.example.rag_chatbot.azureaisearch.services.ChunkSplitter;
import com.example.rag_chatbot.azureaisearch.services.DocumentsService;
import com.example.rag_chatbot.azureaisearch.services.HtmlMarkdownProcessingService;
import com.example.rag_chatbot.azureaisearch.services.IndexService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@RestController
@RequestMapping("/indexes")
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    private final IndexService indexService;
    private final DocumentsService documentsService;
    private final HtmlMarkdownProcessingService htmlMarkdownProcessingService;
    private final ChunkSplitter chunkSplitter;
    private final ObjectMapper objectMapper;

    @PostMapping("/create")
    public ResponseEntity<SearchIndex> createIndex(@RequestParam String indexName) {
        return ResponseEntity.ok(indexService.create(indexName));
    }

    @PostMapping("/upload-html-document")
    public ResponseEntity<List<IndexingResult>> uploadHtmlDoc(
            @RequestParam String indexName,
            @RequestParam String pageName,
            @RequestParam String pageLink,
            @RequestBody String html) {
        try {
            log.info("Start loading HTML document: pageName={}, pageLink={}, indexName={}", pageName, pageLink, indexName);

            String cleanedHtml = htmlMarkdownProcessingService.cleanHtml(html);
            log.info("HTML has been successfully cleaned");

            String markdown = htmlMarkdownProcessingService.convertToMarkdown(cleanedHtml);
            log.info("HTML has been successfully converted to Markdown");

            ChunkedDocument chunkedDocument = chunkSplitter.splitMarkdownIntoChunks(markdown, pageName, pageLink);
            log.info("Markdown has been successfully split into chunks");

            List<QueryChunk> queryChunks = QueryChunk.toQueryChunks(chunkedDocument);
            log.info("Chunks have been successfully converted to QueryChunk");

            List<IndexingResult> results = documentsService.uploadDocuments(indexName, queryChunks);
            log.info("The documents have been successfully uploaded to Azure Vector Search. Quantity: {}", results.size());

            log.info("Saving the document to disk");
            Path dir = Paths.get("src/main/resources/documentation");
            Path jsonPath = dir.resolve(chunkedDocument.getDocumentId() + " " + pageName + ".json");
            Path mdPath = dir.resolve(chunkedDocument.getDocumentId() + " " + pageName + ".md");

            try {
                Files.createDirectories(dir);
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonPath.toFile(), chunkedDocument);
                Files.write(mdPath, markdown.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error when loading a document: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
