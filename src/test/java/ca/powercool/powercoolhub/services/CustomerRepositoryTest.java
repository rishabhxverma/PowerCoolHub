package ca.powercool.powercoolhub.services;

import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @Before
    public void setUp() {
        customer = new Customer("Test", "123 Test St", "1234567890", "test@test.com", Customer.CustomerState.UPCOMING, "Test notes");
        customerRepository.save(customer);
    }

    @Test
    public void testFindByNameLikeIgnoreCase() {
        List<Customer> customers = customerRepository.findByNameLikeIgnoreCase("test");
        assertFalse(customers.isEmpty());
        assertEquals("Test", customers.get(0).getName());
    }

    @Test
    public void testFindById() {
        Integer id = customer.getId();
        assertTrue(customerRepository.findById(id).isPresent());
    }

    @Test
    public void testFindByState() {
        List<Customer> customers = customerRepository.findByState(Customer.CustomerState.UPCOMING);
        assertFalse(customers.isEmpty());
        assertEquals(Customer.CustomerState.UPCOMING, customers.get(0).getState());
    }

    @Test
    public void testCountByState() {
        int count = customerRepository.countByState(Customer.CustomerState.UPCOMING);
        assertEquals(1, count);
    }

    @Test
    public void testCustomerStateChange() {
        customer.setState(Customer.CustomerState.ARCHIVED);
        customerRepository.save(customer);
        assertEquals(Customer.CustomerState.ARCHIVED, customerRepository.findById(customer.getId()).get().getState());
    }
}