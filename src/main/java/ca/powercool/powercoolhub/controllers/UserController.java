package ca.powercool.powercoolhub.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import ca.powercool.powercoolhub.forms.LoginForm;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserController {

    static class RegistrationException extends Exception {
        public RegistrationException(String message) {
            super(message);
        }
    }

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("/users/login");
    }

    @GetMapping("/users/login")
    public String getLogin(LoginForm loginForm, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");

        // Ensure the user is redirected to a correct dashboard.
        if (user != null) {
            return (user.getRole() == UserRole.EMPLOYEE) ? "redirect:/users/employeeDashboard"
                    : "redirect:/users/managerDashboard";
        }

        // If neither session attribute is present, return the login page
        return "/login";
    }

    @PostMapping("/users/login")
    public String postLogin(@Valid LoginForm loginForm, BindingResult bindingResult,
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "/login";
        }

        String email = loginForm.getEmail();
        String password = loginForm.getPassword();

        User user = this.userRepository.findByEmail(email);

        // User does not exist.
        if (user == null) {
            bindingResult.addError(new FieldError("message", "user", "The user does not exist."));
            return "/login";
        }

        // Use session to keep track of user data.
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        // TODO: Add an authentication give user's email and password.
        boolean authenticated = user.getPassword().equals(password);
        return (authenticated && user.getRole() == UserRole.EMPLOYEE) ? "redirect:/users/employee/employeeDashboard"
                : "redirect:/users/manager/managerDashboard";

    }

    // Logs user out
    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/users/login";
    }

    // Ensures that the user is logged in as a manager
    @GetMapping("/users/manager/managerDashboard")
    public String getManagerDashboard(HttpServletRequest request, Model model) {
        User manager = (User) request.getSession().getAttribute("user");
        if (manager == null || !manager.getRole().equals(UserRole.MANAGER)) {
            model.addAttribute("loginError", "Access Denied. You do not have permission to view this page.");
            request.getSession().invalidate();
            return "/login";
        }

        return "/users/manager/managerDashboard";
    }

    // Ensures that the user is logged in as an employee
    @GetMapping("/users/employee/employeeDashboard")
    public String getEmployeeDashboard(HttpServletRequest request, Model model) {
        User employee = (User) request.getSession().getAttribute("user");
        if (employee == null || !employee.getRole().equals(UserRole.EMPLOYEE)) {
            model.addAttribute("loginError", "Access Denied. You do not have permission to view this page.");
            request.getSession().invalidate();
            return "/login";
        }
        return "/users/employee/employeeDashboard";
    }

    @GetMapping("/register")
    public String showRegister(Model model, HttpServletRequest request) {
        return "/register";
    }

    @PostMapping("/register")
    public String registerEmployeeIntoDataBase(@RequestParam("email") String employeeEmail,
            @RequestParam("name") String employeeName,
            @RequestParam("password") String employeePassword,
            HttpServletResponse statusSetter) {

        try {
            checkUserRegistrationByEmail(employeeEmail);
            User newUser = new User();
            newUser.setName(employeeName);
            newUser.setEmail(employeeEmail);
            newUser.setPassword(employeePassword);
            newUser.setRole("employee");
            userRepository.save(newUser);
            statusSetter.setStatus(201);
            return "users/login";
        } catch (RegistrationException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return "register/error";
        }
    }

    private void checkUserRegistrationByEmail(String employeeEmail) throws RegistrationException {
        if (emailExists(employeeEmail)) {
            throw new RegistrationException("The email is already in use!");
        }
    }

    private boolean emailExists(String userEmail) {
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
}
