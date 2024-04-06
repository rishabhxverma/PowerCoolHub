package ca.powercool.powercoolhub.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import ca.powercool.powercoolhub.models.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    List<Customer> findByNameLikeIgnoreCase(String namePattern);
    @NonNull
    Optional<Customer> findById(Integer id);

    // customer query
    // has upcoming appointment, CustomerState != CustomerState.ARCHIVED
    List<Customer> findByState(Customer.CustomerState state);

    // count by state
    int countByState(Customer.CustomerState state);

    Customer findByEmail(String email);
    
    //find if customer already exists (same name, email, address)
    boolean existsByNameAndEmailAndAddress(String name, String email, String address);

    //return the same customer if exists
    Customer findByNameAndEmailAndAddress(String name, String email, String address);

    boolean existsByAddress(String address);
}