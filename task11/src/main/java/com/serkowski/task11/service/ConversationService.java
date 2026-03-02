package com.serkowski.task11.service;

import com.serkowski.task11.model.Conversation;
import com.serkowski.task11.model.ConversationSummary;
import org.springframework.ai.chat.memory.repository.redis.RedisChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    private final static String REDIS_KEY_PREFIX = "conversation-summary:";
    private final RedisChatMemoryRepository redisChatMemoryRepository;
    private final RedisTemplate<String, ConversationSummary> redisTemplate;

    public ConversationService(RedisChatMemoryRepository redisChatMemoryRepository, RedisTemplate<String, ConversationSummary> redisTemplate) {
        this.redisChatMemoryRepository = redisChatMemoryRepository;
        this.redisTemplate = redisTemplate;
    }

    public ConversationSummary saveConversationSummary(String title) {
        ConversationSummary conversationSummary = new ConversationSummary(UUID.randomUUID().toString(), title, String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), 0);
        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + conversationSummary.id(), conversationSummary);
        return conversationSummary;
    }

    public List<ConversationSummary> getConversations() {
        return redisTemplate.keys(REDIS_KEY_PREFIX + "*")
                .stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .toList();
    }

    public Conversation getConversationById(String conversationId) {
        List<Message> messages = redisChatMemoryRepository.findByConversationId(conversationId)
                .stream()
                .toList();
        ConversationSummary summary = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + conversationId);
        String title = (summary != null) ? summary.title() : "Brak tytułu";

        return new Conversation(conversationId, title, messages.stream()
                .map(message -> new com.serkowski.task11.model.MessageResponse(message.getMessageType().getValue(), message.getText()))
                .toList());
    }

    public void deleteConversationById(String conversationId) {
        redisChatMemoryRepository.deleteByConversationId(conversationId);
        redisTemplate.delete(REDIS_KEY_PREFIX + conversationId);
    }
}
