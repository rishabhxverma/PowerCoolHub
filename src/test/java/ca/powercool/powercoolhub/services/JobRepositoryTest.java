package ca.powercool.powercoolhub.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.repositories.JobRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@SpringJUnitConfig
@SpringBootTest
public class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Test
    public void testSaveJobWithTechnicianIds() {
        // Create a new Job instance
        Job job = new Job();
        LocalDate serviceDate = LocalDate.of(2024, 3, 24);

        job.setCustomerId(1); // Set customer ID
        job.setServiceDate(serviceDate); // Set service date
        job.setNote("Test job"); // Set note
        job.setJobType("SERVICE"); // Set job type (assuming string representation)
        job.setJobDone(false); // Set job done status

        // Set technician IDs
        List<Integer> technicianIds = Arrays.asList(1, 2, 3); // Example technician IDs
        job.setTechnicianIds(technicianIds);

        // Save the job entity
        Job savedJob = jobRepository.save(job);

        // Assert that the job is saved successfully
        assertNotNull(savedJob.getId()); // Ensure the job ID is assigned after save
    }

    @Test
    public void testJobCreation() {
        Job job = new Job();
        job.setId(1);
        job.setCustomerId(1);
        LocalDate serviceDate = LocalDate.of(2024, 3, 26);

        job.setServiceDate(serviceDate);
        job.setNote("Test note");
        job.setJobType("SERVICE");
        job.setTechnicianIds(Arrays.asList(1, 2, 3));
        job.setJobDone(false);
        job.setCustomerName("Test Customer");
        job.setPaymentReceived(false);

        assertEquals(Integer.valueOf(1), job.getId());
        assertEquals(1, job.getCustomerId());
        assertNotNull(job.getServiceDate());
        assertEquals("Test note", job.getNote());
        assertEquals("service", job.getJobType());
        assertEquals(Arrays.asList(1, 2, 3), job.getTechnicianIds());
        assertFalse(job.isJobDone());
        assertEquals("Test Customer", job.getCustomerName());
        assertFalse(job.isPaymentReceived());
    }

    @Test
    public void testJobUpdate() {
        Job job = new Job();
        job.setId(1);
        job.setJobType("SERVICE");
        job.setJobDone(false);

        // Save the initial state
        Job savedJob = jobRepository.save(job);

        // Update the job
        savedJob.setJobType("REPAIR");
        savedJob.setJobDone(true);
        Job updatedJob = jobRepository.save(savedJob);

        // Assert that the job is updated correctly
        assertEquals("repair", updatedJob.getJobType());
        assertTrue(updatedJob.isJobDone());
    }

    @Test
    public void testFindJobByCustomerId() {
        Job job = new Job();
        job.setId(1);
        job.setCustomerId(1);
        jobRepository.save(job);

        List<Job> jobs = jobRepository.findByCustomerId(1);
        assertFalse(jobs.isEmpty());
    }

    @Test
    public void testDeleteJob() {
        Job job = new Job();
        job.setId(1);
        jobRepository.save(job);

        jobRepository.delete(job);
        Optional<Job> deletedJob = jobRepository.findById(1);

        assertFalse(deletedJob.isPresent());
    }

    @Test
    public void testFindAllJobs() {
        Job job1 = new Job();
        job1.setId(1);
        jobRepository.save(job1);

        Job job2 = new Job();
        job2.setId(2);
        jobRepository.save(job2);

        List<Job> jobs = jobRepository.findAll();
        assertEquals(37, jobs.size());
    }

    @Test
    public void testFindJobByTechnicianId() {
        Job job = new Job();
        job.setId(1);
        job.setTechnicianIds(Arrays.asList(1, 2, 3));
        jobRepository.save(job);

        List<Job> jobs = jobRepository.findByTechnicianId(1);
        assertFalse(jobs.isEmpty());
    }

}
