package ca.powercool.powercoolhub.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;
import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import ca.powercool.powercoolhub.repositories.JobRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private JobRepository jobRepository;

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

        List<Job> customersJobs = jobRepository.findByCustomerId(id);
        for (Job job : customersJobs) {
            job.setCustomerName(customerDetails.getName());
            jobRepository.save(job);
        }

        redirectAttributes.addFlashAttribute("success", "Customer updated successfully!");
        return "customers/editedCustomer";
    }

    @PostMapping("/")
    public String createCustomer(@ModelAttribute Customer customer, Model model) {
        customerRepository.save(customer);
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
    
}
