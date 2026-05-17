package com.example.be.knowledge_area.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.be.entity.KnowledgeArea;

public record KnowledgeAreaResponse(
        UUID id,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID categoryId,
        String categoryName
) {

    public static KnowledgeAreaResponse from(KnowledgeArea knowledgeArea) {
        return new KnowledgeAreaResponse(
                knowledgeArea.getId(),
                knowledgeArea.getName(),
                knowledgeArea.getDescription(),
                knowledgeArea.isActive(),
                knowledgeArea.getCreatedAt(),
                knowledgeArea.getUpdatedAt(),
                knowledgeArea.getCategory().getId(),
                knowledgeArea.getCategory().getName()
        );
    }
}
