package com.example.be.topic;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.entity.KnowledgeArea;
import com.example.be.entity.Topic;
import com.example.be.exception.GlobalException;
import com.example.be.knowledge_area.KnowledgeAreaRepository;
import com.example.be.topic.dto.TopicCreateRequest;
import com.example.be.topic.dto.TopicResponse;
import com.example.be.topic.dto.TopicUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {

    private static final int MAX_PAGE_SIZE = 100;

    private final TopicRepository topicRepository;
    private final KnowledgeAreaRepository knowledgeAreaRepository;

    @Transactional(readOnly = true)
    public Page<TopicResponse> findAll(
            UUID knowledgeAreaId,
            Boolean active,
            String search,
            Pageable pageable
    ) {
        Pageable safePageable = capPageSize(pageable);
        String normalizedSearch = normalizeSearch(search);

        return topicRepository.findAllProjected(
                knowledgeAreaId,
                active,
                normalizedSearch,
                safePageable
        );
    }

    @Transactional(readOnly = true)
    public TopicResponse findById(UUID id) {
        Topic topic = getTopicWithKnowledgeArea(id);
        return TopicResponse.from(topic);
    }

    @Transactional
    public TopicResponse create(TopicCreateRequest request) {
        validateUniqueName(request.getName(), null);

        KnowledgeArea knowledgeArea = getKnowledgeArea(request.getKnowledgeAreaId());

        Topic topic = Topic.builder()
                .name(request.getName().trim())
                .description(trimToNull(request.getDescription()))
                .knowledgeArea(knowledgeArea)
                .isActive(request.getActive() == null || request.getActive())
                .build();

        Topic saved = topicRepository.save(topic);
        return TopicResponse.from(saved);
    }

    @Transactional
    public TopicResponse update(UUID id, TopicUpdateRequest request) {
        Topic topic = getTopicWithKnowledgeArea(id);

        validateUniqueName(request.getName(), id);

        KnowledgeArea knowledgeArea = getKnowledgeArea(request.getKnowledgeAreaId());

        topic.setName(request.getName().trim());
        topic.setDescription(trimToNull(request.getDescription()));
        topic.setKnowledgeArea(knowledgeArea);
        topic.setActive(request.getActive());

        return TopicResponse.from(topic);
    }

    @Transactional
    public void delete(UUID id) {
        Topic topic = getTopicWithKnowledgeArea(id);
        topic.setActive(false);
    }

    private Topic getTopicWithKnowledgeArea(UUID id) {
        return topicRepository.findByIdWithKnowledgeArea(id)
                .orElseThrow(() ->
                        new GlobalException("NOT_FOUND", "Topic not found"));
    }

    private KnowledgeArea getKnowledgeArea(UUID knowledgeAreaId) {
        return knowledgeAreaRepository.findById(knowledgeAreaId)
                .orElseThrow(() ->
                        new GlobalException("NOT_FOUND", "Knowledge area not found"));
    }

    private void validateUniqueName(String name, UUID excludeId) {
        String trimmedName = name.trim();
        boolean exists = excludeId == null
                ? topicRepository.existsByNameIgnoreCase(trimmedName)
                : topicRepository.existsByNameIgnoreCaseAndIdNot(
                        trimmedName,
                        excludeId
                );

        if (exists) {
            throw new GlobalException("BAD_REQUEST", "Topic name already exists");
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
