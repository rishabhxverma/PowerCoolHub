package ca.powercool.powercoolhub.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;
import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.repositories.CustomerRepository;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/viewAll")
    public String viewAllCustomers(Model model) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        return "customers/viewAll";
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
    public String searchCustomerByName(@RequestParam(value = "customerName", defaultValue = "") String customerName, Model model) {
        customerName = customerName.trim(); 
        if (customerName == "") {
            return "redirect:/customers/viewAll"; 
        }
    
        List<Customer> customers = customerRepository.findByNameLike(customerName + "%");
        model.addAttribute("customers", customers);
        model.addAttribute("searchTerm", customerName); 
        return "customers/searchResults"; 
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable Integer id, @ModelAttribute("customer") Customer customerDetails, RedirectAttributes redirectAttributes) {
    Optional<Customer> customerOptional = customerRepository.findById(id);
    if (!customerOptional.isPresent()) {
        return "customers/editedCustomer";
    }

    customerDetails.setId(id);
    customerRepository.save(customerDetails);
    redirectAttributes.addFlashAttribute("success", "Customer updated successfully!");
    return "customers/editedCustomer";
}

    @PostMapping("/")
    public String createCustomer(@ModelAttribute Customer customer, Model model) {
        Customer createdCustomer = customerRepository.save(customer);
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
}

