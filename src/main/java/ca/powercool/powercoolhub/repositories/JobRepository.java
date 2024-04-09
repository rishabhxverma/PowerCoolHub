package ca.powercool.powercoolhub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.models.Job.JobType;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    @Query("SELECT j FROM Job j WHERE j.serviceDate BETWEEN :startDate AND :endDate")

    List<Job> findJobsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Job> findByCustomerId(int customerId); // customer's appointments

    List<Job> findByJobDoneTrue(); // appointment history

    List<Job> findByJobDoneFalse(); // active appointments

    List<Job> findByPaymentReceivedTrue(); // payment history

    List<Job> findByPaymentReceivedFalse(); // pending payments

    List<Job> findByServiceDate(LocalDate date);

    List<Job> findByCustomerNameLikeIgnoreCase(String customerName);

    List<Job> findByJobType(JobType jobType);

    // find jobs assigned to specific technician ID
    @Query("SELECT j FROM Job j JOIN j.technicianIds t WHERE t = :technicianId")
    List<Job> findByTechnicianId(@Param("technicianId") int technicianId);

    @Query(value = "SELECT * FROM jobs j " +
            "INNER JOIN job_technicians jt ON j.id = jt.job_id " +
            "WHERE jt.technician_id = ?1 " +
            "AND j.service_date BETWEEN ?2 AND ?3", nativeQuery = true)
    List<Job> findByTechnicianIdBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT * FROM jobs j " +
        "INNER JOIN job_technicians jt ON j.id = jt.job_id " +
        "WHERE jt.technician_id = ?1 " +
        "AND j.service_date BETWEEN ?2 AND ?3 AND j.job_done = false", nativeQuery = true)
    List<Job> findIncompleteJobsByTechnicianIdBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT * FROM jobs j " +
        "INNER JOIN job_technicians jt ON j.id = jt.job_id " +
        "WHERE jt.technician_id = ?1 AND j.job_done = true AND j.job_done_time IS NOT NULL " +
        "ORDER BY j.job_done_time DESC LIMIT 1", nativeQuery = true)
    Job findLatestCompleteJobByTechnicianId(Long userId);

    // find jobs that arent assigned to any techID
    @Query("SELECT j FROM Job j WHERE j.technicianIds IS EMPTY")
    List<Job> findUnassignedJobs();

    // find tech ids from jobs on a date, get number of jobs the tech that day
    @Query("SELECT j.technicianIds FROM Job j WHERE j.serviceDate = :date")
    List<Integer> findTechIdsByDate(@Param("date") Date date);

    // find jobs per tech id on a given date
    @Query("SELECT j FROM Job j WHERE :technicianId MEMBER OF j.technicianIds AND j.serviceDate = :date")
    List<Job> findJobsByTechIdAndDate(@Param("technicianId") int technicianId, @Param("date") Date date);

}