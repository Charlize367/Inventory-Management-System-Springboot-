package Unit;


import org.example.DTO.Request.ProductRequest;
import org.example.DTO.Response.ProductResponse;
import org.example.Entities.*;
import org.example.Mapper.ProductMapper;
import org.example.Repository.CategoryRepository;
import org.example.Repository.ProductRepository;
import org.example.Services.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ProductMapper productMapper;


    @InjectMocks
    private ProductService productService;


    @Test
    void testGetProductById() {

        Set<Categories> testCategory = new HashSet<>();
        Set<Variation> testVariation = new HashSet<>();
        Suppliers testSupplier = new Suppliers();
        Brand testBrand = new Brand();

        Products product = new Products(1L, "Test Product", "Test Description", 10.0, 5.0, 100, "test-image.jpg", testCategory, testVariation, true,"PROD",  testSupplier, testBrand);
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse expectedResponse = new ProductResponse();
        expectedResponse.setProductName("Test Product");
        Mockito.when(productMapper.toResponse(product)).thenReturn(expectedResponse);

        ProductResponse result = productService.getProductById(1L);

        Assertions.assertEquals("Test Product", result.getProductName());
    }

    @Test
    void testAddProduct() {

        Set<Categories> testCategory = new HashSet<>();
        Set<Variation> testVariation = new HashSet<>();
        Suppliers testSupplier = new Suppliers();
        Brand testBrand = new Brand();


        Categories mockCategory = new Categories();

        ReflectionTestUtils.setField(productService, "uploadDirectory", "build/test-images");

        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );
        ProductRequest request = new ProductRequest(
                "Test Product",
                "Test Description",
                10.0,
                5.0,
                100,
                List.of(1L, 1L),
                "PROD",
                1L,
                1L,
                1L,
                List.of(1L, 1L),
                "test.jpg"
        );




        mockCategory.setCategoryId(1L);
        mockCategory.setCategoryName("Test Category");
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));

        Products savedProduct = new Products(1L, "Test Product", "Test Description", 10.0, 5.0, 100, "test-image.jpg", testCategory, testVariation, true,"PROD",  testSupplier, testBrand);
        Mockito.when(productRepository.save(Mockito.any(Products.class))).thenReturn(savedProduct);


        ProductResponse expectedResponse = new ProductResponse();
        expectedResponse.setProductId(1L);
        expectedResponse.setProductName("Test Product");
        expectedResponse.setProductImage("test-image.jpg");
        Mockito.when(productMapper.toResponse(savedProduct)).thenReturn(expectedResponse);

        ProductResponse result = productService.addProduct(request);

        Assertions.assertEquals(1L, result.getProductId());
        Assertions.assertEquals("Test Product", result.getProductName());
        Assertions.assertTrue(result.getProductImage().contains("test-image.jpg"));


        Mockito.verify(productRepository, Mockito.times(1)).save(Mockito.any(Products.class));


    }

    @Test
    void testUpdateProduct() {

        ReflectionTestUtils.setField(productService, "uploadDirectory", "build/test-images");

        Set<Categories> testCategory = new HashSet<>();
        Set<Variation> testVariation = new HashSet<>();
        Suppliers testSupplier = new Suppliers();
        Brand testBrand = new Brand();


        Categories mockCategory = new Categories();
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));

        Long productId = 1L;
        Products existingProduct = new Products(productId, "Test Product", "Test Description", 10.0, 5.0, 100, "test-image.jpg", testCategory, testVariation, true,"PROD",  testSupplier, testBrand);
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        Products updatedProducts = new Products(productId, "Test Product", "Test Description", 10.0, 5.0, 100, "test-image.jpg", testCategory, testVariation, true,"PROD",  testSupplier, testBrand);
        Mockito.when(productRepository.save(Mockito.any(Products.class))).thenReturn(updatedProducts);


        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image-updated.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        ProductRequest request = new ProductRequest(
                "Test Product",
                "Test Description",
                10.0,
                5.0,
                100,
                List.of(1L, 1L),
                "PROD",
                1L,
                1L,
                1L,
                List.of(1L, 1L),
                "test-image.jpg"
        );



        ProductResponse expectedResponse = new ProductResponse();
        expectedResponse.setProductId(1L);
        expectedResponse.setProductStock(200);
        expectedResponse.setProductImage("test-image-updated.jpg");
        Mockito.when(productMapper.toResponse(updatedProducts)).thenReturn(expectedResponse);

        ProductResponse result = productService.updateProductDetails(productId, request);

        Assertions.assertEquals(productId, result.getProductId());
        Assertions.assertEquals(200, result.getProductStock());
        Assertions.assertTrue(result.getProductImage().contains("test-image-updated.jpg"));
    }

    @Test
    void testDeleteProduct() {

        Set<Categories> testCategory = new HashSet<>();
        Set<Variation> testVariation = new HashSet<>();
        Suppliers testSupplier = new Suppliers();
        Brand testBrand = new Brand();


        Categories mockCategory = new Categories();
        Long productId = 1L;
        Products existingProduct = new Products(productId, "Test Product", "Test Description", 10.0, 5.0, 100, "test-image.jpg", testCategory, testVariation, true,"PROD",  testSupplier, testBrand);
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        productService.deleteProduct(productId);

        Mockito.verify(productRepository).findById(productId);

        Mockito.verify(productRepository).delete(existingProduct);


    }
}
