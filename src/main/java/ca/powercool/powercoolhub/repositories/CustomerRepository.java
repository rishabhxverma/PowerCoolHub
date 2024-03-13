package ca.powercool.powercoolhub.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.powercool.powercoolhub.models.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    List<Customer> findByNameLikeIgnoreCase(String namePattern);
}
