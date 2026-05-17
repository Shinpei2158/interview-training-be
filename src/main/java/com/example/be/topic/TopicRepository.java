package com.example.be.topic;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.be.entity.Topic;
import com.example.be.topic.dto.TopicResponse;

public interface TopicRepository extends JpaRepository<Topic, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    @Query("""
            SELECT t
            FROM Topic t
            JOIN FETCH t.knowledgeArea
            WHERE t.id = :id
            """)
    Optional<Topic> findByIdWithKnowledgeArea(@Param("id") UUID id);

    @Query(
            value = """
                    SELECT new com.example.be.topic.dto.TopicResponse(
                        t.id,
                        t.name,
                        t.description,
                        t.isActive,
                        t.createdAt,
                        t.updatedAt,
                        ka.id,
                        ka.name
                    )
                    FROM Topic t
                    JOIN t.knowledgeArea ka
                    WHERE (:knowledgeAreaId IS NULL OR ka.id = :knowledgeAreaId)
                      AND (:active IS NULL OR t.isActive = :active)
                      AND (
                          :search IS NULL
                          OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))
                      )
                    """,
            countQuery = """
                    SELECT COUNT(t)
                    FROM Topic t
                    JOIN t.knowledgeArea ka
                    WHERE (:knowledgeAreaId IS NULL OR ka.id = :knowledgeAreaId)
                      AND (:active IS NULL OR t.isActive = :active)
                      AND (
                          :search IS NULL
                          OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))
                      )
                    """
    )
    Page<TopicResponse> findAllProjected(
            @Param("knowledgeAreaId") UUID knowledgeAreaId,
            @Param("active") Boolean active,
            @Param("search") String search,
            Pageable pageable
    );
}
