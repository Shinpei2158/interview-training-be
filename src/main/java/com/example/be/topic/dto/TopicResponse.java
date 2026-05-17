package com.example.be.topic.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.be.entity.Topic;

public record TopicResponse(
        UUID id,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID knowledgeAreaId,
        String knowledgeAreaName
) {

    public static TopicResponse from(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getName(),
                topic.getDescription(),
                topic.isActive(),
                topic.getCreatedAt(),
                topic.getUpdatedAt(),
                topic.getKnowledgeArea().getId(),
                topic.getKnowledgeArea().getName()
        );
    }
}
