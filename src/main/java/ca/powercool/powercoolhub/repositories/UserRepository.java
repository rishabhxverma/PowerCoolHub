package ca.powercool.powercoolhub.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.powercool.powercoolhub.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
}
