package org.example.Services;


import io.sentry.protocol.App;
import jakarta.transaction.Transactional;
import org.example.DTO.Request.PurchaseItemRequest;
import org.example.DTO.Request.PurchaseRequest;
import org.example.DTO.Request.PurchaseStatusRequest;
import org.example.DTO.Request.VariationOptionRequest;
import org.example.DTO.Response.PurchaseResponse;
import org.example.Entities.*;
import org.example.Event.StockChangeEvent;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.PurchasesMapper;
import org.example.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    public PurchaseService(PurchaseRepository purchaseRepository, SuppliersRepository suppliersRepository, UsersRepository usersRepository, ProductRepository productRepository, PurchaseItemsRepository purchaseItemsRepository, StockMovementsRepository stockMovementsRepository, PurchasesMapper purchasesMapper, SkuRepository skuRepository, VariationOptionsRepository variationOptionsRepository, ApplicationEventPublisher eventPublisher) {
        this.purchaseRepository = purchaseRepository;
        this.suppliersRepository = suppliersRepository;
        this.usersRepository = usersRepository;
        this.productRepository = productRepository;
        this.purchaseItemsRepository = purchaseItemsRepository;
        this.stockMovementsRepository = stockMovementsRepository;
        this.purchasesMapper = purchasesMapper;
        this.skuRepository = skuRepository;
        this.variationOptionsRepository = variationOptionsRepository;
        this.eventPublisher = eventPublisher;
    }

    private final PurchaseRepository purchaseRepository;
    private final SuppliersRepository suppliersRepository;
    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private final PurchaseItemsRepository purchaseItemsRepository;
    private final StockMovementsRepository stockMovementsRepository;
    private final PurchasesMapper purchasesMapper;
    private final SkuRepository skuRepository;
    private final VariationOptionsRepository variationOptionsRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Page<PurchaseResponse> getAllPurchases(Long staffId, Pageable pageable) {
        logger.info("Displaying all purchases");
        if (staffId != null) {
            return purchaseRepository.findPurchasesByStaff_UserId(staffId, pageable)
                    .map(purchasesMapper::toResponse);
        }
        return purchaseRepository.findAll(pageable)
                .map(purchasesMapper::toResponse);
    }

    public PurchaseResponse getPurchaseById(Long id) {
        logger.info("Fetching purchase record with id: {}", id);
        Purchases purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Purchase record with id: {} does not exist", id);
                    return new ResourceNotFoundException("Purchase record not found.");
                });


        logger.info("Successfully fetched purchase record with id: {}", id);
        return purchasesMapper.toResponse(purchase);

    }

    @Transactional
    public PurchaseResponse addPurchase(PurchaseRequest request) {

        logger.info("Attempting to add new purchase from Supplier ID: {}", request.getSupplierId());


        Suppliers supplier = suppliersRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

        Users staff = usersRepository.findById(request.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found."));

        List<Long> productIds = request.getPurchaseItems().stream()
                .map(PurchaseItemRequest::getProductId)
                .toList();

        Map<Long, Products> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Products::getProductId, Function.identity()));

        Purchases purchase = new Purchases();
        purchase.setSupplier(supplier);
        purchase.setStaff(staff);
        purchase.setPurchaseStatus(Purchases.PurchaseStatus.PENDING);
        purchase.setPurchaseDate(LocalDate.now());


        double totalAmount = 0;

        for (PurchaseItemRequest productItem : request.getPurchaseItems()) {

            Products product = productMap.get(productItem.getProductId());

            List<VariationOptions> selectedOptions = productItem.getVariationOptions().stream()
                    .map(req -> variationOptionsRepository.findByVariationOptionCode(req.getVariationOptionCode())
                            .orElseThrow(() -> new RuntimeException(
                                    "Variation option not found: " + req.getVariationOptionCode()
                            )))
                    .toList();

            Set<VariationOptions> selectedOptionsSet = productItem.getVariationOptions().stream()
                    .map(req -> variationOptionsRepository.findByVariationOptionCode(req.getVariationOptionCode())
                            .orElseThrow(() -> new RuntimeException("Variation option not found: " + req.getVariationOptionCode())))
                    .collect(Collectors.toSet());

            int newStock = productItem.getPurchaseItemQuantity();
            if (productItem.getVariationOptions() != null) {
                //get the actual entity of the variation option requests


                //get the ids of variation options
                List<Long> selectedIds = selectedOptions.stream()
                        .map(VariationOptions::getVariationOptionId)
                        .toList();

                //get the skus by product
                List<Sku> skus = skuRepository.findByProduct(product);

                //get variation option ids from skus (with the same product) and see if it matches with selected ids
                Optional<Sku> matchingSkus = skus.stream()
                        .filter(sku -> {
                            List<Long> skuOptionIds = sku.getVariationOptions().stream()
                                    .map(VariationOptions::getVariationOptionId)
                                    .sorted()
                                    .toList();
                            List<Long> sortedSelectedIds = selectedIds.stream().sorted().toList();

                            return skuOptionIds.equals(sortedSelectedIds);
                        })

                        .findFirst();

              
                //check if selected skus are present in sku option ids
                if(matchingSkus.isPresent()) {
                    Sku sku = matchingSkus.get();

                    sku.setStockQuantity(sku.getStockQuantity() + newStock);
                    skuRepository.save(sku);
                    logger.info("SKU {} current stock: {}, purchasing: {}", sku.getSkuCode(), sku.getStockQuantity(), newStock);

                } else {
                    throw new RuntimeException("Matching SKU not found for variation options. SKUs: " + skus + "Matching skus" +  matchingSkus);
                }

            } else {
                List<Sku> defaultSkus = skuRepository.findByProductAndVariationOptionsEmpty(product);

                Sku defaultSku;
                if (defaultSkus.isEmpty()) {
                    defaultSku = new Sku();
                    defaultSku.setSkuCode(product.getProductCode());
                    defaultSku.setStockQuantity(0);

                    defaultSku.setProduct(product);
                } else {
                    defaultSku = defaultSkus.get(0);
                }

                if (defaultSku.getFinalPrice() == null) {
                    defaultSku.setFinalPrice(product.getProductPrice());
                }

                defaultSku.setStockQuantity(defaultSku.getStockQuantity() + newStock);
                skuRepository.save(defaultSku);
            }

            totalAmount = totalAmount +  product.getProductCost() * productItem.getPurchaseItemQuantity();



            PurchaseItems purchaseItems = new PurchaseItems();
            purchaseItems.setPurchases(purchase);
            purchaseItems.setPurchaseCostPrice((double) (product.getProductCost() * productItem.getPurchaseItemQuantity()));
            purchaseItems.setProduct(product);
            purchaseItems.setVariationOptions(selectedOptionsSet);
            purchaseItems.setPurchaseItemQuantity(productItem.getPurchaseItemQuantity());

            purchaseItemsRepository.saveAndFlush(purchaseItems);


            List<Sku> skus = skuRepository.findByProduct(product);
            int totalStock = skus.stream()
                    .mapToInt(Sku::getStockQuantity)
                    .sum();

            product.setProductStock(totalStock);
            eventPublisher.publishEvent(new StockChangeEvent(product));


            productRepository.save(product);

            StockMovements movement = new StockMovements();

            movement.setProduct(product);
            movement.setQuantityChange(productItem.getPurchaseItemQuantity());
            movement.setStockMovement(StockMovements.StockMovement.PURCHASE);
            movement.setStaff(staff);



            movement.setCreatedAt(LocalDateTime.now());
            stockMovementsRepository.save(movement);


        }

        purchase.setPurchaseStatus(Purchases.PurchaseStatus.PENDING);
        purchase.setPurchaseAmount(totalAmount);


        Purchases savedPurchase = purchaseRepository.save(purchase);



        logger.info("Successfully added purchase from Supplier ID: {}", request.getSupplierId());

        return purchasesMapper.toResponse(savedPurchase);
    }

    public PurchaseResponse updatePurchaseStatus(Long id, PurchaseStatusRequest request) {

        logger.info("Updating status id: {} with new name: {}", id, request.getPurchaseStatus());

        Purchases purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Purchase not found with id: {}", id);
                    return new ResourceNotFoundException("Purchase not found");
                });

        purchase.setPurchaseStatus(Purchases.PurchaseStatus.valueOf(request.getPurchaseStatus()));

        Purchases updatedPurchase = purchaseRepository.save(purchase);
        logger.info("Successfully updated purchase id: {} to name: {}", id, request.getPurchaseStatus());

        return purchasesMapper.toResponse(updatedPurchase);
    }



    public void deletePurchase(Long id) {
        logger.info("Deleting purchase record with id: {}", id);
        Purchases purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));
        purchaseRepository.delete(purchase);

        logger.info("Successfully deleted purchase record with id: {}", id);
    }
}
