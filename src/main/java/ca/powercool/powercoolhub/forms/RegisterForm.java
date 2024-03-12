package ca.powercool.powercoolhub.forms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public class RegisterForm {
    @NotNull
    @Size(min = 2, max = 30, message = "Name must be between {min} and {max} characters long.")
    private String name;

    @NotNull
    @Size(min = 5, message = "Password must be at least {min} characters long.")
    private String password;

    @NotNull
    @Email(message = "Invalid email.")
    @Size(min = 2, max = 255, message = "Email must be between {min} and {max} characters long.")
    private String email;

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
