package com.example.rag_chatbot.services;

import com.azure.ai.openai.models.*;
import com.azure.core.util.IterableStream;
import com.azure.search.documents.SearchDocument;
import com.example.rag_chatbot.azureaiclient.chat.AzureAiClient;
import com.example.rag_chatbot.azureaisearch.services.DocumentsService;
import com.example.rag_chatbot.exceptions.ChatException;
import com.example.rag_chatbot.models.ChatHistory;
import com.example.rag_chatbot.models.ChatRequest;
import com.example.rag_chatbot.models.ChatResponse;
import com.example.rag_chatbot.models.RephrasedUserInputResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.rag_chatbot.utils.ChatUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    @Value("${azure.openai.max.input.tokens}")
    private Integer maxInputTokens;

    @Value("${azure.openai.max.history.summarization.tokens}")
    private Integer maxHistoryTokens;

    @Value("${azure.openai.max.query.context.tokens}")
    private Integer maxQueryContextTokens;

    @Value("${azure.openai.max.query.model.tokens}")
    private Integer maxQueryModelTokens;

    @Value("${azure.openai.search.max.num.of.docs.received}")
    private Integer maxNumOfMasterChunksInContext;

    @Value("${azure.openai.search.hybrid.search}")
    private boolean isHybridSearchEnabled;

    @Value("${azure.openai.search.default.index.name}")
    private String defaultIndex;

    @Value("${chat.exception.too.many.characters.message}")
    private String tooManyCharactersErrorMessage;

    private final AzureAiClient azureAiClient;
    private final DocumentsService documentsService;

    public ChatResponse chat(final ChatRequest request) {
        final String userQuery = request.getUserQuery();
        final String requestedIndexName = defaultIndex;

        validateAmountOfTokens(userQuery);

        List<ChatHistory> chatHistory = request.getChatHistory();
        String summarizedUserQuery = getSummarizedUserQuery(userQuery, chatHistory);
        log.info("Initial user query: {}", userQuery);
        log.info("Summarized user query: {}", summarizedUserQuery);
        Set<String> topResults = getTopMatchingDbResults(summarizedUserQuery, requestedIndexName);
        List<String> contextSections = buildMaxQueryContext(topResults.stream().toList());
        log.info("contextSections.size(): {}", contextSections.size());

        List<ChatRequestMessage> messagesArray = constructQueryPromptWithHistory(contextSections, userQuery, chatHistory);
        log.info("messagesArray.size(): {}", messagesArray.size());
        logMessagesArray(messagesArray);

        ChatResponse chatResponse = ChatResponse.builder()
                .chatResponse(processResponse(messagesArray))
                .userQuery(userQuery)
                .build();

        String chatAnswer = chatResponse.getChatResponse();

        saveChatDetailsWithContext(userQuery, chatAnswer, contextSections);

        return chatResponse;
    }

    private String processResponse(final List<ChatRequestMessage> completedChatHistory) {
        StringBuilder response = new StringBuilder();
        IterableStream<ChatCompletions> completionsIterableStream = azureAiClient.generateFullAnswer(completedChatHistory);
        for (ChatCompletions chatCompletions : completionsIterableStream) {
            if (!CollectionUtils.isEmpty(chatCompletions.getChoices())) {
                String completionResult = Optional.ofNullable(chatCompletions.getChoices().get(0).getDelta().getContent())
                        .orElse("");
                response.append(completionResult);
            }
        }
        return response.toString();
    }

    private List<ChatRequestMessage> constructQueryPromptWithHistory(final List<String> contextSections, final String userQuery, final List<ChatHistory> chatHistory) {
        List<ChatRequestMessage> messagesArray = new ArrayList<>();
        String systemMessageContent = getSystemPromptWithContext(contextSections);
        messagesArray.add(new ChatRequestSystemMessage(systemMessageContent));

        if (!CollectionUtils.isEmpty(chatHistory)) {
            int tokensAvailableForHistory = getTokensAvailableForHistory(contextSections, userQuery);
            List<ChatRequestMessage> maxHistoryArray = buildMaxHistoryArray(chatHistory, tokensAvailableForHistory);
            messagesArray.addAll(maxHistoryArray);
        }

        messagesArray.add(new ChatRequestUserMessage(userQuery));
        return messagesArray;
    }

    public List<ChatRequestMessage> buildMaxHistoryArray(final List<ChatHistory> chatHistory, final int maxTokens) {
        List<ChatRequestMessage> maxHistoryArray = new ArrayList<>();
        int currentTokenCount = 0;
        List<ChatHistory> reversedChatHistory = Optional.ofNullable(chatHistory).orElse(Collections.emptyList());
        Collections.reverse(reversedChatHistory);

        for (ChatHistory element : reversedChatHistory) {
            ChatRequestMessage chatObject;
            if (element.getIsUser()) {
                chatObject = new ChatRequestUserMessage(element.getContent());
            } else {
                chatObject = new ChatRequestAssistantMessage(element.getContent());
            }

            currentTokenCount = currentTokenCount + getNumberOfTokens(element.getContent());
            if (currentTokenCount >= maxTokens) {
                break;
            } else {
                maxHistoryArray.add(chatObject);
            }
        }
        return maxHistoryArray;
    }

    private int getTokensAvailableForHistory(List<String> contextSections, String userQuery) {
        int tokensInContext = 0;
        for (String section : contextSections) {
            tokensInContext += getNumberOfTokens(section);
        }

        int tokensInUserInput = getNumberOfTokens(userQuery);
        int tokensReservedForQueryResponse = maxQueryModelTokens - maxInputTokens - maxQueryContextTokens - maxNumOfMasterChunksInContext;
        int tokensAvailableForHistory = maxQueryModelTokens - tokensReservedForQueryResponse - tokensInContext - maxNumOfMasterChunksInContext - tokensInUserInput;
        log.info("tokensInUserInput: {}", tokensInUserInput);
        log.info("tokensInContext: {}", tokensInContext);
        log.info("tokensAvailableForHistory: {}", tokensAvailableForHistory);
        return tokensAvailableForHistory;
    }

    private String buildMaxHistoryString(final List<ChatHistory> chatHistory) {
        StringBuilder maxHistoryString = new StringBuilder();
        int currentTokensCount = 0;

        List<ChatHistory> history = Optional.ofNullable(chatHistory).orElse(Collections.emptyList());
        Collections.reverse(history);

        for (ChatHistory element : history) {
            String operator = element.getIsUser() ? "user" : "assistant";
            String chatString = operator +
                    ": " +
                    element.getContent() +
                    "\n\n";

            currentTokensCount += getNumberOfTokens(chatString);

            if (currentTokensCount > maxHistoryTokens) break;
            else maxHistoryString.append(chatString);
        }

        return maxHistoryString.toString();
    }

    private List<String> buildMaxQueryContext(final List<String> topMatchingPageChunks) {
        List<String> contextSections = new ArrayList<>();
        int currentTokenCount = 0;

        for (int index = 0; index < topMatchingPageChunks.size() && contextSections.size() < maxNumOfMasterChunksInContext; index++) {
            String chunk = topMatchingPageChunks.get(index);
            int chunkTokenCount = getNumberOfTokens(chunk);
            if (chunkTokenCount + chunkTokenCount <= maxQueryContextTokens) {
                contextSections.add(chunk);
                currentTokenCount += chunkTokenCount;
            }
        }
        log.info("Token count of context: {}", currentTokenCount);
        return contextSections;
    }

    private Set<String> getTopMatchingDbResults(String summarizedUserQuery, String indexName) {
        List<SearchDocument> documents = documentsService.getDocuments(indexName, summarizedUserQuery, maxNumOfMasterChunksInContext, isHybridSearchEnabled);
        Set<String> topResults = new HashSet<>();
        documents.forEach(doc -> {
            String finalSegment = doc.get("masterChunk").toString() + " [Source]{" + doc.get("pageLink") + ")";
            topResults.add(finalSegment);
        });
        return topResults;
    }

    private String getSummarizedUserQuery(final String userQuery, final List<ChatHistory> chatHistory) {
        if (CollectionUtils.isEmpty(chatHistory)) {
            log.info("No chat history received. Skipping summarization and rephrase");
            return userQuery;
        }

        log.info("Chat history received. Summarizing query.");
        String maxAvailableHistory = buildMaxHistoryString(chatHistory);
        List<ChatRequestMessage> messages = constructSummaryPromptWithHistory(maxAvailableHistory, userQuery);
        String completions = azureAiClient.rephraseUserPrompt(messages);
        RephrasedUserInputResponse rephrasedUserInput = getParsedOpenAiResponse(completions);

        if (rephrasedUserInput.getIsFollowUpRelevant()) {
            return rephrasedUserInput.getRephrasedUserInput();
        } else {
            log.info("Follow up is not deemed relevant. Summary won't be used for context");
            return userQuery;
        }
    }

    private void validateAmountOfTokens(final String userQuery) {
        final Integer numberOfTokens = getNumberOfTokens(userQuery);
        if (numberOfTokens > maxInputTokens) {
            log.warn("User input has too many tokens: {}", numberOfTokens);
            throw new ChatException(tooManyCharactersErrorMessage);
        }
    }

    private void saveChatDetailsWithContext(String userQuery, String chatAnswer, List<String> contextSections) {
        log.info("Saving chat details for user query: {}", userQuery);
        try {
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String[] words = userQuery.split("\\s+");
            StringBuilder firstWords = new StringBuilder();
            for (int i = 0; i < Math.min(6, words.length); i++) {
                firstWords.append(words[i]);
                if (i < Math.min(6, words.length) - 1) {
                    firstWords.append(" ");
                }
            }
            String fileName = dateTime + " " + firstWords + ".json";
            String dirPath = "src/test/resources/dataset";
            Files.createDirectories(Paths.get(dirPath));
            String filePath = dirPath + "/" + fileName.replaceAll("[\\\\/:*?\"<>|]", "_");

            Map<String, Object> data = new HashMap<>();
            data.put("question", userQuery);
            data.put("answer", chatAnswer);
            data.put("contexts", contextSections);

            ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
            writer.writeValue(new File(filePath), data);

            log.info("Chat details saved to file: {}\n", filePath);
        } catch (Exception e) {
            log.error("Failed to save chat details: {}", e.getMessage(), e);
        }
    }

}
