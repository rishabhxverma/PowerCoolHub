package ca.powercool.powercoolhub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ca.powercool.powercoolhub.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    // find all users with role technician
    List<User> findByRole(String role);

    @Query(value = "SELECT u.* FROM users u " +
            "INNER JOIN job_technicians jt ON jt.technician_id = u.id " +
            "WHERE jt.job_id = ?1", nativeQuery = true)
    List<User> findAssignedTechnicians(Integer jobId);
}
