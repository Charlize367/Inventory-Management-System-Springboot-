package Unit;


import org.example.DTO.Request.SaleItemRequest;
import org.example.DTO.Request.SalesRequest;
import org.example.DTO.Request.VariationOptionRequest;
import org.example.DTO.Response.PurchaseResponse;
import org.example.DTO.Response.SaleItemResponse;
import org.example.DTO.Response.SalesResponse;
import org.example.Entities.*;
import org.example.Mapper.SalesMapper;
import org.example.Repository.*;
import org.example.Services.SalesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class SalesServiceTest {

    @Mock
    private SalesMapper salesMapper;

    @Mock
    SalesRepository salesRepository;

    @Mock
    CustomersRepository customersRepository;

    @Mock
    UsersRepository usersRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    SaleItemsRepository saleItemsRepository;

    @Mock
    StockMovementsRepository stockMovementsRepository;

    @InjectMocks
    private SalesService salesService;

    @Test
    void testGetSalesById() {

        Customers testCustomer = new Customers();
        testCustomer.setCustomerId(1L);
        testCustomer.setCustomerName("Jane Doe");

        Users testStaff = new Users();
        testStaff.setUserId(2L);
        testStaff.setUsername("John Doe");
        testStaff.setRole(Users.Role.STAFF);

        Sales sale = new Sales(1L, testCustomer, testStaff, 100.0, Sales.SalesPaymentStatus.PAID, LocalDate.now());
        Mockito.when(salesRepository.findById(1L)).thenReturn(Optional.of(sale));

        SalesResponse expectedResponse = new SalesResponse();
        expectedResponse.setSalesId(1L);
        Mockito.when(salesMapper.toResponse(sale)).thenReturn(expectedResponse);

        SalesResponse result = salesService.getSalesById(1L);

        Assertions.assertEquals(1, result.getSalesId());
    }

    @Test
    void testAddSale() {

        Customers testCustomer = new Customers();
        testCustomer.setCustomerId(1L);
        testCustomer.setCustomerName("Jane Doe");

        Suppliers testSupplier = new Suppliers();
        testSupplier.setSupplierId(1L);
        testSupplier.setSupplierName("ABC Supplier");

        Users testStaff = new Users();
        testStaff.setUserId(2L);
        testStaff.setUsername("John Doe");
        testStaff.setRole(Users.Role.STAFF);

        Set<Categories> testCategory = new HashSet<>();
        Set<Variation> testVariation = new HashSet<>();
        Brand testBrand = new Brand();
        List<VariationOptionRequest> testOptions = new ArrayList<>();


        Products product1 = new Products(1L, "Test Product 1", "Test Description 1", 10.0, 10.0, 50, "test-image.jpg", testCategory, testVariation, true, "CODE", testSupplier, testBrand);
        Products product2 = new Products(2L, "Test Product 2", "Test Description 2", 12.0, 12.0, 50, "test-image2.jpg", testCategory, testVariation, true, "CODE-2", testSupplier, testBrand);

        List<SaleItemRequest> saleItemRequests = List.of(
                new SaleItemRequest(1L, 10, testOptions),
                new SaleItemRequest(2L, 5, testOptions)
        );

        SalesRequest request = new SalesRequest(
                1L,
                2L,
                saleItemRequests
        );


        Mockito.when(customersRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        Mockito.when(usersRepository.findById(2L)).thenReturn(Optional.of(testStaff));
        Mockito.when(productRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(product1, product2));

        Mockito.when(salesRepository.save(Mockito.any(Sales.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(saleItemsRepository.save(Mockito.any(SaleItems.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(productRepository.save(Mockito.any(Products.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(stockMovementsRepository.save(Mockito.any(StockMovements.class))).thenAnswer(invocation -> invocation.getArgument(0));


        SalesResponse mockResponse = new SalesResponse();
        mockResponse.setSalesPaymentStatus("PAID");
        mockResponse.setSaleDate(LocalDate.now());
        mockResponse.setSalesItem(List.of(new SaleItemResponse()));
        Mockito.when(salesMapper.toResponse(Mockito.any(Sales.class)))
                .thenReturn(mockResponse);


        SalesResponse response = salesService.addSale(request);


        Assertions.assertNotNull(response);
        Assertions.assertEquals("PAID", response.getSalesPaymentStatus());


        Mockito.verify(customersRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(usersRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(productRepository, Mockito.times(1)).findAllById(List.of(1L, 2L));
        Mockito.verify(saleItemsRepository, Mockito.times(2)).save(Mockito.any(SaleItems.class));
        Mockito.verify(stockMovementsRepository, Mockito.times(2)).save(Mockito.any(StockMovements.class));
    }


    @Test
    void testDeleteSale() {

        Customers testCustomer = new Customers();
        testCustomer.setCustomerId(1L);
        testCustomer.setCustomerName("Jane Doe");

        Users testStaff = new Users();
        testStaff.setUserId(1L);
        testStaff.setUsername("John Doe");
        testStaff.setRole(Users.Role.STAFF);

        Long salesId = 1L;
        Sales existingSale = new Sales(1L, testCustomer, testStaff, 100.0, Sales.SalesPaymentStatus.PAID, LocalDate.now());
        Mockito.when(salesRepository.findById(salesId)).thenReturn(Optional.of(existingSale));

        salesService.deleteSale(salesId);

        Mockito.verify(salesRepository).findById(salesId);

        Mockito.verify(salesRepository).delete(existingSale);


    }
}
