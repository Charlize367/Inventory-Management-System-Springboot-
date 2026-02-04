package org.example.Mapper;

import org.example.DTO.Request.CategoryRequest;
import org.example.DTO.Response.CategoryResponse;
import org.example.DTO.Response.VariationOptionsResponse;
import org.example.DTO.Response.VariationResponse;
import org.example.Entities.Categories;
import org.example.Entities.Variation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    public Categories toEntity(CategoryRequest req) {
        Categories category = new Categories();
        category.setCategoryName(req.getCategoryName());
        return category;
    }

    public CategoryResponse toResponse(Categories category) {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategoryId(category.getCategoryId());
        categoryResponse.setCategoryName(category.getCategoryName());
        return categoryResponse;
    }

    public Set<CategoryResponse> toSetResponse(Set<Categories> categories) {
        return categories.stream()
                .map(category -> {
                    CategoryResponse categoryResponse = new CategoryResponse();
                    categoryResponse.setCategoryId(category.getCategoryId());
                    categoryResponse.setCategoryName(category.getCategoryName());
                    return categoryResponse;
                })
                .collect(Collectors.toSet());
    }
}
