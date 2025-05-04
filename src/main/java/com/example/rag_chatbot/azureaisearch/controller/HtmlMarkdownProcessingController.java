package com.example.rag_chatbot.azureaisearch.controller;

import com.example.rag_chatbot.azureaisearch.services.HtmlMarkdownProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/util")
@RequiredArgsConstructor
@Slf4j
public class HtmlMarkdownProcessingController {

    private final HtmlMarkdownProcessingService htmlMarkdownProcessingService;

    @PostMapping("/clean-html")
    public ResponseEntity<String> cleanHtml(@RequestBody String html) {
        try {
            String result = htmlMarkdownProcessingService.cleanHtml(html);
            log.info("HTML cleaned successfully");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error during HTML clean: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error during HTML clean");
        }
    }

    @PostMapping("/convert-md")
    public ResponseEntity<String> convertHtmlToMarkdown(@RequestBody String html) {
        try {
            String result = htmlMarkdownProcessingService.convertToMarkdown(html);
            log.info("HTML successfully converted to Markdown");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error during HTML to Markdown conversion: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error during HTML to Markdown conversion");
        }
    }
}
