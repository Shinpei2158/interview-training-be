package com.example.be.knowledge_area;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.be.entity.KnowledgeArea;
import com.example.be.knowledge_area.dto.KnowledgeAreaResponse;

public interface KnowledgeAreaRepository extends JpaRepository<KnowledgeArea, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    @Query("""
            SELECT ka
            FROM KnowledgeArea ka
            JOIN FETCH ka.category
            WHERE ka.id = :id
            """)
    Optional<KnowledgeArea> findByIdWithCategory(@Param("id") UUID id);

    @Query(
            value = """
                    SELECT new com.example.be.knowledge_area.dto.KnowledgeAreaResponse(
                        ka.id,
                        ka.name,
                        ka.description,
                        ka.isActive,
                        ka.createdAt,
                        ka.updatedAt,
                        c.id,
                        c.name
                    )
                    FROM KnowledgeArea ka
                    JOIN ka.category c
                    WHERE (:categoryId IS NULL OR c.id = :categoryId)
                      AND (:active IS NULL OR ka.isActive = :active)
                      AND (
                          :search IS NULL
                          OR LOWER(ka.name) LIKE LOWER(CONCAT('%', :search, '%'))
                      )
                    """,
            countQuery = """
                    SELECT COUNT(ka)
                    FROM KnowledgeArea ka
                    JOIN ka.category c
                    WHERE (:categoryId IS NULL OR c.id = :categoryId)
                      AND (:active IS NULL OR ka.isActive = :active)
                      AND (
                          :search IS NULL
                          OR LOWER(ka.name) LIKE LOWER(CONCAT('%', :search, '%'))
                      )
                    """
    )
    Page<KnowledgeAreaResponse> findAllProjected(
            @Param("categoryId") UUID categoryId,
            @Param("active") Boolean active,
            @Param("search") String search,
            Pageable pageable
    );
}
