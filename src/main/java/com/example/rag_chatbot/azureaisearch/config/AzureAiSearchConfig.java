package com.example.rag_chatbot.azureaisearch.config;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureAiSearchConfig {

    @Value("${azure.ai.search.admin.key}")
    private String adminKey;

    @Value("${azure.ai.search.query.key}")
    private String searchKey;

    @Value("${azure.ai.search.url}")
    private String searchEndpoint;

    @Bean
    public SearchIndexClient searchIndexClient() {
        return new SearchIndexClientBuilder()
                .endpoint(searchEndpoint)
                .credential(new AzureKeyCredential(adminKey))
                .buildClient();
    }

    @Bean
    public SearchClientBuilder searchClientSearchKey() {
        return new SearchClientBuilder()
                .endpoint(searchEndpoint)
                .credential(new AzureKeyCredential(searchKey));
    }

    @Bean
    public SearchClientBuilder searchClientAdminKey() {
        return new SearchClientBuilder()
                .endpoint(searchEndpoint)
                .credential(new AzureKeyCredential(adminKey));
    }
}
