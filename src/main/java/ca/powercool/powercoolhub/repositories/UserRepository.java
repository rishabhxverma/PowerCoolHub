package ca.powercool.powercoolhub.repositories;
import ca.powercool.powercoolhub.controllers.UserController;
import org.springframework.data.jpa.repository.JpaRepository;

import ca.powercool.powercoolhub.models.User;

import java.sql.*;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    default boolean emailExists(String userEmail) {
        String query = "SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://dpg-cnh7f3da73kc73b7o2ag-a.oregon-postgres.render.com/powercool_hub_database",
                "powercool_hub_database_user", "jblaTia2sez3rMqOj8tW9yxcFgEiybMg");
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, userEmail);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    default void checkUserRegistrationByEmail(String employeeEmail) throws UserController.RegistrationException {
        if (emailExists(employeeEmail)) {
            throw new UserController.RegistrationException("The email is already in use!");
        }
    }
}
