package ca.powercool.powercoolhub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.powercool.powercoolhub.models.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    
}
