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
    @Query("SELECT c FROM Customer c WHERE c.state != 'ARCHIVED'")
    List<Customer> findByStateNot(Customer.CustomerState state);
    @Query("SELECT c FROM Customer c WHERE c.state = 'ARCHIVED'")
    List<Customer> findArchivedCustomers(Customer.CustomerState state);
    @Query("SELECT c FROM Customer c WHERE c.state = 'UPCOMING_APPOINTMENT_SERVICE'")
    List<Customer> findServiceCustomers(Customer.CustomerState state);
    @Query("SELECT c FROM Customer c WHERE c.state = 'UPCOMING_APPOINTMENT_INSTALL'")
    List<Customer> findInstallCustomers(Customer.CustomerState state);
    @Query("SELECT c FROM Customer c WHERE c.state = 'UPCOMING_APPOINTMENT_REPAIR'")
    List<Customer> findRepairCustomers(Customer.CustomerState state);
    


}
