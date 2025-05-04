package com.example.rag_chatbot.azureaisearch.services;

import com.azure.ai.openai.models.Embeddings;
import com.azure.core.util.Context;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.models.*;
import com.azure.search.documents.util.SearchPagedIterable;
import com.example.rag_chatbot.azureaiclient.chat.AzureAiClient;
import com.example.rag_chatbot.azureaisearch.model.QueryChunk;
import com.example.rag_chatbot.azureaisearch.utils.VectorDbUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentsService {
    private final SearchClientBuilder searchClientSearchKey;
    private final SearchClientBuilder searchClientAdminKey;
    private final AzureAiClient azureAiClient;

    @Value("${azure.openai.search.max.num.of.docs.received}")
    private Integer topSearch;

    public List<IndexingResult> uploadDocuments(final String indexName, final List<QueryChunk> chunks) {
        if (chunks.isEmpty()) {
            return Collections.emptyList();
        }
        List<SearchDocument> searchDocuments = new ArrayList<>();
        List<String> subChunks = new ArrayList<>();
        for (QueryChunk chunk : chunks) {
            SearchDocument doc = VectorDbUtils.generateSearchDocumentFromChunk(chunk);
            subChunks.add(chunk.getSubChunk());
            searchDocuments.add(doc);
        }

        Embeddings embeddedSubChunks = azureAiClient.generateEmbeddings(subChunks);
        VectorDbUtils.updateDocsWithEmbeddedData(searchDocuments, "subChunkVector", embeddedSubChunks);

        SearchClient searchClient = getSearchClient(indexName, searchClientAdminKey);
        return searchClient.uploadDocuments(searchDocuments).getResults();
    }

    public List<SearchDocument> getDocuments(final String indexName, final String searchText, final Integer topSearchResults, final boolean hybridSearch) {
        SearchClient searchClient = getSearchClient(indexName, searchClientSearchKey);
        SearchOptions searchOptions = getSearchOptions(searchText, topSearchResults);
        SearchPagedIterable searchResults = searchClient.search(hybridSearch ? searchText : null, searchOptions, Context.NONE);

        if (searchResults.stream().findAny().isEmpty()) {
            return Collections.emptyList();
        }

        List<SearchDocument> docs = new ArrayList<>();
        for (SearchResult searchResult : searchResults) {
            SearchDocument doc = searchResult.getDocument(SearchDocument.class);
            docs.add(doc);
            log.info("Query: {}, score: {}, doc id: {}", searchText, searchResult.getScore(), doc.get("id"));
        }
        return docs;
    }

    private SearchOptions getSearchOptions(final String searchText, final Integer topSearchResults) {
        SearchOptions searchOptions = new SearchOptions();
        searchOptions.setTop(topSearchResults == null ? topSearch : topSearchResults);

        if (Objects.nonNull(searchText)) {
            List<Float> embedding = getEmbeddingFromQuery(searchText);
            searchOptions.setVectorSearchOptions(
                    new VectorSearchOptions()
                            .setQueries(new VectorizedQuery(embedding)
                                    .setFields("subChunkVector")
                                    .setExhaustive(true)
                            )
            );
        }
        return searchOptions;
    }

    private List<Float> getEmbeddingFromQuery(final String searchText) {
        Embeddings embeddings = azureAiClient.generateEmbeddings(Collections.singletonList(searchText));
        return new ArrayList<>(embeddings
                .getData()
                .get(0)
                .getEmbedding());
    }

    public SearchClient getSearchClient(final String indexName, final SearchClientBuilder searchClientBuilder) {
        return searchClientBuilder.indexName(indexName).buildClient();
    }
}
