package com.example.be.topic;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.topic.dto.TopicCreateRequest;
import com.example.be.topic.dto.TopicResponse;
import com.example.be.topic.dto.TopicUpdateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public Page<TopicResponse> list(
            @RequestParam(required = false) UUID knowledgeAreaId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return topicService.findAll(
                knowledgeAreaId,
                active,
                search,
                pageable
        );
    }

    @GetMapping("/{id}")
    public TopicResponse getById(@PathVariable UUID id) {
        return topicService.findById(id);
    }

    @PostMapping
    public ResponseEntity<TopicResponse> create(
            @Valid @RequestBody TopicCreateRequest request
    ) {
        TopicResponse created = topicService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public TopicResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody TopicUpdateRequest request
    ) {
        return topicService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        topicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
