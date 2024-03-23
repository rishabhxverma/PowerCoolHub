package ca.powercool.powercoolhub.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import ca.powercool.powercoolhub.models.Customer.CustomerState;

@Controller
@RequestMapping("/manager/dashboard")
public class DashboardController {
    @Autowired
    private CustomerRepository customerRepository;
    
    @GetMapping
    public String getDashboard(Model model) {
        return "dashboard";
    }

    @GetMapping("/counts")
    @ResponseBody
    public Map<String, Integer> getClientCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("unscheduledClientsCount", customerRepository.countByState(Customer.CustomerState.REQUESTING_APPOINTMENT));
        counts.put("upcomingClientsCount", customerRepository.countByState(Customer.CustomerState.UPCOMING));
        counts.put("archivedClientsCount", customerRepository.countByState(Customer.CustomerState.ARCHIVED));
        return counts;
    }
}