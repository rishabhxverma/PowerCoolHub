package ca.powercool.powercoolhub.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.powercool.powercoolhub.models.Customer;
import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.repositories.CustomerRepository;
import ca.powercool.powercoolhub.repositories.JobRepository;
import ca.powercool.powercoolhub.repositories.TechnicianWorkLogRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TechnicianWorkLogRepository technicianWorkLogRepository;

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

    @Override
    public boolean isTechnicianWithinRange(Long technicianId, double latitude, double longitude) {
        String address = getLatestCompletedJobAddress(technicianId);
        
        Map<String, Double> result = geocodingService.geocodeAddress(address);
        
        double jobLatitude = result.get("lat");
        double jobLongitude = result.get("lng");
        double distance = calculateDistance(latitude, longitude, jobLatitude, jobLongitude);
        
        return distance <= 1000;
    }

    @Override
    public double calculateDistance(double techLat, double techLong, double jobLat, double jobLong) {
        final int R = 6371000; // Radius of the earth in meters

        double latDistance = Math.toRadians(jobLat - techLat);
        double lonDistance = Math.toRadians(jobLong - techLong);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(techLat)) * Math.cos(Math.toRadians(jobLat))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to meters

    return distance;
    }

    @Override
    public String getLastClockOutAddress(Long technicianId) {
        String lastClockOutLocation = technicianWorkLogRepository.findLastClockOutAddressForTechnician(technicianId);
        return lastClockOutLocation;
    }

}
