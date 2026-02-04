package org.example.Services;

import jakarta.transaction.Transactional;
import org.example.DTO.Request.ProductRequest;
import org.example.DTO.Response.ProductResponse;
import org.example.Entities.*;
import org.example.Exception.ResourceAlreadyExistsException;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.ProductMapper;
import org.example.Repository.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockMovementsRepository stockMovementsRepository;
    private final UsersRepository usersRepository;
    private final ProductMapper productMapper;
    private final SkuRepository skuRepository;
    private final SuppliersRepository suppliersRepository;
    private final BrandRepository brandRepository;
    private final VariationRepository variationRepository;
    private final VariationOptionsRepository variationOptionsRepository;
    private final VariationService variationService;


    public static String uploadDirectory = "/Users/charlizemendoza/Documents/IMSImages";

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, StockMovementsRepository stockMovementsRepository, UsersRepository usersRepository, ProductMapper productMapper, SkuRepository skuRepository, SuppliersRepository suppliersRepository, BrandRepository brandRepository, VariationRepository variationRepository, VariationOptionsRepository variationOptionsRepository, VariationService variationService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.stockMovementsRepository = stockMovementsRepository;
        this.usersRepository = usersRepository;
        this.productMapper = productMapper;
        this.skuRepository = skuRepository;
        this.suppliersRepository = suppliersRepository;
        this.brandRepository = brandRepository;
        this.variationRepository = variationRepository;
        this.variationOptionsRepository = variationOptionsRepository;
        this.variationService = variationService;
    }


    public Page<ProductResponse> getProducts(Long categoryId, Long brandId, Pageable pageable) {
        logger.info("Displaying all products");

        if(categoryId != null && brandId != null) {
            return productRepository.findByBrandBrandIdAndCategoriesCategoryIdAndActiveTrue(brandId, categoryId, pageable)
                    .map(productMapper::toResponse);
        }

        if (categoryId != null) {
            return productRepository.findByCategoriesCategoryIdAndActiveTrue(categoryId, pageable)
                    .map(productMapper::toResponse);
        }

        if (brandId != null) {
            return productRepository.findByBrandBrandIdAndActiveTrue(brandId, pageable)
                    .map(productMapper::toResponse);
        }

        return productRepository.findByActiveTrue(pageable)
                .map(productMapper::toResponse);
    }

    public List<ProductResponse> getAllProducts(Long categoryId, Long brandId, String query) {
        logger.info("Displaying all products without pagination");


        if (categoryId != null) {
            return productRepository.findAllByCategoriesCategoryIdAndActiveTrue(categoryId).stream()
                    .map(productMapper::toResponse)
                    .toList();
        }

        if (brandId != null) {
            return productRepository.findAllByBrandBrandIdAndActiveTrue(brandId).stream()
                    .map(productMapper::toResponse)
                    .toList();
        }

        if (query != null) {
            return productRepository.findByProductNameContainingIgnoreCase(query).stream()
                    .map(productMapper::toResponse)
                    .toList();

        }
        return productRepository.findAllByActiveTrue().stream()
                .map(productMapper::toResponse)
                .toList();


    }


    public ProductResponse getProductById(Long id) {
        logger.info("Fetching product with id: {}", id);
        Products product = productRepository.findByProductIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    logger.error("Product with id: {} does not exist", id);
                    return new ResourceNotFoundException("Product not found.");
                });

        logger.info("Successfully fetched products:{} (id: {})", product.getProductName(), product.getProductId() );
        return productMapper.toResponse(product);
    }


    @Transactional
    public ProductResponse addProduct(ProductRequest request) {

        logger.info("Attempting to add new product with name: {}", request.getProductName());


        Suppliers supplier = suppliersRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found."));

        Products product = productRepository.findByProductName(request.getProductName());
        if(product != null) {
            logger.warn("Product already exists with name: {}", request.getProductName());
            throw new ResourceAlreadyExistsException("Product already exists.");
        } else {

            String safeName = Jsoup.clean(request.getProductName(), Safelist.none()).trim();
            String safeDescription = Jsoup.clean(request.getProductDescription(), Safelist.none()).trim();
            String productCode = request.getProductCode().toUpperCase();

            Products newProduct = new Products();
            newProduct.setProductName(safeName);
            newProduct.setProductDescription(safeDescription);
            newProduct.setProductPrice(request.getProductPrice());
            newProduct.setProductCost(request.getProductCost());
            newProduct.setProductStock(request.getProductStock());
            newProduct.setProductCode(productCode);
            newProduct.setSupplier(supplier);
            newProduct.setBrand(brand);

            Set<Categories> categories = new HashSet<>(
                    categoryRepository.findAllById(request.getCategoryId())
            );
            newProduct.setCategories(categories);

            Products savedProduct = productRepository.save(newProduct);

            if (request.getVariationIds() == null) {
                Sku skuData = new Sku();
                skuData.setSkuCode(savedProduct.getProductCode());
                skuData.setFinalPrice(savedProduct.getProductPrice());
                skuRepository.save(skuData);
            } else {


                List<Variation> variation = variationRepository.findAllById(request.getVariationIds()
                        .stream().toList());


                newProduct.setVariations(new HashSet<>(variation));

                productRepository.save(newProduct);

                List<String> skus = generateSKUs(
                        newProduct.getProductCode(),
                        new ArrayList<>(newProduct.getVariations())
                );

                List<Sku> skuList = skuRepository.findAllByProduct(savedProduct);

                for (String sku : skus) {
                    Sku skuData = new Sku();
                    String[] parts = sku.split("-");
                    List<String> optionCodes = Arrays.asList(parts).subList(1, parts.length);
                    List<VariationOptions> optionsForSku = variationOptionsRepository.findAll()
                            .stream()
                            .filter(option -> optionCodes.contains(option.getVariationOptionCode()))
                            .toList();

                    double totalAdjustment = optionsForSku.stream()
                            .mapToDouble(VariationOptions::getVariationPriceAdjustment)
                            .sum();


                    Sku skuInfo = skuRepository.getBySkuCode(sku);
                    skuData.setSkuCode(sku);
                    if (skuInfo != null) {
                        skuData.setStockQuantity(skuInfo.getStockQuantity());
                    } else {
                        skuData.setStockQuantity(0);
                    }
                    skuData.setFinalPrice(newProduct.getProductPrice() + totalAdjustment);
                    skuData.setActive(true);
                    skuData.setProduct(savedProduct);
                    skuData.setVariationOptions(optionsForSku);
                    skuRepository.save(skuData);
                }
                savedProduct.setSku(skuList);
                productRepository.save(savedProduct);
                System.out.println("SkuList: " + skuList);
            }





            logger.info("Successfully added new product: {}", safeName);

            return productMapper.toResponse(savedProduct);
        }

    }

    public void saveImage(Long productId, MultipartFile file) throws IOException {
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));


        Files.createDirectories(Paths.get(uploadDirectory));

        if (product.getProductImage() != null) {
            Path oldPath = Paths.get(uploadDirectory, product.getProductImage());

            if (Files.exists(oldPath)) {
                Files.delete(oldPath);
            }
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDirectory, fileName);

        Files.write(filePath, file.getBytes());


        product.setProductImage(fileName);
        productRepository.save(product);
    }


    public ProductResponse updateProductDetails(Long id, ProductRequest request) {

        logger.info("Updating product id: {} ", id);

        Products product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product not found");
                });

        Suppliers supplier = suppliersRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found."));

        String safeName = Jsoup.clean(request.getProductName(), Safelist.none()).trim();
        String safeDescription = Jsoup.clean(request.getProductDescription(), Safelist.none()).trim();
        String productCode = request.getProductCode().toUpperCase();

        product.setProductName(safeName);
        product.setProductDescription(safeDescription);
        product.setProductPrice(request.getProductPrice());
        product.setProductCost(request.getProductCost());
        product.setProductStock(request.getProductStock());
        product.setProductCode(productCode);
        product.setSupplier(supplier);
        product.setBrand(brand);




        Set<Long> oldVariationIds = product.getVariations()
                .stream()
                .map(Variation::getVariationId)
                .collect(Collectors.toSet());



        List<Variation> variation = variationRepository.findAllById(request.getVariationIds()
                .stream().toList());
        product.setVariations(new HashSet<>(variation));

        Set<Long> newVariationIds = new HashSet<>(request.getVariationIds());

        if (!newVariationIds.equals(oldVariationIds)) {

            List<String> skus = generateSKUs(
                    product.getProductCode(),
                    new ArrayList<>(product.getVariations())
            );



            for (String sku : skus) {
                Sku skuData = new Sku();
                String[] parts = sku.split("-");
                List<String> optionCodes = Arrays.asList(parts).subList(1, parts.length);
                List<VariationOptions> optionsForSku = variationOptionsRepository.findAll()
                        .stream()
                        .filter(option -> optionCodes.contains(option.getVariationOptionCode()))
                        .toList();

                double totalAdjustment = optionsForSku.stream()
                        .mapToDouble(VariationOptions::getVariationPriceAdjustment)
                        .sum();


                Sku skuInfo = skuRepository.getBySkuCode(sku);
                skuData.setSkuCode(sku);
                if (skuInfo != null) {
                    skuData.setStockQuantity(skuInfo.getStockQuantity());
                } else {
                    skuData.setStockQuantity(0);
                }
                skuData.setFinalPrice(product.getProductPrice() + totalAdjustment);
                skuData.setActive(true);
                skuData.setProduct(product);
                skuData.setVariationOptions(optionsForSku);
                skuRepository.save(skuData);
            }



            if (!Objects.equals(product.getProductStock(), request.getProductStock())) {
                StockMovements stockMovement = new StockMovements();
                if (product.getProductStock() > request.getProductStock()) {
                    stockMovement.setQuantityChange(product.getProductStock() - request.getProductStock());
                } else {
                    stockMovement.setQuantityChange(request.getProductStock() - product.getProductStock());
                }

                Users staff = usersRepository.findById(request.getStaffId())
                        .orElseThrow(() -> new ResourceNotFoundException("Staff not found."));

                stockMovement.setStockMovement(StockMovements.StockMovement.ADJUSTMENT);
                stockMovement.setStaff(staff);
                stockMovementsRepository.save(stockMovement);
            }

            Set<Categories> categories = new HashSet<>(
                    categoryRepository.findAllById(request.getCategoryId())
            );
            product.setCategories(categories);
            }




            Products updatedProduct = productRepository.save(product);
            logger.info("Successfully updated customer id: {}", id);
            return productMapper.toResponse(updatedProduct);


    }


    public void deleteProduct(Long id) {

        logger.info("Attempting to delete product by ID");
        Products product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setActive(false);
        productRepository.save(product);

        logger.info("Successfully deleted product: {} (id: {})", product.getProductName(), id);
    }


    public List<String> generateSKUs(String productCode, List<Variation> variations) {

        List<List<String>> variationPairs = new ArrayList<>();
        variationPairs.add(new ArrayList<>());

        for (Variation variation : variations) {
            List<List<String>> tempPairs = new ArrayList<>();

            for(List<String> pairsCreated : variationPairs) {
                for (VariationOptions option : variation.getVariationOptions()) {
                    List<String> newPair = new ArrayList<>(pairsCreated);
                    newPair.add(option.getVariationOptionCode());
                    tempPairs.add(newPair);
                }
            }

            variationPairs = tempPairs;
        }

        return variationPairs.stream()
                .map(combo -> productCode.toUpperCase( ) + "-" + String.join("-", combo))
                .collect(Collectors.toList());
    }


}
