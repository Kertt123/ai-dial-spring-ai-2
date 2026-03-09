package com.serkowski.task14.generalPurpose.clients;

import com.serkowski.generalPurpose.model.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class DialClient {

    ChatClient chatClient;

    public DialClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Tool(description = "Sends a message to the chat model and retrieves the response. The conversationId is used to maintain context across multiple messages in the same conversation.")
    public ChatResponse chat(@ToolParam(description = "Description of a task provided from orchestrator.") String prompt,
                             @ToolParam(description = "Conversation id") String conversationId) {
       return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .entity(ChatResponse.class);
    }

}