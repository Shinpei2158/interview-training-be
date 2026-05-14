package com.example.be.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.be.category.dto.CategoryList;
import com.example.be.entity.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryList> getAll() {

        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> CategoryList.builder()
                        .id(category.getId().toString())
                        .name(category.getName())
                        .parentName(
                                category.getParent() != null
                                        ? category.getParent().getName()
                                        : null
                        )
                        .childrenNames(
                                category.getChildren()
                                        .stream()
                                        .map(Category::getName)
                                        .toList()
                        )
                        .build())
                .toList();
    }
}
