package ca.powercool.powercoolhub.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import ca.powercool.powercoolhub.repositories.JobRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Job> getUpcomingJobs(User user, String startDate, String endDate) {
        LocalDate localStartDate = LocalDate.parse(startDate);
        LocalDate localEndDate = LocalDate.parse(endDate);
        List<Job> upcomingJobs = this.jobRepository.findIncompleteJobsByTechnicianIdBetween(user.getId(), localStartDate, localEndDate);
        return upcomingJobs;
    }

    @Override
    public String getLatestCompletedJobAddress(Long techId) {
        Job latest = this.jobRepository.findLatestCompleteJobByTechnicianId(techId);
        if (latest != null) {
        return customerRepository.findById(latest.getCustomerId())
            .map(Customer::getAddress)
            .orElseThrow(() -> new EntityNotFoundException("No customer found for ID " + latest.getCustomerId()));
    } else {
        throw new EntityNotFoundException("No completed job found for technician ID " + techId);
    }
    }
}
