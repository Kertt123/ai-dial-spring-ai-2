package com.serkowski.task13.repository;

import com.serkowski.task13.model.memory.UserMemory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryRepository extends JpaRepository<UserMemory, Long> {

    UserMemory findByUserId(String userId);
}
