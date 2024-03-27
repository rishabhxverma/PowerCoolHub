package ca.powercool.powercoolhub.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("")
    public String getManagerDashboard(HttpServletRequest request, HttpServletResponse response) {
        return "users/manager/dashboard";
    }

    @GetMapping("/counts")
    public ResponseEntity<Map<String, Integer>> getClientCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("unscheduledClientsCount", customerRepository.countByState(Customer.CustomerState.REQUESTING_APPOINTMENT));
        counts.put("upcomingClientsCount", customerRepository.countByState(Customer.CustomerState.UPCOMING));
        counts.put("archivedClientsCount", customerRepository.countByState(Customer.CustomerState.ARCHIVED));
        return ResponseEntity.ok(counts);
    }
}