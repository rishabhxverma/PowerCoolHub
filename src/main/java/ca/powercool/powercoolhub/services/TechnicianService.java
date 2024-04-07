package ca.powercool.powercoolhub.services;

import java.time.LocalDate;
import java.util.List;

import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.models.User;

public interface TechnicianService {
    List<Job> getUpcomingJobs(User user, String startDate, String endDate);

    String getLatestCompletedJobAddress(Long techId);

    boolean isTechnicianWithinRange(Long technicianId, double latitude, double longitude);

    double calculateDistance(double techLat, double techLong, double jobLat, double jobLong);
}
