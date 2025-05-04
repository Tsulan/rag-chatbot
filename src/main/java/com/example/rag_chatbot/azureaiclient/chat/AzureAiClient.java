package com.example.rag_chatbot.azureaiclient.chat;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import com.azure.core.util.IterableStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AzureAiClient {

    @Value("${azure.openai.max.output.tokens}")
    private Integer maxOutputTokens;

    @Value("${azure.openai.llm.model}")
    private String llmModel;

    @Value("${azure.openai.embedding.model}")
    private String embeddingModel;

    @Value("${azure.openai.llm.temperature}")
    private Double temperature;

    @Value("${azure.openai.embedding.dimensions}")
    private Integer vectorSearchDimension;

    private final OpenAIClient openAIClient;

    public IterableStream<ChatCompletions> generateFullAnswer(final List<ChatRequestMessage> messages) {
        ChatCompletionsOptions options = new ChatCompletionsOptions(messages)
                .setTemperature(temperature)
                .setMaxTokens(maxOutputTokens);

        return openAIClient.getChatCompletionsStream(llmModel, options);
    }

    public String rephraseUserPrompt(final List<ChatRequestMessage> chatMessages) {
        ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages)
                .setTemperature(temperature)
                .setMaxTokens(maxOutputTokens);

        ChatCompletions completions = openAIClient.getChatCompletions(llmModel, options);

        return completions.getChoices().get(0).getMessage().getContent();
    }

    public Embeddings generateEmbeddings(final List<String> content) {
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(content);
        embeddingsOptions.setUser("rag-chatbot");
        embeddingsOptions.setModel(embeddingModel);
        embeddingsOptions.setInputType("text");
        embeddingsOptions.setDimensions(vectorSearchDimension);

        return openAIClient.getEmbeddings(embeddingModel, embeddingsOptions);
    }
}
