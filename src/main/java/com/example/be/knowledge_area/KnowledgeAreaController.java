package com.example.be.knowledge_area;

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

import com.example.be.knowledge_area.dto.KnowledgeAreaCreateRequest;
import com.example.be.knowledge_area.dto.KnowledgeAreaResponse;
import com.example.be.knowledge_area.dto.KnowledgeAreaUpdateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/knowledge-areas")
@RequiredArgsConstructor
public class KnowledgeAreaController {

    private final KnowledgeAreaService knowledgeAreaService;

    @GetMapping
    public Page<KnowledgeAreaResponse> list(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return knowledgeAreaService.findAll(
                categoryId,
                active,
                search,
                pageable
        );
    }

    @GetMapping("/{id}")
    public KnowledgeAreaResponse getById(@PathVariable UUID id) {
        return knowledgeAreaService.findById(id);
    }

    @PostMapping
    public ResponseEntity<KnowledgeAreaResponse> create(
            @Valid @RequestBody KnowledgeAreaCreateRequest request
    ) {
        KnowledgeAreaResponse created = knowledgeAreaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public KnowledgeAreaResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody KnowledgeAreaUpdateRequest request
    ) {
        return knowledgeAreaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        knowledgeAreaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
