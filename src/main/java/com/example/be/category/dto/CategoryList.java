package com.example.be.category.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryList {

    private String id;

    private String name;

    private String parentName;

    private List<String> childrenNames;
}