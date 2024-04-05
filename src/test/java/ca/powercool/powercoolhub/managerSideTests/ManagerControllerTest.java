package ca.powercool.powercoolhub.managerSideTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import ca.powercool.powercoolhub.controllers.ManagerController;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import ca.powercool.powercoolhub.models.Customer;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ManagerControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private ManagerController managerController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetManagerDashboard() {
        String view = managerController.getManagerDashboard(null, null);
        assertNotNull(view);
        assertEquals("users/manager/dashboard", view);
    }

    @Test
    public void testGetClientCounts() {
        when(customerRepository.countByState(Customer.CustomerState.REQUESTING_APPOINTMENT)).thenReturn(10);
        when(customerRepository.countByState(Customer.CustomerState.UPCOMING)).thenReturn(5);
        when(customerRepository.countByState(Customer.CustomerState.ARCHIVED)).thenReturn(2);

        ResponseEntity<Map<String, Integer>> response = managerController.getClientCounts();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Integer> counts = response.getBody();
        assertNotNull(counts);
        assertEquals(3, counts.size());
        assertEquals(10, counts.get("unscheduledClientsCount"));
        assertEquals(5, counts.get("upcomingClientsCount"));
        assertEquals(2, counts.get("archivedClientsCount"));
    }
}
