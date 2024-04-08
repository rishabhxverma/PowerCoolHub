package ca.powercool.powercoolhub.controllers;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import ca.powercool.powercoolhub.services.MailService;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller for handling job-related requests.
 */
@Controller
@RequestMapping("/jobs")
public class JobController {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    private final MailService mailService;

    /**
     * Constructor for JobController.
     *
     * @param mailService The mail service to be used by this controller.
     */
    public JobController(MailService mailService) {     this.mailService = mailService;}
    
    @Autowired
    private TechnicianWorkLogService technicianWorkLogService;

    /**
     * Endpoint for adding a job.
     *
     * @return The name of the view to be rendered.
     */
    @GetMapping("/addJob")
    public String addJob() {
        return "jobs/addJob";
    }

    /**
     * Endpoint for adding a job for a customer into the database.
     *
     * @param customerIdInfo The ID of the customer.
     * @param serviceDate The date of the service.
     * @param message The message from the customer.
     * @param note The note for the job.
     * @param jobTypeString The type of the job.
     * @param technicianIds The IDs of the technicians.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/addJob")
    public String addJobForTheCustomerIntoDataBase(
            @RequestParam("customerId") int customerIdInfo,
            @RequestParam("dateService") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate serviceDate,
            @RequestParam(required = false, value = "message") String message,
            @RequestParam("note") String note,
            @RequestParam("jobType") String jobTypeString,
            @RequestParam("technicianIds") List<Integer> technicianIds) {
    
        Optional<Customer> optionalCustomer = customerRepository.findById(customerIdInfo);
        if (!optionalCustomer.isPresent()) {
            return "Customer not found";
        }
    
        Customer customer = optionalCustomer.get();
        String customerName = customer.getName() != null ? customer.getName() : "Customer unnamed";
    
        Job job = new Job();
        job.setCustomerMessage(message);
        job.setCustomerId(customerIdInfo);
        job.setServiceDate(serviceDate);
        job.setNote(note);
        job.setJobType(jobTypeString);
        job.setTechnicianIds(technicianIds);
        job.setCustomerName(customerName);
    
        customer.setState(Customer.CustomerState.UPCOMING);
        customer.setNextService(serviceDate);
        customerRepository.save(customer);
    
        jobRepository.save(job);
    
        List<String> technicians = new ArrayList<>();
        for (Integer i : technicianIds) {
            Optional<User> optionalUser = userRepository.findById((long) i);
            if (!optionalUser.isPresent()) {
                return "Technician not found";
            }
            technicians.add(optionalUser.get().getName());
        }
    
        //mailService.sendBookingConfirmation(customer.getEmail(), customerName, serviceDate, customer.getAddress(), jobTypeString, technicians);
    
        return "redirect:/customers/viewAll?filter=requesting-app";
    }

    /**
     * Endpoint for getting jobs for a customer.
     *
     * @param customerId The ID of the customer.
     * @param model The model to be used by the view.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/getJobs")
    public String getJobsForCustomer(@RequestParam("customerId") int customerId, Model model) {
        List<Job> jobs = jobRepository.findByCustomerId(customerId);
        model.addAttribute("jobs", jobs);
        return "jobs/viewJobs";
    }

    /**
     * Endpoint for getting jobs for a week.
     *
     * @param startDate The start date of the week.
     * @param endDate The end date of the week.
     * @return A list of jobs for the week.
     */
    @GetMapping("/getWeek")
    @ResponseBody
    public List<Job> getJobsForWeek(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Job> jobs = jobRepository.findJobsBetweenDates(startDate, endDate);
        return jobs;
    }


    /**
     * Endpoint for showing a job page.
     *
     * @param id The ID of the job.
     * @param request The HttpServletRequest.
     * @param model The model to be used by the view.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/{id}")
    public String showJobPage(@PathVariable("id") Integer id, HttpServletRequest request, Model model) {
        Optional<Job> existingJob = this.jobRepository.findById(id);

        if (!existingJob.isPresent()) {
            model.addAttribute("error", "Error: Job not found.");
            return "jobs/job";
        }

        Job job = existingJob.get();
        List<User> assignedTechnicians = this.userRepository.findAssignedTechnicians(job.getId());
        List<User> allTechnicians = this.userRepository.findByRole(UserRole.TECHNICIAN);

        User user = (User) request.getSession().getAttribute("user");
        Optional<Customer> customer = this.customerRepository.findById(job.getCustomerId());

        String clockState = this.technicianWorkLogService.getClockState(user);

        model.addAttribute("job", job);
        model.addAttribute("customer", customer.get());
        model.addAttribute("assignedTechnicians", assignedTechnicians);
        model.addAttribute("allTechnicians", allTechnicians);
        model.addAttribute("clockButtonState", clockState);
        model.addAttribute("user", user);

        if (user.getRole().equals(UserRole.MANAGER)) {
            return "users/manager/job";
        } else {
            return "users/technician/job";
        }
    }


    /**
     * Endpoint for completing a job.
     *
     * @param id The ID of the job.
     * @param request The HttpServletRequest.
     * @param address The address where the job was completed.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeJob(@PathVariable("id") Integer id, HttpServletRequest request,
            @RequestBody String address) {
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

    /**
     * Endpoint for updating a job's note.
     *
     * @param id The ID of the job.
     * @param note The new note.
     * @return A ResponseEntity indicating the result of the operation.
     */
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

   /**
     * Endpoint for getting the job count for technicians.
     *
     * @param date The date for which to get the job count.
     * @return A ResponseEntity containing a map of technician IDs to job counts.
     */
    @GetMapping("/getJobsCount")
    public ResponseEntity<Map<Integer,  Integer>> getJobsCountForTechnicians(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<User> technicians = userRepository.findByRole(UserRole.TECHNICIAN);
        Map<Integer, Integer> techJobs = new HashMap<>(); // techId, jobCount
        List<Job> jobsOnDate = jobRepository.findByServiceDate(date);

        for (User tech : technicians) {
            int techId = tech.getId().intValue();
            int jobCount = 0;
            for (Job job : jobsOnDate) {
                if (job.getTechnicianIds().contains(techId)) {
                    jobCount++;
                }
            }
            techJobs.put(techId, jobCount);
        }
        return new ResponseEntity<>(techJobs, HttpStatus.OK);
    }

    /**
     * Endpoint for viewing all jobs.
     *
     * @param model The model to be used by the view.
     * @return The name of the view to be rendered.
     */
    @GetMapping("/viewAllJobs")
    public String getAllandShowAllJobs(@RequestParam(required = false) String filter,
                                       @RequestParam(required = false, value = "customerName", defaultValue = "") String customerName,
                                       Model model) {
        List<Job> jobs;
    
        if (customerName != null && !customerName.isEmpty()) {
            // Search jobs by customer's name
            jobs = jobRepository.findByCustomerNameLikeIgnoreCase(customerName + "%");
        } else {
            // Filter jobs based on the selected filter
            if (filter != null && !filter.isEmpty()) {
                if (filter.equalsIgnoreCase("done")) {
                    jobs = jobRepository.findByJobDoneTrue();
                } else if (filter.equalsIgnoreCase("notdone")) {
                    jobs = jobRepository.findByJobDoneFalse();
                } else {
                    Job.JobType type = mapFilterToType(filter);
                    jobs = jobRepository.findByJobType(type);
                }
            } else {
                // If no filter is provided, return all jobs
                jobs = jobRepository.findAll();
            }
        }
    
        model.addAttribute("jobs", jobs);
        model.addAttribute("selectedFilter", filter);

        Map<Integer, List<User>> jobTechnicians = new HashMap<>();
        for (Job job : jobs) {
            List<User> assignedTechnicians = userRepository.findAssignedTechnicians(job.getId());
            jobTechnicians.put(job.getId(), assignedTechnicians);
        }
        model.addAttribute("jobTechnicians", jobTechnicians);
    
        return "jobs/viewAllJobs";
    }

    private Job.JobType mapFilterToType(String filter) {
        switch (filter.toLowerCase()) {
            case "service":
                return Job.JobType.SERVICE;
            case "install":
                return Job.JobType.INSTALL;
            case "repair":
                return Job.JobType.REPAIR;
            default:
                throw new IllegalArgumentException("Invalid filter: " + filter);
        }
    }



    /**
     * Endpoint for updating a job in the database.
     *
     * @param jobId The ID of the job.
     * @param serviceDate The new service date.
     * @param message The new message.
     * @param note The new note.
     * @param jobTypeString The new job type.
     * @param technicianIds The new technician IDs.
     * @param stat The HttpServletResponse.
     * @return A string indicating the result of the operation.
     */
    @PostMapping("/updateJob")
    public String updateJobInDatabase(@RequestParam("jobId") int jobId,
                                    @RequestParam("dateService") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate serviceDate,
                                    @RequestParam(required = false, value = "message") String message,
                                    @RequestParam("note") String note,
                                    @RequestParam("jobType") String jobTypeString,
                                    @RequestParam("technicianIds") List<Integer> technicianIds,
                                    HttpServletResponse stat) {
        // Fetch the existing job from the database
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) {
            // Handle case where job is not found
            return "redirect:/error";
        }

        // Update job details
        job.setCustomerMessage(message); // Assuming this field can be updated
        job.setServiceDate(serviceDate);
        job.setNote(note);
        job.setJobType(jobTypeString);
        job.setTechnicianIds(technicianIds);

        // Update job in the database
        jobRepository.save(job);

        // Update related customer's state and next service date (assuming this logic is the same as in addJobForTheCustomerIntoDataBase method)

        // Update related customer's state and next service date
        Customer customer = customerRepository.findById(job.getCustomerId()).orElse(null);
        if (customer != null) {
            customer.setState(Customer.CustomerState.UPCOMING);
            customer.setNextService(serviceDate);
            customerRepository.save(customer);
        } else {
            // Handle case where customer is not found
            return NullPointerException.class.getName();
        }

        stat.setStatus(HttpServletResponse.SC_OK);
        //send confirmation email
        //get list of technicians by their ids
        List<String> technicians = new ArrayList<>();
        for (Integer i : technicianIds) {
            technicians.add(userRepository.findById((long) i).get().getName());
        }
        //mailService.sendBookingConfirmation(customer.getEmail(), customer.getName(), serviceDate, customer.getAddress(), jobTypeString, technicians);
        return "redirect:/customers/viewAll";
    }

    // method to delete a job by id
    @DeleteMapping("/delete/{id}")
    public String deleteJob(@PathVariable Integer id) {
        try {
            // Delete the job
            jobRepository.deleteById(id);
    
            // Get the job
            Optional<Job> job = jobRepository.findById(id);
            if (job.isPresent()) {
                // Get the customer
                Optional<Customer> customer = customerRepository.findById(job.get().getCustomerId());
                if (customer.isPresent()) {
                    // Add note to customer that job has been deleted
                    customer.get().setMessage("last job was cancelled");
                    // MAILS CUSTOMER THAT JOB HAS BEEN CANCELLED, uncomment when wanting functionality
                    //mailService.sendCancellationConfirmation(customer.get().getEmail(), customer.get().getName(), job.get().getServiceDate(), customer.get().getAddress(), job.get().getJobType());
                } else {
                    // Handle case where customer is not found
                    System.out.println("Customer not found");
                }
            } else {
                // Handle case where job is not found
                System.out.println("Job not found");
            }
        } catch (Exception e) {
            // Handle any other exceptions
            System.out.println("An error occurred: " + e.getMessage());
        }
    
        return "redirect:/jobs/viewAllJobs";
    }
}