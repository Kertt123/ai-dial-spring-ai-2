package com.serkowski.task6_4.service;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;

public class VectorSyncAdvisor implements BaseAdvisor {

    private final VectorStoreService vectorStoreService;
    public VectorSyncAdvisor( VectorStoreService vectorStoreService1) {
        this.vectorStoreService = vectorStoreService1;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        vectorStoreService.syncVectorStore();
        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
