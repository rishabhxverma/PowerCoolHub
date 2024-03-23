package ca.powercool.powercoolhub.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("/login");
    }

    @GetMapping("/login")
    public String getLogin(LoginForm loginForm, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");

        // Ensure the user is redirected to a correct dashboard.
        if (user != null) {
            return (user.getRole().equals(UserRole.MANAGER)) ? "redirect:/users/manager/dashboard"
                    : "redirect:/users/employee/dashboard";
        }

        // If neither session attribute is present, return the login page
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@Valid LoginForm loginForm, BindingResult bindingResult,
            HttpServletRequest request, Model model) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        String email = loginForm.getEmail();
        String password = loginForm.getPassword();

        model.addAttribute("savedEmail", email);

        User user = this.userRepository.findByEmail(email);
        // User does not exist.
        if (user == null) {
            try {
                Thread.sleep(250); // Delay to prevent brute force attacks
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            bindingResult.addError(new FieldError("loginForm", "email", "No user found with this email."));
            return "login";
        }

        // Use session to keep track of user data.
        request.getSession().setAttribute("user", user);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            try {
                Thread.sleep(250); // Delay to prevent brute force attacks
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            bindingResult.addError(new FieldError("loginForm", "password", "Incorrect password."));
            return "login";
        }

        return (user.getRole().equals(UserRole.MANAGER)) ? "redirect:/users/manager/dashboard"
                : "redirect:/employee";
    }

    // Logs user out
    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }

    // Ensures that the user is logged in as a manager
    @GetMapping("/users/manager/dashboard")
    public String getManagerDashboard(HttpServletRequest request) {
        return "users/manager/dashboard";
    }

    @GetMapping("/register")
    public String showRegister(Model model, HttpServletRequest request) {
        return "register";
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam("email") String email) {
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }
    @GetMapping("/employee")
    public String getEmployeeDashboard(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        // Pass model attribute to the view.
        model.addAttribute("user", user);

        return "users/employee/dashboard";
    }

    @PostMapping("/register")
    public String registerEmployeeIntoDataBase(@RequestParam("email") String employeeEmail,
            @RequestParam("name") String employeeName,
            @RequestParam("password") String employeePassword,
            @RequestParam("role") String userRole,
            HttpServletResponse statusSetter) {

        if (userRepository.existsByEmail(employeeEmail)) {
            statusSetter.setStatus(HttpServletResponse.SC_CONFLICT);
            return "register";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(employeePassword);

        User newUser = new User();
        newUser.setName(employeeName);
        newUser.setEmail(employeeEmail);
        newUser.setPassword(hashedPassword);
        newUser.setRole(userRole);
        userRepository.save(newUser);
        return "redirect:/login";
    }

    @GetMapping("/users/manager/employeeManagementSystem")
    public String getAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users/manager/employeeManagementSystem";
    }

    @GetMapping("/users/manager/operationsOnUsers/editUsers")
    public String getPathForUserEdition() {
        return "users/manager/operationsOnUsers/editUsers";
    }

    @GetMapping("/users/manager/operationsOnUsers/deleteUsers")
    public String getPathForUserDeletion() {
        return "users/manager/operationsOnUsers/deleteUsers";
    }

    @PostMapping("/users/manager/operationsOnUsers/editUsers")
    public String updateUserByEmailAddress(
            @RequestParam("oldEmail") String oldEmail,
            @RequestParam("name") String newName,
            @RequestParam("email") String newEmail,
            @RequestParam("role") String newTitle,
            @RequestParam("password") String newPassword) {

        User existingUser = userRepository.findByEmail(oldEmail);
        if (existingUser != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(newPassword);

            existingUser.setName(newName);
            existingUser.setEmail(newEmail);
            if (newTitle.toLowerCase().equals("manager")) {
                existingUser.setRole(UserRole.MANAGER);
            } else {
                existingUser.setRole(UserRole.EMPLOYEE);
            }
            existingUser.setPassword(hashedPassword);
            userRepository.save(existingUser);

            return "users/manager/operationsOnUsers/successMessageOnUpdate";
        } else {
            return "users/manager/operationsOnUsers/failedUpdate";
        }
    }

    @PostMapping("/users/manager/operationsOnUsers/deleteUsers")
    public String deleteUserByEmail(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            userRepository.delete(user);
            return "users/manager/operationsOnUsers/successfulDeletion";
        } else {
            return "users/manager/operationsOnUsers/failedDeletion";
        }
    }
}
