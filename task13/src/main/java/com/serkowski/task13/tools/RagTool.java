package com.serkowski.task13.tools;

import com.serkowski.task13.service.VectorStoreService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class RagTool {

    private final ChatClient ragClient;
    private final VectorStoreService vectorStoreService;

    public RagTool(ChatClient ragClient, VectorStoreService vectorStoreService) {
        this.ragClient = ragClient;
        this.vectorStoreService = vectorStoreService;
    }

    @Tool(description = """ 
            Performs a semantic search within a specified document to find answers to questions. 
            Use this tool when you need to answer a question based on the content of a file. 
            Provide the user's question or search query and the URL of the file.
            """)
    public String ask(@ToolParam(description = "The search query or question to search for in the document") String question, @ToolParam(description = "The path of the document to search within") String filePath) {
        vectorStoreService.storeAsVector(filePath);
        return ragClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
