package com.example.be.knowledge_area;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.category.CategoryRepository;
import com.example.be.entity.Category;
import com.example.be.entity.KnowledgeArea;
import com.example.be.exception.GlobalException;
import com.example.be.knowledge_area.dto.KnowledgeAreaCreateRequest;
import com.example.be.knowledge_area.dto.KnowledgeAreaResponse;
import com.example.be.knowledge_area.dto.KnowledgeAreaUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KnowledgeAreaService {

    private static final int MAX_PAGE_SIZE = 100;

    private final KnowledgeAreaRepository knowledgeAreaRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<KnowledgeAreaResponse> findAll(
            UUID categoryId,
            Boolean active,
            String search,
            Pageable pageable
    ) {
        Pageable safePageable = capPageSize(pageable);
        String normalizedSearch = normalizeSearch(search);

        return knowledgeAreaRepository.findAllProjected(
                categoryId,
                active,
                normalizedSearch,
                safePageable
        );
    }

    @Transactional(readOnly = true)
    public KnowledgeAreaResponse findById(UUID id) {
        KnowledgeArea knowledgeArea = getKnowledgeAreaWithCategory(id);
        return KnowledgeAreaResponse.from(knowledgeArea);
    }

    @Transactional
    public KnowledgeAreaResponse create(KnowledgeAreaCreateRequest request) {
        validateUniqueName(request.getName(), null);

        Category category = getCategory(request.getCategoryId());

        KnowledgeArea knowledgeArea = KnowledgeArea.builder()
                .name(request.getName().trim())
                .description(trimToNull(request.getDescription()))
                .category(category)
                .isActive(request.getActive() == null || request.getActive())
                .build();

        KnowledgeArea saved = knowledgeAreaRepository.save(knowledgeArea);
        return KnowledgeAreaResponse.from(saved);
    }

    @Transactional
    public KnowledgeAreaResponse update(UUID id, KnowledgeAreaUpdateRequest request) {
        KnowledgeArea knowledgeArea = getKnowledgeAreaWithCategory(id);

        validateUniqueName(request.getName(), id);

        Category category = getCategory(request.getCategoryId());

        knowledgeArea.setName(request.getName().trim());
        knowledgeArea.setDescription(trimToNull(request.getDescription()));
        knowledgeArea.setCategory(category);
        knowledgeArea.setActive(request.getActive());

        return KnowledgeAreaResponse.from(knowledgeArea);
    }

    @Transactional
    public void delete(UUID id) {
        KnowledgeArea knowledgeArea = getKnowledgeAreaWithCategory(id);
        knowledgeArea.setActive(false);
    }

    private KnowledgeArea getKnowledgeAreaWithCategory(UUID id) {
        return knowledgeAreaRepository.findByIdWithCategory(id)
                .orElseThrow(() ->
                        new GlobalException("NOT_FOUND", "Knowledge area not found"));
    }

    private Category getCategory(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new GlobalException("NOT_FOUND", "Category not found"));
    }

    private void validateUniqueName(String name, UUID excludeId) {
        String trimmedName = name.trim();
        boolean exists = excludeId == null
                ? knowledgeAreaRepository.existsByNameIgnoreCase(trimmedName)
                : knowledgeAreaRepository.existsByNameIgnoreCaseAndIdNot(
                        trimmedName,
                        excludeId
                );

        if (exists) {
            throw new GlobalException("BAD_REQUEST", "Knowledge area name already exists");
        }
    }

    private Pageable capPageSize(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(
                pageable.getPageNumber(),
                size,
                pageable.getSort()
        );
    }

    private String normalizeSearch(String search) {
        if (search == null) {
            return null;
        }

        String trimmed = search.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
