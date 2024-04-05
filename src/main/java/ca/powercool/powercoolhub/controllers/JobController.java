package ca.powercool.powercoolhub.controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import ca.powercool.powercoolhub.repositories.JobRepository;
import ca.powercool.powercoolhub.repositories.UserRepository;
import ca.powercool.powercoolhub.services.TechnicianWorkLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    private TechnicianWorkLogService technicianWorkLogService;

    @GetMapping("/addJob")
    public String addJob() {
        return "jobs/addJob";
    }

    @PostMapping("/addJob")
    public String addJobForTheCustomerIntoDataBase(@RequestParam("customerId") int customerIdInfo,
            @RequestParam("dateService") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate serviceDate,
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

    @GetMapping("/{id}")
    public String showJobPage(@PathVariable("id") Integer id, HttpServletRequest request, Model model) {
        Optional<Job> existingJob = this.jobRepository.findById(id);

        if (!existingJob.isPresent()) {
            model.addAttribute("error", "Error: Job not found.");
            return "jobs/job";
        }

        Job job = existingJob.get();
        List<User> assignedTechnicians = this.userRepository.findAssignedTechnicians(job.getId());
        
        User user = (User) request.getSession().getAttribute("user");
        Optional<Customer> customer = this.customerRepository.findById(job.getCustomerId());
        
        String clockState = this.technicianWorkLogService.getClockState(user);
        
        model.addAttribute("job", job);
        model.addAttribute("customer", customer.get());
        model.addAttribute("assignedTechnicians", assignedTechnicians);
        model.addAttribute("clockButtonState", clockState);
        model.addAttribute("user", user);

        if (user.getRole().equals(UserRole.MANAGER)) {
            return "users/manager/job";
        } else {
            return "users/technician/job";
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeJob(@PathVariable("id") Integer id, HttpServletRequest request, @RequestBody String address) {
        Optional<Job> existingJob = this.jobRepository.findById(id);

        if (!existingJob.isPresent()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
        }

        User user = (User) request.getSession().getAttribute("user");
        LocalDateTime now = LocalDateTime.now();

        Job completedJob = existingJob.get();
        completedJob.setJobDone(true);
        completedJob.setJobDoneTime(now);
        this.jobRepository.save(completedJob);

        // Log the job completion
        TechnicianWorkLog jobCompletion = new TechnicianWorkLog();
        jobCompletion.setAction("job_completed");
        jobCompletion.setLocation(address);

        this.technicianWorkLogService.saveWorkLog(user, jobCompletion);

        return new ResponseEntity<>(completedJob, HttpStatus.OK);
    }

    @PostMapping("/{id}/note/update")
    public ResponseEntity<?> updateNote(@PathVariable("id") Integer id, @RequestBody String note) {
        Optional<Job> existingJob = this.jobRepository.findById(id);
        if (!existingJob.isPresent()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
        }

        Job job = existingJob.get();
        job.setNote(note);

        this.jobRepository.save(job);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

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