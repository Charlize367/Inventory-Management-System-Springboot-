package org.example.Services;

import org.example.DTO.Request.SkuRequest;
import org.example.DTO.Response.SkuResponse;
import org.example.Entities.Sku;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.SkuMapper;
import org.example.Repository.SkuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuService {

    private static final Logger logger = LoggerFactory.getLogger(SkuService.class);

    private final SkuRepository skuRepository;
    private final SkuMapper skuMapper;

    public SkuService(SkuRepository skuRepository, SkuMapper skuMapper) {
        this.skuRepository = skuRepository;
        this.skuMapper = skuMapper;

    }

    public Page<SkuResponse> getSkus(Pageable pageable) {
        logger.info("Displaying all SKUs");
        return skuRepository.findAll(pageable)
                .map(skuMapper::toResponse);
    }

    public List<SkuResponse> getAllSkus() {
        logger.info("Displaying all SKUs without pagination");
        return skuRepository.findAll().stream()
                .map(skuMapper::toResponse)
                .toList();
    }

    public SkuResponse findSkuByCode(String skuCode) {
        Sku sku = skuRepository.findBySkuCode(skuCode.trim())
                .orElseThrow(() -> {
                    logger.error("SKU not found with code: {}", skuCode);
                    return new ResourceNotFoundException("SKU not found");
                });
        System.out.println("[" + skuCode + "] length=" + skuCode.length());
        return skuMapper.toResponse(sku);

    }

    public SkuResponse updateSKUStatus(Long id, SkuRequest request) {

        logger.info("Updating status id: {} with new name: {}", id, request.getActive());

        Sku sku = skuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("SKU not found with id: {}", id);
                    return new ResourceNotFoundException("SKU not found");
                });

        sku.setActive(request.getActive());

        Sku updatedSku = skuRepository.save(sku);
        logger.info("Successfully updated sku id: {} to active: {}", id, request.getActive());

        return skuMapper.toResponse(updatedSku);
    }

    public void deleteSKU(Long id) {
        logger.info("Deleting SKU with id: {}", id);
        Sku sku = skuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("SKU to delete is not found with id: {}", id);
                    return new ResourceNotFoundException("SKU not found");
                });
        skuRepository.delete(sku);

        logger.info("Successfully deleted sku with id: {}", id);
    }
}
