package ca.powercool.powercoolhub.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;
import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.models.Customer.CustomerState;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import ca.powercool.powercoolhub.repositories.JobRepository;
import ca.powercool.powercoolhub.repositories.UserRepository;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    UserRepository userRepository;

    // View all customers
    @GetMapping("/viewAll")
    public String viewAllCustomers(@RequestParam(required = false) String filter, Model model) {
        List<Customer> customers = customerRepository.findAll();
        if (filter != null) {
            Customer.CustomerState state = mapFilterToState(filter);
            customers = customerRepository.findByState(state);
        } else {
            customers = customerRepository.findAll();
        }
        model.addAttribute("customers", customers);

        List<User> techs = userRepository.findByRole(UserRole.TECHNICIAN);
        model.addAttribute("techs", techs);

        return "customers/viewAll";
    }

    // Filter customers by state
    @GetMapping("/filterJson")
    @ResponseBody
    public ResponseEntity<List<Customer>> filterCustomersJson(@RequestParam("filter") String selectedFilter) {
        if (selectedFilter.equals("all")) {
            return ResponseEntity.ok(customerRepository.findAll());
        } else if (selectedFilter.equals("upcoming")) {
            return ResponseEntity.ok(customerRepository.findByState(mapFilterToState("upcoming")));
        } else if (selectedFilter.equals("requesting-app")) {
            return ResponseEntity.ok(customerRepository.findByState(mapFilterToState("requesting-app")));
        } else if (selectedFilter.equals("archived")) {
            return ResponseEntity.ok(customerRepository.findByState(mapFilterToState("archived")));
        }

        Customer.CustomerState state = mapFilterToState(selectedFilter);
        List<Customer> customers = customerRepository.findByState(state);
        return ResponseEntity.ok(customers);
    }

    private Customer.CustomerState mapFilterToState(String filter) {
        switch (filter) {
            case "upcoming":
                return Customer.CustomerState.UPCOMING;
            case "archived":
                return Customer.CustomerState.ARCHIVED;
            case "requesting-app":
                return Customer.CustomerState.REQUESTING_APPOINTMENT;
            default:
                return null;
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditCustomerForm(@PathVariable Integer id, Model model) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + id));
        model.addAttribute("customer", customer);
        return "customers/editCustomer";
    }

    @GetMapping("/addCustomer")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/addCustomer";
    }

    @GetMapping("/searchCustomer")
    public String searchCustomerByName(@RequestParam(value = "customerName", defaultValue = "") String customerName,
            Model model) {
        customerName = customerName.trim();
        if (customerName == "") {
            return "redirect:/customers/viewAll";
        }

        List<Customer> customers = customerRepository.findByNameLikeIgnoreCase(customerName + "%");
        model.addAttribute("customers", customers);
        model.addAttribute("searchTerm", customerName);
        return "customers/searchResults";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable Integer id, @ModelAttribute("customer") Customer customerDetails,
            RedirectAttributes redirectAttributes) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (!customerOptional.isPresent()) {
            return "customers/editedCustomer";
        }

        customerDetails.setId(id);
        customerRepository.save(customerDetails);

        List<Job> customersJobs = jobRepository.findByCustomerId(id); // Assuming you have this method in your
                                                                      // repository
        for (Job job : customersJobs) {
            job.setCustomerName(customerDetails.getName());
            jobRepository.save(job);
        }

        customerRepository.save(customerDetails);
        redirectAttributes.addFlashAttribute("success", "Customer updated successfully!");
        return "customers/editedCustomer";
    }

    @PostMapping("/")
    public String createCustomer(@ModelAttribute Customer customer, Model model) {
        Customer existingCustomer = customerRepository.findByEmail(customer.getEmail());
        boolean addressExists = customerRepository.existsByAddress(customer.getAddress()); 
    
        if (existingCustomer != null && addressExists) {
            existingCustomer.setState(CustomerState.REQUESTING_APPOINTMENT);
            existingCustomer.setNotes(customer.getNotes());
        } else {
            existingCustomer = customer;
        }
        customerRepository.save(existingCustomer);
    
        return "redirect:/customers/viewAll"; 
    }
    
    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isPresent()) {
            customerRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Customer not found.");
        }
        return "redirect:/customers/viewAll";
    }

    @GetMapping("/calendar")
    public String getCalendar() {
        return "customers/calendar";
    }

    @GetMapping("/getCustomerNameFromId")
    @ResponseBody
    public String getCustomerNameFromId(@RequestParam("customerId") int customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            return customer.get().getName();
        } else {
            return "No customer exists";
        }
    }

    // all mappings for customer queries
}
