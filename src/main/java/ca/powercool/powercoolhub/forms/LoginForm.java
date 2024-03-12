package ca.powercool.powercoolhub.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginForm {

    private String message;

    @Email(message = "Invalid email.")
    @Size(min = 2, message = "Email must be at least {min} characters long.")
    private String email;

    @NotNull
    @Size(min = 5, message = "Password must be at least {min} characters long.")
    private String password;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getMessage() {
        return this.message;
    }
}