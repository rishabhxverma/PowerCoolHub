package ca.powercool.powercoolhub.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;

import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.repositories.JobRepository;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JobController {
    @Autowired
    private JobRepository jobRepository;

    @PostMapping("/job")
    public String addJobForTheCustomerIntoDataBase(@RequestParam("customerId") int customerIdInfo,
            @RequestParam("dateService") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date serviceDate,
            @RequestParam("note") String note,
            @RequestParam("jobType") String jobType,
            @RequestParam("jobDone") String jobDoneYes,
            HttpServletResponse stat) {
        Job job = new Job();
        job.setCustomerId(customerIdInfo);
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
        return "jobSuccess";
    }
}
