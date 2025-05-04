package com.example.rag_chatbot.azureaisearch.services;

import com.example.rag_chatbot.azureaisearch.model.ChunkedDocument;
import com.example.rag_chatbot.azureaisearch.model.MasterChunk;
import com.example.rag_chatbot.azureaisearch.model.SubChunk;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ChunkSplitter {

    private static final int MASTER_CHUNKS_COUNT = 2;
    private static final int SUB_CHUNKS_PER_MASTER = 2;
    private static final AtomicInteger docIdCounter = new AtomicInteger(1);

    public ChunkedDocument splitMarkdownIntoChunks(String markdown, String pageName, String pageLink) {
        int documentId = docIdCounter.getAndIncrement();
        List<String> blocks = splitIntoBlocks(markdown);

        List<List<String>> masterBlocks =
                splitEquallyByBlocks(blocks, MASTER_CHUNKS_COUNT);

        List<MasterChunk> masterChunks = new ArrayList<>();
        for (int i = 0; i < masterBlocks.size(); i++) {
            String masterId = documentId + "-" + (i + 1);
            String masterText = String.join("\n\n", masterBlocks.get(i));

            List<List<String>> subBlocks =
                    splitEquallyByBlocks(masterBlocks.get(i), SUB_CHUNKS_PER_MASTER);

            List<SubChunk> subChunks = new ArrayList<>();
            for (int j = 0; j < subBlocks.size(); j++) {
                String subId = masterId + "-" + (j + 1);
                String subText = String.join("\n\n", subBlocks.get(j));
                subChunks.add(new SubChunk(subId, subText, masterId));
            }

            masterChunks.add(new MasterChunk(masterId, masterText, subChunks));
        }

        return new ChunkedDocument(String.valueOf(documentId), pageName, pageLink, masterChunks);
    }

    private List<String> splitIntoBlocks(String markdown) {
        String boundaryRegex = "(?m)(?:\\r?\\n\\s*\\r?\\n)|" +
                "(?=^#{1,6}\\s)|(?=^[-*+]\\s)|(?=^\\d+\\.\\s)";
        String[] raw = markdown.split(boundaryRegex);

        List<String> blocks = new ArrayList<>();
        for (String part : raw) {
            String t = part.trim();
            if (!t.isEmpty()) {
                blocks.add(t);
            }
        }
        return blocks;
    }

    private List<List<String>> splitEquallyByBlocks(
            List<String> blocks, int parts) {

        List<List<String>> result = new ArrayList<>();
        int total = blocks.size();
        int base = total / parts;
        int rem = total % parts;
        int cursor = 0;

        for (int i = 0; i < parts; i++) {
            int thisSize = base + (i < rem ? 1 : 0);
            int end = cursor + thisSize;

            end = Math.min(end, total);
            result.add(new ArrayList<>(blocks.subList(cursor, end)));
            cursor = end;
        }
        return result;
    }
}

