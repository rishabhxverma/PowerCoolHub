
package ca.powercool.powercoolhub.managerSideTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import ca.powercool.powercoolhub.controllers.CustomerController;
import ca.powercool.powercoolhub.controllers.JobController;
import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.models.Customer.CustomerState;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import ca.powercool.powercoolhub.repositories.JobRepository;

public class ViewAllCustomersTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private JobController jobController;

    @InjectMocks
    private CustomerController customerController;

    Job job;
    Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize Job
        job = new Job();
        job.setCustomerName("Matt Stock");
        job.setCustomerId(1);
        job.setCustomerMessage("Hello Customer");
        job.setNote("Customer wants a brand new AC.");
        job.setServiceDate(LocalDate.now());
        job.setJobDone(false);

        // Initialize Customer
        customer = new Customer();
        customer.setId(1);
        customer.setAddress("customer@gmail.com");
        customer.setState(CustomerState.REQUESTING_APPOINTMENT);
        customer.setMessage("It's gonna be a hot summer this year. I need a new AC.");
        customer.setName("Matt Stock");
        customer.setPhoneNumber("778-082-092-1123");

        when(jobRepository.findAll()).thenReturn(Collections.singletonList(job));
        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));
        when(customerRepository.findByState(CustomerState.REQUESTING_APPOINTMENT))
                .thenReturn(Collections.singletonList(customer));
    }

    @Test
    void testViewAllJobsAndCustomers() {
        List<Job> allJobs = jobRepository.findAll();
        assertEquals(1, allJobs.size());

        List<Customer> customers = customerRepository.findAll();
        assertEquals(1, customers.size());

        List<Customer> requestingCustomers = customerRepository.findByState(CustomerState.REQUESTING_APPOINTMENT);
        assertEquals(1, requestingCustomers.size());
    }
}
