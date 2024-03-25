package ca.powercool.powercoolhub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.powercool.powercoolhub.models.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findById(Long id);
}
