package com.serkowski.task13.advisor;

import com.serkowski.task13.model.memory.MemoryUpdateCheck;
import com.serkowski.task13.model.memory.MemoryUpdateResult;
import com.serkowski.task13.service.MemoryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;
import java.util.Optional;

public class LongMemoryAdvisor implements BaseAdvisor {

    private static final String MEMORY_CHECK_SYSTEM_PROMPT = """
            You are a memory filter. Your ONLY job is to quickly determine if any new information is available about the user.
            
            ## What we MUST to store:
            - Personal details (name, location, job, etc.)
            - Preferences and likes/dislikes
            - Goals, projects, or plans
            - Skills, hobbies, or interests
            - Important relationships or life events
            - Decisions or past experiences
            
            ## Instructions:
            1. Find in USER_MESSAGE and ASSISTANT_MESSAGE on any information about user that can be stored
            2. Compare EXISTING_INFORMATION and information that you have found at point if anything NEW was revealed or anything that should be updated
            3. Provide output according to the RESPONSE_FORMAT
            
            ## EXISTING_INFORMATION:
            {current_memories}
            
            ## USER_MESSAGE:
            {user_message}
            
            ## ASSISTANT_MESSAGE: 
            {assistant_message}
            """;
    private final static String MEMORY_UPDATE_SYSTEM_PROMPT = """
            You are a memory manager. Generate updated memory content by merging existing memories with new information.
            
            ## Your Task:
            Combine EXISTING_INFORMATION with the NEW information from the USER_MESSAGE+ASSISTANT_MESSAGE. Output the complete updated memory in concise bullet format.
            
            ## Rules:
            1. BE EXTREMELY CONCISE - telegraphic style, short phrases
            2. Use bullet points (- prefix) for each fact
            3. Each bullet = 1-2 sentences MAX
            4. Remove outdated/contradicting information
            5. Group related facts with markdown headers
            6. FACTS ONLY - no explanations or elaboration
            7. Get information only about the user not the assistant
            
            ## EXISTING_INFORMATION:
            {current_memories}
            
            ## USER_MESSAGE:
            {user_message}
            
            ## ASSISTANT_MESSAGE: 
            {assistant_message}""";

    private final ChatClient longMemoryChatClient;
    private final ChatMemory chatMemory;
    private final MemoryService memoryService;

    public LongMemoryAdvisor(ChatClient longMemoryChatClient, ChatMemory chatMemory, MemoryService memoryService) {
        this.longMemoryChatClient = longMemoryChatClient;
        this.chatMemory = chatMemory;
        this.memoryService = memoryService;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        List<Message> messages = chatMemory.get(chatClientResponse.context().get(ChatMemory.CONVERSATION_ID).toString());

        UserMessage userMessage = (UserMessage) messages.stream()
                .filter(message -> message instanceof UserMessage)
                .reduce((first, second) -> second)
                .orElse(null);

        AssistantMessage assistantMessage = (AssistantMessage) messages.stream()
                .filter(message -> message instanceof AssistantMessage)
                .reduce((first, second) -> second)
                .orElse(null);
        String userMemo = memoryService.getUserMemory("user1");

        MemoryUpdateCheck memoryUpdateCheck = longMemoryChatClient.prompt()
                .user(userSpec -> userSpec.text(MEMORY_CHECK_SYSTEM_PROMPT)
                        .param("current_memories", userMemo)
                        .param("user_message", userMessage != null ? userMessage.getText() : "No user message")
                        .param("assistant_message", assistantMessage != null ? assistantMessage.getText() : "No assistant message")
                )
                .call()
                .entity(MemoryUpdateCheck.class);
        if (memoryUpdateCheck.shouldUpdate()) {
            MemoryUpdateResult entity = longMemoryChatClient.prompt()
                    .user(userSpec -> userSpec.text(MEMORY_UPDATE_SYSTEM_PROMPT)
                            .param("current_memories", userMemo)
                            .param("user_message", userMessage != null ? userMessage.getText() : "No user message")
                            .param("assistant_message", assistantMessage.getText()))
                    .call()
                    .entity(MemoryUpdateResult.class);
            memoryService.saveUserMemory("user1", entity.updatedMemory());
        }

        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return DEFAULT_CHAT_MEMORY_PRECEDENCE_ORDER - 1;
    }
}
