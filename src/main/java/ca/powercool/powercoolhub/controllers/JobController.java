package ca.powercool.powercoolhub.controllers;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import ca.powercool.powercoolhub.repositories.JobRepository;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;


@RequestMapping("/jobs")
@Controller
public class JobController {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private CustomerRepository customerRepository;
    
    @GetMapping("/addJob")
    public String addJob() {
        return "jobs/addJob";
    }
    
    @PostMapping("/addJob")
    public String addJobForTheCustomerIntoDataBase(@RequestParam("customerId") int customerIdInfo,
            @RequestParam("dateService") Date serviceDate,
            @RequestParam("note") String note,
            @RequestParam("jobType") String jobType,
            @RequestParam("jobDone") String jobDoneYes,
            HttpServletResponse stat) {
        Job job = new Job();
        job.setCustomerId(customerIdInfo);

        String customerName;
        Customer customer = customerRepository.findById(customerIdInfo).orElse(null);
        if (customer == null) {
            customerName = "Customer not found";
        }
        else{
            customerName = customer.getName();
            if(customerName == null){
                customerName = "Customer unnamed";
            }
        }

        job.setServiceDate(serviceDate);
        job.setNote(note);
        job.setJobType(jobType);
        if (jobDoneYes.toLowerCase().equals("yes")) {
            job.setJobDone(true);
        } else {
            job.setJobDone(false);
        }
        jobRepository.save(job);
        stat.setStatus(HttpServletResponse.SC_OK);
        return "jobs/jobSuccess";
    }

    @GetMapping("/getWeek")
    @ResponseBody
    public List<Job> getJobsForWeek(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                    @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Job>  jobs = jobRepository.findJobsBetweenDates(startDate, endDate);                  
        return jobs;
    }
}