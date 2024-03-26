package ca.powercool.powercoolhub.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.junit.jupiter.api.Test;
import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.repositories.JobRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
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
        job.setCustomerId(1); // Set customer ID
        job.setServiceDate(Date.valueOf("2024-03-24")); // Set service date
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
}
