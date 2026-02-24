package org.example.Services;


import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Request.StockMovementRequest;
import org.example.DTO.Response.CustomerResponse;
import org.example.DTO.Response.PageResponse;
import org.example.DTO.Response.SkuResponse;
import org.example.DTO.Response.StockMovementResponse;
import org.example.Entities.Customers;
import org.example.Entities.Purchases;
import org.example.Entities.Sku;
import org.example.Entities.StockMovements;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.StockMovementsMapper;
import org.example.Repository.StockMovementsRepository;
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
import java.util.stream.Collectors;

@Service
public class StockMovementsService {

    private static final Logger logger = LoggerFactory.getLogger(StockMovementsService.class);

    private final StockMovementsRepository stockMovementsRepository;
    private final StockMovementsMapper stockMovementsMapper;

    public StockMovementsService(StockMovementsRepository stockMovementsRepository, StockMovementsMapper stockMovementsMapper) {
        this.stockMovementsRepository = stockMovementsRepository;
        this.stockMovementsMapper = stockMovementsMapper;
    }

    @Cacheable(value = "stockMovements", key = "'page_'+#pageable.pageNumber+'_'+#pageable.pageSize+'_'+#pageable.sort.toString()")
    public PageResponse<StockMovementResponse> getAllStockMovements(Pageable pageable) {
        logger.info("Displaying all stock movement records");
        Page<StockMovements> page = stockMovementsRepository.findAll(pageable);

        Page<StockMovementResponse> mapped = page.map(stockMovementsMapper::toResponse);

        return new PageResponse<>(mapped);
    }

    @Cacheable(value = "stockMovement", key = "#stockMovementId")
    public StockMovementResponse getStockMovementById(Long id) {
        logger.info("Fetching stock movement record with id: {}", id);
        StockMovements stockMovement = stockMovementsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Stock movement record with id: {} does not exist", id);
                    return new ResourceNotFoundException("Stock movement record not found.");
                });
        logger.info("Successfully fetched stock movement record with id {}", stockMovement.getStockMovementId());
        return stockMovementsMapper.toResponse(stockMovement);
    }

    @Caching(
            put = {
                    @CachePut(value = "stockMovement", key = "#result.stockMovementId")
            },
            evict = {
                    @CacheEvict(value = "stockMovements", allEntries = true)
            }
    )
    public StockMovementResponse updateStockMovement(Long id, StockMovementRequest request) {

        logger.info("Updating stock movement id: {} ", id);

        StockMovements stockMovement = stockMovementsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Stock Movement not found with id: {}", id);
                    return new ResourceNotFoundException("Stock Movement not found");
                });

        StockMovements updatedStockMovement = stockMovementsRepository.save(stockMovement);

        logger.info("Successfully updated stock movement id: {}", id);

        return stockMovementsMapper.toResponse(updatedStockMovement);
    }

    @Caching(evict = {
            @CacheEvict(value = "stockMovement", key = "#stockMovementId"),
            @CacheEvict(value = "stockMovements", allEntries = true)
    })
    public void deleteStockMovement(Long stockMovementId) {

        logger.info("Attempting to delete stock movement by ID");

        StockMovements stockMovement = stockMovementsRepository.findById(stockMovementId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        stockMovementsRepository.delete(stockMovement);

        logger.info("Successfully deleted stock movement (id: {})", stockMovementId);
    }
}
