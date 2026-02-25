package org.example.Services;

import org.example.DTO.Request.CategoryRequest;
import org.example.DTO.Response.CategoryResponse;
import org.example.DTO.Response.PageResponse;
import org.example.Entities.Categories;
import org.example.Exception.ResourceAlreadyExistsException;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.CategoryMapper;
import org.example.Repository.CategoryRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }


    @Cacheable(value = "categories", key = "'page_'+#pageable.pageNumber+'_'+#pageable.pageSize+'_'+#pageable.sort.toString()")
    public PageResponse<CategoryResponse> getCategories(Pageable pageable) {


        logger.info("Displaying all categories");
        Page<Categories> page = categoryRepository.findAll(pageable);

        Page<CategoryResponse> mapped = page.map(categoryMapper::toResponse);

        return new PageResponse<>(mapped);
    }



    public List<CategoryResponse> getAllCategories() {

        logger.info("Displaying all categories without pagination");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Cacheable(value = "category", key = "#categoryId")
    public CategoryResponse getCategoryById(Long categoryId) {
        logger.info("Fetching category with categoryId: {}", categoryId);
        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.error("Category with categoryId: {} does not exist", categoryId);
                    return new ResourceNotFoundException("Category not found.");
                });
        logger.info("Successfully fetched category:{} (categoryId: {})", category.getCategoryName(), category.getCategoryId() );
        return categoryMapper.toResponse(category);
    }

    @Caching(
            put = {
                    @CachePut(value = "category", key = "#result.categoryId")
            },
            evict = {
                    @CacheEvict(value = "categories", allEntries = true)
            }
    )
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


        Categories savedCategory = categoryRepository.save(newCategory);

        logger.info("Successfully added new category: {}", safeName);

        return categoryMapper.toResponse(savedCategory);
    }


    @Caching(
            put = {
                    @CachePut(value = "category", key = "#result.categoryId")
            },
            evict = {
                    @CacheEvict(value = "categories", allEntries = true)
            }
    )
    public CategoryResponse updateCategoryName(Long id, CategoryRequest request) {

        logger.info("Updating category id: {} with new name: {}", id, request.getCategoryName());

        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category not found");
                });

        String safeName = Jsoup.clean(request.getCategoryName(), Safelist.none()).trim();
        category.setCategoryName(safeName);

        Categories updatedCategory = categoryRepository.save(category);
        logger.info("Successfully updated category id: {} to name: {}", id, request.getCategoryName());

        return categoryMapper.toResponse(updatedCategory);
    }

    @Caching(evict = {
            @CacheEvict(value = "category", key = "#categoryId"),
            @CacheEvict(value = "categories", allEntries = true)
    })
    public void deleteCategory(Long categoryId) {

        logger.info("Attempting to delete category with id: {}", categoryId);
        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        try {
            categoryRepository.delete(category);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                    "Cannot delete category because it is still assigned to products."
            );
        }


        logger.info("Successfully deleted category: {} (id: {})", category.getCategoryName(), categoryId);
    }


}
