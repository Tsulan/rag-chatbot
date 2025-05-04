package com.example.rag_chatbot.azureaisearch.services;

import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexService {

    @Value("${azure.openai.embedding.dimensions}")
    private Integer vectorSearchDimension;

    @Value("${azure.openai.search.default.index.name}")
    private String defaultIndex;

    private final SearchIndexClient searchIndexClient;
    private final ObjectMapper objectMapper;

    public SearchIndex create(final String indexName) {
        if (StringUtils.isBlank(indexName)) {
            throw new IllegalArgumentException("The index name was not provided.");
        }
        SearchIndex newIndex = generateIndex(indexName);
        return searchIndexClient.createIndex(newIndex);
    }

    private SearchIndex generateIndex(final String indexName) {
        return new SearchIndex(indexName)
                .setFields(List.of(
                                new SearchField("id", SearchFieldDataType.STRING)
                                        .setKey(true),
                                new SearchField("pageLink", SearchFieldDataType.STRING),
                                new SearchField("subChunkVector", SearchFieldDataType.collection(SearchFieldDataType.SINGLE))
                                        .setSearchable(true)
                                        .setVectorSearchDimensions(vectorSearchDimension)
                                        .setVectorSearchProfileName(defaultIndex),
                                new SearchField("masterChunk", SearchFieldDataType.STRING)
                        )
                )
                .setVectorSearch(new VectorSearch()
                        .setAlgorithms(Collections.singletonList(new HnswAlgorithmConfiguration("use-hnsw")))
                        .setProfiles(Collections.singletonList(
                                new VectorSearchProfile(defaultIndex, "use-hnsw")))
                );
    }

}
