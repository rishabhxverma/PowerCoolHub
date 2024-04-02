package ca.powercool.powercoolhub.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.repositories.JobRepository;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    @Autowired
    private JobRepository jobRepository;

    @Override
    public List<Job> getUpcomingJobs(User user, String startDate, String endDate) {
        LocalDate localStartDate = LocalDate.parse(startDate);
        LocalDate localEndDate = LocalDate.parse(endDate);
        List<Job> upcomingJobs = this.jobRepository.findIncompleteJobsByTechnicianIdBetween(user.getId(), localStartDate, localEndDate);
        return upcomingJobs;
    }

}
