package org.example.Services;

import org.example.DTO.Request.CategoryRequest;
import org.example.DTO.Response.CategoryResponse;
import org.example.Entities.Categories;
import org.example.Exception.ResourceAlreadyExistsException;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.CategoryMapper;
import org.example.Repository.CategoryRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }


    public Page<CategoryResponse> getCategories(Pageable pageable) {

        logger.info("Displaying all categories");
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
    }

    public List<CategoryResponse> getAllCategories() {

        logger.info("Displaying all categories without pagination");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        logger.info("Fetching category with id: {}", id);
        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Category with id: {} does not exist", id);
                    return new ResourceNotFoundException("Category not found.");
                });
        logger.info("Successfully fetched category:{} (id: {})", category.getCategoryName(), category.getCategoryId() );
        return categoryMapper.toResponse(category);
    }

    public CategoryResponse addCategory(CategoryRequest request) {

        logger.info("Attempting to add new category with name: {}", request.getCategoryName());

        Categories category = categoryRepository.findByCategoryName(request.getCategoryName());

        if (category != null) {
            logger.warn("Category already exists with name: {}", request.getCategoryName());
            throw new ResourceAlreadyExistsException("Category already exists.");
        }

        String safeName = Jsoup.clean(request.getCategoryName(), Safelist.none()).trim();
        Categories newCategory = new Categories();
        newCategory.setCategoryName(safeName);


        categoryRepository.save(newCategory);

        logger.info("Successfully added new category: {}", safeName);

        return categoryMapper.toResponse(newCategory);
    }


    public CategoryResponse updateCategoryName(Long id, CategoryRequest request) {

        logger.info("Updating category id: {} with new name: {}", id, request.getCategoryName());

        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category not found");
                });

        String safeName = Jsoup.clean(request.getCategoryName(), Safelist.none()).trim();
        category.setCategoryName(request.getCategoryName());

        categoryRepository.save(category);
        logger.info("Successfully updated category id: {} to name: {}", id, request.getCategoryName());

        return categoryMapper.toResponse(category);
    }

    public void deleteCategory(Long id) {

        logger.info("Attempting to delete category with id: {}", id);
        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryRepository.delete(category);

        logger.info("Successfully deleted category: {} (id: {})", category.getCategoryName(), id);
    }


}
