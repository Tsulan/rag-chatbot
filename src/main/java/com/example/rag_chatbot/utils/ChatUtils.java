package com.example.rag_chatbot.utils;

import com.azure.ai.openai.models.*;
import com.example.rag_chatbot.models.RephrasedUserInputResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class ChatUtils {
    private static final Pattern tokenezationPattern = Pattern.compile("[ \\t\\n\\r\\f,.:;?!\\[\\]']");

    public static Integer getNumberOfTokens(final String userQuery) {
        String[] splitUserQuery = userQuery.split(tokenezationPattern.pattern());
        return splitUserQuery.length * 2;
    }

    public static List<ChatRequestMessage> constructSummaryPromptWithHistory(final String maxHistoryString, final String userQuery) {
        List<ChatRequestMessage> messagesArray = new ArrayList<>();
        messagesArray.add(new ChatRequestSystemMessage(
                """
                        You are helpful assistant that rephrases user input so it can be understood without knowing the chat history.
                        1) First summarize the chat history into a single sentence.
                        2) Then determine if the follow-up user input is related to the chat history. Respond with true or false.
                        3) Finally, determine if the latest user prompt require to be rephrased and if so, rephrase the user's follow-uo paragraph into a standalone paragraph that includes any knowledge from the chat history that the assistant will need to know in order to fully understand the user's question. Don't reference the previous conversation. Simply rephrase the question. Don't change the meaning. Maintain the user's point of view and use the same language as user's follow-up input.
                        Format your response as stringified valid JSON like this:
                        {"summary": "<String>", "isFollowUpRelevant": <boolean>, "rephrasedUserInput": "<String>"}
                        """
        ));

        messagesArray.add(new ChatRequestUserMessage(
                "Here is a chat history between a user and assistant.\n\n" +
                        "Chat history:\n" +
                        "-----\n" +
                        maxHistoryString +
                        "\n------\n\n" +
                        "That was the chat history. For now just remember this and reply with \"READ\". Don't do anything else."
        ));

        messagesArray.add(new ChatRequestAssistantMessage("READ"));

        messagesArray.add(new ChatRequestUserMessage(
                "Here is the user's follow-up input:\n" +
                        "----\n" +
                        userQuery + "\n" +
                        "----\n\n"
        ));

        return messagesArray;
    }

    public static String getSystemPromptWithContext(final List<String> contextSections) {
        StringBuilder systemMessageContent = new StringBuilder("You are an enthusiastic Moldova State University assistant that loves to help people! Your task is to answer the user's questions using only the information below.\nIf you cannot answer the question based on the information, say that you are sorry to be unable to answer his question using the information. Explain the reason based on the provided information. Reference specific sections and include relevant links to any closely related information to help guide the user to potentially useful resources.\nOnly base your answer on information found in the information below and don't add any additional information.\nExplain your answer step-by-step and justify your reasoning. Also, you must provide links to relevant sources in markdown format.\nKeep the conversation language same as the user's language." +
                "\n\ninformation snippet:" +
                "\n-------------------\n" +
                contextSections.stream().findFirst().orElse("") +
                "\n-------------------\n\n");

        for (int index = 1; index < contextSections.size(); index++) {
            systemMessageContent.append("Next information snippet:")
                    .append("\n-------------------\n")
                    .append(contextSections.get(index))
                    .append("\n-------------------\n\n");
        }
        systemMessageContent.append("That was all the information context.");
        return systemMessageContent.toString();
    }

    public static void logMessagesArray(List<ChatRequestMessage> messagesArray) {
        for (ChatRequestMessage message : messagesArray) {
            if (message instanceof ChatRequestSystemMessage chatRequestSystemMessage) {
                log.info("messagesArray[role: {}]", ChatRole.SYSTEM);
                log.info("messagesArray[content: {}]", chatRequestSystemMessage.getContent());
            } else if (message instanceof ChatRequestUserMessage chatRequestUserMessage) {
                log.info("messagesArray[role: {}]", ChatRole.USER);
                log.info("messagesArray[content: {}]", chatRequestUserMessage.getContent());
            } else if (message instanceof ChatRequestAssistantMessage chatRequestAssistantMessage) {
                log.info("messagesArray[role: {}]", ChatRole.ASSISTANT);
                log.info("messagesArray[content: {}]", chatRequestAssistantMessage.getContent());
            }
        }
    }

    public static RephrasedUserInputResponse getParsedOpenAiResponse(final String completionContent) {
        try {
            return new ObjectMapper().readValue(completionContent, RephrasedUserInputResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Could not parse the json content. Cause: {}", e.getMessage());
            return new RephrasedUserInputResponse();
        }
    }
}
