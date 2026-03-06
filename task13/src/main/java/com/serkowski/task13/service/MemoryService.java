package com.serkowski.task13.service;

import com.serkowski.task13.model.memory.UserMemory;
import com.serkowski.task13.repository.MemoryRepository;

import java.util.Optional;

public class MemoryService {

    private final MemoryRepository memoryRepository;

    public MemoryService(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    public String getUserMemory(String userId) {
        return Optional.ofNullable(memoryRepository.findByUserId(userId))
                .map(UserMemory::getMemory)
                .orElse("No memory found for user.");
    }

    public void saveUserMemory(String userId, String memory) {
        UserMemory userMemory = memoryRepository.findByUserId(userId);
        if (userMemory == null) {
            userMemory = new UserMemory();
            userMemory.setUserId(userId);
        }
        userMemory.setMemory(memory);
        memoryRepository.save(userMemory);
    }
}
