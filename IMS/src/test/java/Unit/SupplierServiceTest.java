package Unit;


import org.example.DTO.Request.SupplierRequest;
import org.example.DTO.Response.PurchaseResponse;
import org.example.DTO.Response.SalesResponse;
import org.example.DTO.Response.SupplierResponse;
import org.example.Entities.Suppliers;
import org.example.Mapper.SuppliersMapper;
import org.example.Repository.SuppliersRepository;
import org.example.Services.SuppliersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceTest {
    @Mock
    SuppliersRepository suppliersRepository;

    @Mock
    SuppliersMapper suppliersMapper;

    @InjectMocks
    private SuppliersService suppliersService;


    @Test
    void testGetSupplierById() {
        Suppliers suppliers = new Suppliers(1L, "John Doe", "John Doe","johndoe@gmail.com", "0987654324", "New York City");
        Mockito.when(suppliersRepository.findById(1L)).thenReturn(Optional.of(suppliers));

        SupplierResponse expectedResponse = new SupplierResponse();
        expectedResponse.setSupplierId(1L);
        expectedResponse.setSupplierName("John Doe");
        Mockito.when(suppliersMapper.toResponse(suppliers)).thenReturn(expectedResponse);

        SupplierResponse result = suppliersService.getSupplierById(1L);

        Assertions.assertEquals("John Doe", result.getSupplierName());
    }

    @Test
    void testAddSupplier() {

        SupplierRequest request = new SupplierRequest("John Doe", "John Doe","johndoe@gmail.com", "0987654324", "New York City");

        Suppliers savedSupplier = new Suppliers(1L, "John Doe", "John Doe","johndoe@gmail.com", "0987654324", "New York City");
        Mockito.when(suppliersRepository.save(Mockito.any(Suppliers.class))).thenReturn(savedSupplier);

        SupplierResponse expectedResponse = new SupplierResponse();
        expectedResponse.setSupplierId(1L);
        expectedResponse.setSupplierName("John Doe");
        Mockito.when(suppliersMapper.toResponse(savedSupplier)).thenReturn(expectedResponse);

        SupplierResponse result = suppliersService.addSupplier(request);

        Assertions.assertEquals(1L, result.getSupplierId());
        Assertions.assertEquals("John Doe", result.getSupplierName());


        Mockito.verify(suppliersRepository, Mockito.times(1)).save(Mockito.any(Suppliers.class));
    }

    @Test
    void testUpdateSupplier() {

        Long supplierId = 1L;
        Suppliers existingSuppliers = new Suppliers(supplierId, "John Doe", "John Doe","johndoe@gmail.com", "0987654324", "New York City");
        Mockito.when(suppliersRepository.findById(supplierId)).thenReturn(Optional.of(existingSuppliers));

        Suppliers updatedSupplier = new Suppliers(supplierId, "John Doe", "John Doe","johndoe@gmail.com", "0987654324", "Los Angeles, California");
        Mockito.when(suppliersRepository.save(Mockito.any(Suppliers.class))).thenReturn(updatedSupplier);


        SupplierRequest request = new SupplierRequest("John Doe", "John Doe","johndoe@gmail.com", "0987654324", "Los Angeles, California");

        SupplierResponse expectedResponse = new SupplierResponse();
        expectedResponse.setSupplierId(1L);
        expectedResponse.setSupplierName("John Doe");
        expectedResponse.setSupplierAddress("Los Angeles, California");
        Mockito.when(suppliersMapper.toResponse(updatedSupplier)).thenReturn(expectedResponse);

        SupplierResponse result = suppliersService.updateSupplierDetails(supplierId, request);

        Assertions.assertEquals(supplierId, result.getSupplierId());
        Assertions.assertEquals("John Doe", result.getSupplierName());
        Assertions.assertEquals("Los Angeles, California", result.getSupplierAddress());
    }

    @Test
    void testDeleteSupplier() {
        Long supplierId = 1L;
        Suppliers existingSupplier = new Suppliers(supplierId, "John Doe", "John Doe","johndoe@gmail.com", "0987654324", "New York City");
        Mockito.when(suppliersRepository.findById(supplierId)).thenReturn(Optional.of(existingSupplier));

        suppliersService.deleteSupplier(supplierId);

        Mockito.verify(suppliersRepository).findById(supplierId);

        Mockito.verify(suppliersRepository).delete(existingSupplier);


    }
}
