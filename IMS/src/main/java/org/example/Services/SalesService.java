package org.example.Services;

import jakarta.transaction.Transactional;
import org.example.DTO.Request.*;
import org.example.DTO.Response.PurchaseResponse;
import org.example.DTO.Response.SalesResponse;
import org.example.Entities.*;
import org.example.Event.StockChangeEvent;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.SalesMapper;
import org.example.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);

    public SalesService(SalesRepository salesRepository, CustomersRepository customersRepository, UsersRepository usersRepository, ProductRepository productRepository, SaleItemsRepository saleItemsRepository, StockMovementsRepository stockMovementsRepository, SalesMapper salesMapper, PurchaseItemsRepository purchaseItemsRepository, SkuRepository skuRepository, VariationOptionsRepository variationOptionsRepository, ApplicationEventPublisher eventPublisher) {
        this.salesRepository = salesRepository;
        this.customersRepository = customersRepository;
        this.usersRepository = usersRepository;
        this.productRepository = productRepository;
        this.saleItemsRepository = saleItemsRepository;
        this.stockMovementsRepository = stockMovementsRepository;
        this.purchaseItemsRepository = purchaseItemsRepository;
        this.salesMapper = salesMapper;
        this.skuRepository = skuRepository;
        this.variationOptionsRepository = variationOptionsRepository;
        this.eventPublisher = eventPublisher;

    }

    private final SalesRepository salesRepository;
    private final CustomersRepository customersRepository;
    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private final SaleItemsRepository saleItemsRepository;
    private final StockMovementsRepository stockMovementsRepository;
    private final PurchaseItemsRepository purchaseItemsRepository;
    private final SkuRepository skuRepository;
    private final VariationOptionsRepository variationOptionsRepository;
    private final SalesMapper salesMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Page<SalesResponse> getAllSales(Long staffId, Pageable pageable) {
        logger.info("Displaying all sales");
        if (staffId != null) {
            return salesRepository.findSalesByStaff_UserId(staffId, pageable)
                    .map(salesMapper::toResponse);
        }
        return salesRepository.findAll(pageable)
                .map(salesMapper::toResponse);
    }

    public SalesResponse getSalesById(Long id) {
        logger.info("Fetching sales record with id: {}", id);
        Sales sale = salesRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Sales record with id: {} does not exist", id);
                    return new ResourceNotFoundException("Sales record not found.");
                });
        logger.info("Successfully fetched sales record with id: {}", id);
        return salesMapper.toResponse(sale);
    }

    @Transactional
    public SalesResponse addSale(SalesRequest request) {

        logger.info("Attempting to add new sale");

        Customers customer = null;
        if (request.getCustomerId() != null) {
            customer = customersRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }

        Users staff = usersRepository.findById(request.getStaffId())

                .orElseThrow(() -> new ResourceNotFoundException("Staff not found."));

        List<Long> productIds = request.getSalesItem().stream()
                .map(SaleItemRequest::getProductId)
                .toList();

        Map<Long, Products> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Products::getProductId, Function.identity()));

        Sales sale = new Sales();
        sale.setCustomer(customer);
        sale.setStaff(staff);
        sale.setSalesPaymentStatus(Sales.SalesPaymentStatus.PENDING);
        sale.setSaleDate(LocalDate.now());

        sale = salesRepository.save(sale);

        double totalAmount = 0;
        SaleItems savedSaleItems = null;
        for (SaleItemRequest productItem : request.getSalesItem()) {

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

            int newStock = productItem.getSaleItemQuantity();
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

                            return skuOptionIds.equals(sortedSelectedIds); // exact match
                        })
                        .findFirst();

                //check if selected skus are present in sku option ids
                if(matchingSkus.isPresent()) {
                    Sku sku = matchingSkus.get();

                    sku.setStockQuantity(sku.getStockQuantity() - newStock);
                    skuRepository.save(sku);
                    logger.info("SKU {} current stock: {}, selling: {}", sku.getSkuCode(), sku.getStockQuantity(), newStock);

                } else {
                    throw new RuntimeException("Matching SKU not found for variation options");
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

                defaultSku.setStockQuantity(defaultSku.getStockQuantity() - newStock);

                skuRepository.save(defaultSku);
            }

            totalAmount = totalAmount + product.getProductPrice() * productItem.getSaleItemQuantity();


            System.out.println("Selected variation options size: " + selectedOptions.size());

            SaleItems saleItems = new SaleItems();
            saleItems.setSales(sale);
            saleItems.setSalesPrice((double) (product.getProductPrice() * productItem.getSaleItemQuantity()));
            saleItems.setProduct(product);
            saleItems.setSalesItemQuantity(productItem.getSaleItemQuantity());
            saleItems.setVariationOptions(selectedOptionsSet);

            saleItemsRepository.saveAndFlush(saleItems);


            List<Sku> skus = skuRepository.findByProduct(product);


            int totalStock = skus.stream()
                    .mapToInt(Sku::getStockQuantity)
                    .sum();

            product.setProductStock(totalStock);
            eventPublisher.publishEvent(new StockChangeEvent(product));
            productRepository.save(product);

            StockMovements movement = new StockMovements();

            movement.setProduct(product);
            movement.setQuantityChange(productItem.getSaleItemQuantity());
            movement.setStockMovement(StockMovements.StockMovement.SALE);
            movement.setStaff(staff);
            movement.setCreatedAt(LocalDateTime.now());
            stockMovementsRepository.save(movement);

        }


        sale.setSalesPaymentStatus(Sales.SalesPaymentStatus.PAID);
        sale.setSalesAmount(totalAmount);
        Sales savedSales = salesRepository.save(sale);
        salesRepository.flush();
        logger.info("Successfully added sale for Customer ID: {}", request.getCustomerId());

        return salesMapper.toResponse(savedSales);
    }


    public SalesResponse updateSalesPaymentStatus(Long id, SalesPaymentStatusRequest request) {

        logger.info("Updating status id: {} with new name: {}", id, request.getSalesPaymentStatus());

        Sales sale = salesRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Sale not found with id: {}", id);
                    return new ResourceNotFoundException("Sale not found");
                });

        sale.setSalesPaymentStatus(Sales.SalesPaymentStatus.valueOf(request.getSalesPaymentStatus()));

        Sales updatedSale = salesRepository.save(sale);
        logger.info("Successfully updated sales id: {} to name: {}", id, request.getSalesPaymentStatus());

        return salesMapper.toResponse(updatedSale);
    }

    public void deleteSale(Long id) {
        logger.info("Deleting purchase record with id: {}", id);
        Sales sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        salesRepository.delete(sale);
        logger.info("Successfully deleted sales record with id: {}", id);
    }




}
