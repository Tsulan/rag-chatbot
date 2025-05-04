package com.example.rag_chatbot.azureaisearch.utils;

import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.Embeddings;
import com.azure.search.documents.SearchDocument;
import com.example.rag_chatbot.azureaisearch.model.QueryChunk;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class VectorDbUtils {
    public static SearchDocument generateSearchDocumentFromChunk(final QueryChunk chunk) {
        SearchDocument doc = new SearchDocument();
        doc.put("id", chunk.getId());
        doc.put("pageLink", chunk.getPageLink());
        doc.put("masterChunk", chunk.getMasterChunk());
        return doc;
    }

    public static void updateDocsWithEmbeddedData(final List<SearchDocument> searchDocuments, final String field, final Embeddings embeddings) {
        int i = 0;
        for (EmbeddingItem item : embeddings.getData()) {
            searchDocuments.get(i).put(field, item.getEmbedding());
            i++;
        }
    }
}

