package com.example.be.category;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.be.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
