package ca.powercool.powercoolhub.services;

import ca.powercool.powercoolhub.controllers.CustomerController;
import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class CustomerControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateNewCustomer() {
        // Arrange
        when(customerRepository.findByEmail(anyString())).thenReturn(null);
        when(customerRepository.existsByAddress(anyString())).thenReturn(false);

        Customer newCustomer = new Customer();
        newCustomer.setEmail("newcustomer@example.com");
        newCustomer.setAddress("123 New St");

        // Act
        String viewName = customerController.createCustomer(newCustomer, model);

        // Assert
        verify(customerRepository, times(1)).save(any(Customer.class));
        assertEquals("redirect:/customers/viewAll", viewName);
    }
}
