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
    List<Job> findJobsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}