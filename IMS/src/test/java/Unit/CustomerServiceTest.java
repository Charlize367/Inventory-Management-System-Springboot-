package Unit;

import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Response.CustomerResponse;
import org.example.Entities.Customers;
import org.example.Mapper.CustomerMapper;
import org.example.Repository.CustomersRepository;
import org.example.Services.CustomersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    CustomersRepository customersRepository;


    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomersService customersService;


    @Test
    void testGetCustomerById() {
        Customers customer = new Customers(1L, "John Doe", "johndoe@gmail.com", "0987654324", "New York City", Customers.CustomerType.RETAIL);
        Mockito.when(customersRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponse expectedResponse = new CustomerResponse();
        expectedResponse.setCustomerName("John Doe");
        Mockito.when(customerMapper.toResponse(customer)).thenReturn(expectedResponse);

        CustomerResponse result = customersService.getCustomerById(1L);

        Assertions.assertEquals("John Doe", result.getCustomerName());
    }

    @Test
    void testAddCustomer() {

        CustomerRequest request = new CustomerRequest("John Doe", "johndoe@gmail.com", "0987654324", "New York City", "RETAIL");

        Customers savedCustomer = new Customers(1L, "John Doe", "johndoe@gmail.com", "0987654324", "New York City", Customers.CustomerType.RETAIL);
        Mockito.when(customersRepository.save(Mockito.any(Customers.class))).thenReturn(savedCustomer);


        CustomerResponse expectedResponse = new CustomerResponse();
        expectedResponse.setCustomerName("John Doe");
        Mockito.when(customerMapper.toResponse(savedCustomer)).thenReturn(expectedResponse);


        CustomerResponse result = customersService.addCustomer(request);

        Assertions.assertEquals("John Doe", result.getCustomerName());


        Mockito.verify(customersRepository, Mockito.times(1)).save(Mockito.any(Customers.class));
    }

    @Test
    void testUpdateCustomer() {

        Long customerId = 1L;
        Customers existingCustomer = new Customers(customerId, "John Doe", "johndoe@gmail.com", "0987654324", "New York City", Customers.CustomerType.RETAIL);
        Mockito.when(customersRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        Customers updatedCustomer = new Customers(customerId, "John Doe", "johndoe@gmail.com", "0987654324", "Los Angeles, California", Customers.CustomerType.RETAIL);
        Mockito.when(customersRepository.save(Mockito.any(Customers.class))).thenReturn(updatedCustomer);


        CustomerRequest request = new CustomerRequest("John Doe", "johndoe@gmail.com", "0987654324", "Los Angeles, California", "RETAIL");

        CustomerResponse expectedResponse = new CustomerResponse();
        expectedResponse.setCustomerId(customerId);
        expectedResponse.setCustomerName("John Doe");
        Mockito.when(customerMapper.toResponse(updatedCustomer)).thenReturn(expectedResponse);

        CustomerResponse result = customersService.updateCustomerDetails(customerId, request);

        Assertions.assertEquals(customerId, result.getCustomerId());
        Assertions.assertEquals("John Doe", result.getCustomerName());

    }

    @Test
    void testDeleteCustomer() {
        Long customerId = 1L;
        Customers existingCustomer = new Customers(customerId, "John Doe", "johndoe@gmail.com", "0987654324", "New York City", Customers.CustomerType.RETAIL);
        Mockito.when(customersRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        customersService.deleteCustomer(customerId);

        Mockito.verify(customersRepository).findById(customerId);

        Mockito.verify(customersRepository).delete(existingCustomer);


    }
}
