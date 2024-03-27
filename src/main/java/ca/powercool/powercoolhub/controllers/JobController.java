package ca.powercool.powercoolhub.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import ca.powercool.powercoolhub.repositories.JobRepository;
import ca.powercool.powercoolhub.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/jobs")
@Controller
public class JobController {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/addJob")
    public String addJob() {
        return "jobs/addJob";
    }

    @PostMapping("/addJob")
    public String addJobForTheCustomerIntoDataBase(@RequestParam("customerId") int customerIdInfo,
            @RequestParam("dateService") @DateTimeFormat(pattern = "yyyy-MM-dd") Date serviceDate,
            @RequestParam("note") String note,
            @RequestParam("jobType") String jobTypeString,
            @RequestParam("technicianIds") List<Integer> technicianIds,
            HttpServletResponse stat) {
        Job job = new Job();
        job.setCustomerId(customerIdInfo);
        job.setServiceDate(serviceDate);
        job.setNote(note);
        job.setJobType(jobTypeString);
        job.setTechnicianIds(technicianIds);
        

        Customer customer = customerRepository.findById(customerIdInfo).orElse(null);

        String customerName;
        if (customer == null) {
            customerName = "Customer not found";
        }
        else{
            customerName = customer.getName();
            if(customerName == null){
                customerName = "Customer unnamed";
            }
        }
        job.setCustomerName(customerName);
        customer.setState(Customer.CustomerState.UPCOMING);
        customer.setNextService(serviceDate);
        jobRepository.save(job);
        stat.setStatus(HttpServletResponse.SC_OK);

        return "redirect:/customers/viewAll";
    }

    //get jobs from customer id
    @GetMapping("/getJobs")
    public String getJobsForCustomer(@RequestParam("customerId") int customerId, Model model) {
        List<Job> jobs = jobRepository.findByCustomerId(customerId);
        model.addAttribute("jobs", jobs);
        return "jobs/viewJobs";
    }

    @GetMapping("/getWeek")
    @ResponseBody
    public List<Job> getJobsForWeek(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Job> jobs = jobRepository.findJobsBetweenDates(startDate, endDate);
        return jobs;
    }

    // @GetMapping("/{id}")
    // public String showJobPage(@PathVariable("id") Integer id, Model model) {
    //     Optional<Job> job = this.jobRepository.findById(id);

    //     if (!job.isPresent()) {
    //         model.addAttribute("error", "Error: Job not found.");
    //         return "jobs/job";
    //     }

    //     model.addAttribute("job", job);

    //     return "jobs/job";
    // }

    @GetMapping("/getJobsCount")
    public ResponseEntity<Map<Integer,  Integer>> getJobsCountForTechnicians(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<User> technicians = userRepository.findByRole(UserRole.TECHNICIAN);
        Map<Integer,  Integer> techJobs = new HashMap<>(); // techId, jobCount
        List<Job> jobsOnDate = jobRepository.findByServiceDate(date);

        for (User tech : technicians) {
            int techId = tech.getId().intValue();
            int jobCount = 0;
            for(Job job : jobsOnDate){
                if(job.getTechnicianIds().contains(techId)){
                    jobCount++;
                }
            }
            techJobs.put(techId, jobCount);
        }
        return new ResponseEntity<>(techJobs, HttpStatus.OK);
    }
}