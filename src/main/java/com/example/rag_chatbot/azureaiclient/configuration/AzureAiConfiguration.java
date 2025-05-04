package com.example.rag_chatbot.azureaiclient.configuration;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureAiConfiguration {

    @Value("${azure.openai.api.url}")
    private String azureAiUrl;

    @Value("${azure.openai.api.key}")
    private String openAiApiKey;

    @Bean
    public OpenAIClient openAIClient() {
        return new OpenAIClientBuilder()
                .endpoint(azureAiUrl)
                .credential(new AzureKeyCredential(openAiApiKey))
                .buildClient();
    }
}
