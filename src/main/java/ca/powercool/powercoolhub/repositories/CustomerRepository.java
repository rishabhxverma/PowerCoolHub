package ca.powercool.powercoolhub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.powercool.powercoolhub.models.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
}
