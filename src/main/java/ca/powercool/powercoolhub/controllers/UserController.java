package ca.powercool.powercoolhub.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    private UserRepository userRepo;

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("/users/login");
    }

    @GetMapping("/users/login")
    public String getLogin(Model model, HttpServletRequest request) {
        // Check for manager session attribute
        User manager = (User) request.getSession().getAttribute("manager_session");
        if (manager != null) {
            return "redirect:/users/manager/managerDashboard";
        }

        // Check for employee session attribute
        User employee = (User) request.getSession().getAttribute("employee_session");
        if (employee != null) {
            return "redirect:/users/employee/employeeDashboard";
        }

        // If neither session attribute is present, return the login page
        return "/users/login";
    }

    @PostMapping("/users/login")
    public String postLogin(@RequestParam Map<String, String> data, Model model, HttpServletRequest request) {
        String email = data.get("email");
        String password = data.get("password");
        User user = userRepo.findByEmail(email);

        model.addAttribute("email", email);
        // Check if the user exists
        if (user == null) {
            model.addAttribute("loginError", "Email not found.");
            return "/users/login";
        }
        // Check if the password is correct
        if (!user.getPassword().equals(password)) {
            model.addAttribute("loginError", "Incorrect password.");
            return "/users/login";
        }
        // Correct password, proceed with setting session and redirecting
        else if (user.getRole().equals("manager")) {
            request.getSession().setAttribute("manager_session", user);
            return "redirect:/users/manager/managerDashboard";
        } else if (user.getRole().equals("employee")) {
            request.getSession().setAttribute("employee_session", user);
            return "redirect:/users/employee/employeeDashboard";
        } else {
            request.getSession().invalidate();
            model.addAttribute("loginError", "User role is not recognized."); // This should never happen
            return "/users/login";
        }
    }

    // Logs user out
    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        request.getSession().invalidate();
        return "/users/login";
    }

    // Ensures that the user is logged in as a manager
    @GetMapping("/users/manager/managerDashboard")
    public String getManagerDashboard(HttpServletRequest request, Model model) {
        User manager = (User) request.getSession().getAttribute("manager_session");
        if (manager == null || !manager.getRole().equals("manager")) {
            model.addAttribute("loginError", "Access Denied. You do not have permission to view this page.");
            request.getSession().invalidate();
            return "/users/login";
        }

        return "/users/manager/managerDashboard";
    }

    // Ensures that the user is logged in as an employee
    @GetMapping("/users/employee/employeeDashboard")
    public String getEmployeeDashboard(HttpServletRequest request, Model model) {
        User employee = (User) request.getSession().getAttribute("employee_session");
        if (employee == null || !employee.getRole().equals("employee")) {
            model.addAttribute("loginError", "Access Denied. You do not have permission to view this page.");
            request.getSession().invalidate();
            return "/users/login";
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
            userRepo.save(newUser);
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
