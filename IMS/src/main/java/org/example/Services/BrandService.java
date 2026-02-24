package org.example.Services;

import org.example.DTO.Request.BrandRequest;
import org.example.DTO.Request.CategoryRequest;
import org.example.DTO.Response.BrandResponse;
import org.example.DTO.Response.CategoryResponse;
import org.example.DTO.Response.PageResponse;
import org.example.Entities.Brand;
import org.example.Entities.Categories;
import org.example.Exception.ResourceAlreadyExistsException;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.BrandMapper;
import org.example.Repository.BrandRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    private static final Logger logger = LoggerFactory.getLogger(BrandService.class);

    public BrandService(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    @Cacheable(value = "brands", key = "'page_'+#pageable.pageNumber+'_'+#pageable.pageSize+'_'+#pageable.sort.toString()")
    public PageResponse<BrandResponse> getBrands(Pageable pageable) {

        logger.info("Displaying all categories");

        Page<Brand> page = brandRepository.findAll(pageable);

        Page<BrandResponse> mapped = page.map(brandMapper::toResponse);

        return new PageResponse<>(mapped);
    }


    public List<BrandResponse> getAllBrands() {

        logger.info("Displaying all brands without pagination");
        return brandRepository.findAll().stream()
                .map(brandMapper::toResponse)
                .toList();


    }

    @Cacheable(value = "brand", key = "#brandId")
    public BrandResponse getBrandById(Long brandId) {
        logger.info("Fetching brand with id: {}", brandId);
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> {
                    logger.error("Brand with id: {} does not exist", brandId);
                    return new ResourceNotFoundException("Brand not found.");
                });
        logger.info("Successfully fetched brand:{} (id: {})", brand.getBrandName(), brand.getBrandId() );
        return brandMapper.toResponse(brand);
    }

    @Caching(
            put = {
                    @CachePut(value = "brand", key = "#result.brandId")
            },
            evict = {
                    @CacheEvict(value = "brands", allEntries = true)
            }
    )
    public BrandResponse addBrand(BrandRequest request) {

        logger.info("Attempting to add new brand with name: {}", request.getBrandName());

        Brand brand = brandRepository.findByBrandName(request.getBrandName());

        if (brand != null) {
            logger.warn("Brand already exists with name: {}", request.getBrandName());
            throw new ResourceAlreadyExistsException("Brand already exists.");
        }

        String safeName = Jsoup.clean(request.getBrandName(), Safelist.none()).trim();
        Brand newBrand = new Brand();
        newBrand.setBrandName(safeName);

        Brand savedBrand = brandRepository.save(newBrand);

        logger.info("Successfully added new brand: {}", safeName);

        return brandMapper.toResponse(savedBrand);
    }


    @Caching(
            put = {
                    @CachePut(value = "brand", key = "#result.brandId")
            },
            evict = {
                    @CacheEvict(value = "brands", allEntries = true)
            }
    )
    public BrandResponse updateBrandName(Long id, BrandRequest request) {

        logger.info("Updating brand id: {} with new name: {}", id, request.getBrandName());

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Brand not found with id: {}", id);
                    return new ResourceNotFoundException("Brand not found");
                });

        String safeName = Jsoup.clean(request.getBrandName(), Safelist.none()).trim();
        brand.setBrandName(safeName);

        Brand updatedBrand = brandRepository.save(brand);
        logger.info("Successfully updated brand id: {} to name: {}", id, request.getBrandName());

        return brandMapper.toResponse(updatedBrand);
    }

    @Caching(evict = {
            @CacheEvict(value = "brand", key = "#brandId"),
            @CacheEvict(value = "brands", allEntries = true)
    })
    public void deleteBrand(Long id) {

        logger.info("Attempting to delete brand with id: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        brandRepository.delete(brand);

        logger.info("Successfully deleted brand: {} (id: {})", brand.getBrandName(), id);
    }
}
