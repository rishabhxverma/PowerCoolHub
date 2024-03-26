package ca.powercool.powercoolhub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.powercool.powercoolhub.models.Job;

import java.sql.Date;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    @Query("SELECT j FROM Job j WHERE j.serviceDate BETWEEN :startDate AND :endDate")
    List<Job> findJobsBetweenDates(@Param("startDate") java.sql.Date startDate, @Param("endDate") java.sql.Date endDate);
    List<Job> findByCustomerId(int customerId);     // customer's appointments
    List<Job> findByJobDoneTrue();                  // appointment history
    List<Job> findByJobDoneFalse();                 // active appointments
    List<Job> findByPaymentReceivedTrue();          // payment history
    List<Job> findByPaymentReceivedFalse();         // pending payments

    //find jobs assigned to specific technician ID
    @Query("SELECT j FROM Job j JOIN j.technicianIds t WHERE t = :technicianId")
    List<Job> findByTechnicianId(@Param("technicianId") int technicianId);

    //find jobs assigned to specific tech ID within dates
    // @Query("SELECT j FROM Job j WHERE :technicianId MEMBER OF j.technicianIds AND j.serviceDate BETWEEN :startDate AND :endDate")
    // List<Job> findByTechnicianIdBetweenDates(@Param("technicianId") int technicianId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    //find jobs that arent assigned to any techID
    @Query("SELECT j FROM Job j WHERE j.technicianIds IS EMPTY")
    List<Job> findUnassignedJobs();

    //find tech ids from jobs on a date, get number of jobs the tech that day
    @Query("SELECT j.technicianIds FROM Job j WHERE j.serviceDate = :date")
    List<Integer> findTechIdsByDate(@Param("date") Date date);

    //find jobs per tech id on a given date
    @Query("SELECT j FROM Job j WHERE :technicianId MEMBER OF j.technicianIds AND j.serviceDate = :date")
    List<Job> findJobsByTechIdAndDate(@Param("technicianId") int technicianId, @Param("date") Date date);



}