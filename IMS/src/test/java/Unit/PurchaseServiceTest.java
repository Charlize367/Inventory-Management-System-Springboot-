package Unit;

import org.example.DTO.Request.PurchaseItemRequest;
import org.example.DTO.Request.PurchaseRequest;
import org.example.DTO.Response.ProductResponse;
import org.example.DTO.Response.PurchaseItemResponse;
import org.example.DTO.Response.PurchaseResponse;
import org.example.Entities.*;
import org.example.Mapper.PurchasesMapper;
import org.example.Repository.*;
import org.example.Services.PurchaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {



    @Mock
    PurchaseRepository purchaseRepository;

    @Mock
    SuppliersRepository suppliersRepository;

    @Mock
    UsersRepository usersRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    PurchaseItemsRepository purchaseItemsRepository;

    @Mock
    StockMovementsRepository stockMovementsRepository;

    @Mock
    PurchasesMapper purchasesMapper;

    @InjectMocks
    private PurchaseService purchaseService;


    @Test
    void testGetPurchaseById() {

        Suppliers testSupplier = new Suppliers();
        testSupplier.setSupplierId(1L);
        testSupplier.setSupplierName("ABC Supplier");

        Users testStaff = new Users();
        testStaff.setUserId(2L);
        testStaff.setUsername("John Doe");
        testStaff.setRole(Users.Role.STAFF);

        Purchases purchase = new Purchases(1L, testSupplier, testStaff, 100.0, Purchases.PurchaseStatus.COMPLETE, LocalDate.now());
        Mockito.when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        PurchaseResponse expectedResponse = new PurchaseResponse();
        expectedResponse.setPurchaseId(1L);
        Mockito.when(purchasesMapper.toResponse(purchase)).thenReturn(expectedResponse);

        PurchaseResponse result = purchaseService.getPurchaseById(1L);

        Assertions.assertEquals(1L, result.getPurchaseId());
    }

    @Test
    void testAddPurchase() {

        Suppliers testSupplier = new Suppliers();
        testSupplier.setSupplierId(1L);
        testSupplier.setSupplierName("ABC Supplier");

        Users testStaff = new Users();
        testStaff.setUserId(2L);
        testStaff.setUsername("John Doe");
        testStaff.setRole(Users.Role.STAFF);

        Categories testCategory = new Categories();
        Products product1 = new Products(1L, "Test Product 1", "Test Description 1", 10.0, 10.0, 50, "test-image.jpg", testCategory, true);
        Products product2 = new Products(2L, "Test Product 2", "Test Description 2", 12.0, 12.0, 50, "test-image2.jpg", testCategory, true);

        List<PurchaseItemRequest> purchaseItemRequests = List.of(
                new PurchaseItemRequest(1L, 10),
                new PurchaseItemRequest(2L, 5)
        );

        PurchaseRequest request = new PurchaseRequest(
                1L,
                2L,
                purchaseItemRequests
        );


        Mockito.when(suppliersRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        Mockito.when(usersRepository.findById(2L)).thenReturn(Optional.of(testStaff));
        Mockito.when(productRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(product1, product2));

        Mockito.when(purchaseRepository.save(Mockito.any(Purchases.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(purchaseItemsRepository.save(Mockito.any(PurchaseItems.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(productRepository.save(Mockito.any(Products.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(stockMovementsRepository.save(Mockito.any(StockMovements.class))).thenAnswer(invocation -> invocation.getArgument(0));


        PurchaseResponse mockResponse = new PurchaseResponse();
        mockResponse.setPurchaseStatus("COMPLETE");
        mockResponse.setPurchaseDate(LocalDate.now());
        mockResponse.setPurchaseItems(List.of(new PurchaseItemResponse()));
        Mockito.when(purchasesMapper.toResponse(Mockito.any(Purchases.class)))
                .thenReturn(mockResponse);


        PurchaseResponse response = purchaseService.addPurchase(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("COMPLETE", response.getPurchaseStatus());
        Assertions.assertNotNull(response.getPurchaseItems());


        Mockito.verify(suppliersRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(usersRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(productRepository, Mockito.times(1)).findAllById(List.of(1L, 2L));
        Mockito.verify(purchaseRepository, Mockito.times(1)).save(Mockito.any(Purchases.class));
        Mockito.verify(purchaseItemsRepository, Mockito.times(2)).save(Mockito.any(PurchaseItems.class));
        Mockito.verify(stockMovementsRepository, Mockito.times(2)).save(Mockito.any(StockMovements.class));
    }


    @Test
    void testDeletePurchase() {

        Suppliers testSupplier = new Suppliers();
        testSupplier.setSupplierId(1L);
        testSupplier.setSupplierName("ABC Supplier");

        Users testStaff = new Users();
        testStaff.setUserId(2L);
        testStaff.setUsername("John Doe");
        testStaff.setRole(Users.Role.STAFF);

        Long purchaseId = 1L;
        Purchases existingPurchase = new Purchases(purchaseId, testSupplier, testStaff, 100.0, Purchases.PurchaseStatus.COMPLETE, LocalDate.now());
        Mockito.when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(existingPurchase));

        purchaseService.deletePurchase(purchaseId);

        Mockito.verify(purchaseRepository).findById(purchaseId);

        Mockito.verify(purchaseRepository).delete(existingPurchase);


    }
}
