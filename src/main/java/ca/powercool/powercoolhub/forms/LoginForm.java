package ca.powercool.powercoolhub.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginForm {
    @NotNull
    @Email(message = "Invalid email.")
    @Size(min = 2, message = "Email must be at least {min} characters long.")
    private String email;

    @NotNull
    @Min(value = 5, message = "Password must be at least {value} characters long.")
    private String password;

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}