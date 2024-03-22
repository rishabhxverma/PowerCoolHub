package ca.powercool.powercoolhub.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ca.powercool.powercoolhub.models.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    List<Customer> findByNameLikeIgnoreCase(String namePattern);
    Optional<Customer> findById(Integer id);

    //customer query
    // has upcoming appointment, CustomerState != CustomerState.ARCHIVED

    List<Customer> findByStateNot(Customer.CustomerState state);
    List<Customer> findByState(Customer.CustomerState state);
}