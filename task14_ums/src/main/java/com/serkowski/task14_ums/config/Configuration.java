package com.serkowski.task14_ums.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serkowski.task14_ums.model.ChatResponse;
import com.serkowski.task14_ums.service.UserChatClient;
import com.serkowski.task14_ums.service.UserService;
import com.serkowski.task14_ums.service.UserTools;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
                registry.addMapping("/sse/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*");
                registry.addMapping("/mcp/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    public UserChatClient agentClient(ChatModel chatModel, UserService userService) {
        return new UserChatClient(ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                                        .maxMessages(30)
                                        .build())
                                .build()
                )
                .defaultOptions(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4o")
                        .build())
                .defaultToolCallbacks(ToolCallbacks.from(new UserTools(userService)))
                .build());
    }

    @Bean
    public List<McpServerFeatures.SyncToolSpecification> myTools(@Lazy UserChatClient agentClient) {
        var inputSchema = new McpSchema.JsonSchema(
                "object",
                Map.of(
                        "prompt", Map.of("type", "string", "description", "Task prompt."),
                        "conversationId", Map.of("type", "string", "description", "Conversation history id.")
                ),
                List.of("prompt", "conversationId"),
                null,
                null,
                null
        );

        var tool = McpSchema.Tool.builder()
                .name("ums-agent")
                .description("User service manager agent. Use this tool to manage user accounts, including creating, updating, and deleting user information. Provide the necessary details for the desired operation.")
                .inputSchema(inputSchema)
                .build();

        var objectMapper = new ObjectMapper();
        var registration = new McpServerFeatures.SyncToolSpecification(tool, (exchange, args) -> {
            String prompt = (String) args.get("prompt");
            String conversationId = (String) args.get("conversationId");
            ChatResponse result = agentClient.chat(prompt, conversationId);
            try {
                String json = objectMapper.writeValueAsString(result);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(json)), false);
            } catch (JsonProcessingException e) {
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error serializing response: " + e.getMessage())), true);
            }
        });

        return List.of(registration);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public UserService userService(RestTemplate restTemplate) {
        return new UserService(restTemplate);
    }
}
