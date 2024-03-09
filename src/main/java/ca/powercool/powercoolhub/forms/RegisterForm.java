package ca.powercool.powercoolhub.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RegisterForm {
    @NotNull
    @Min(value = 2, message = "Name must be at least {value} characters long.")
    @Max(value = 30, message = "Name must be at max {value} characters.")
    private String name;

    @NotNull
    @Min(value = 5, message = "Password must be at least {value} characters long.")
    private String password;

    @NotNull
    @Email(message = "Invalid email.")
    @Min(value = 2, message = "Email must be at least {value} characters long.")
    @Max(value = 255, message = "Email must be at max {value} characters.")
    private String email;

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getters
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
