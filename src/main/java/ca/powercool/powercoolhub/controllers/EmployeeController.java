package ca.powercool.powercoolhub.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EmployeeController {

    @Autowired
    private UserRepository userRepo;

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

        if (userRepo.existsByEmail(employeeEmail)) {
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
        userRepo.save(newUser);
        return "redirect:/login";
    }

    @GetMapping("/users/manager/employeeManagementSystem")
    public String getAllUsers(Model model) {
        List<User> users = userRepo.findAll();
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

        User existingUser = userRepo.findByEmail(oldEmail);
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
            userRepo.save(existingUser);

            return "users/manager/operationsOnUsers/successMessageOnUpdate";
        } else {
            return "users/manager/operationsOnUsers/failedUpdate";
        }
    }

    @PostMapping("/users/manager/operationsOnUsers/deleteUsers")
    public String deleteUserByEmail(@RequestParam("email") String email) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            userRepo.delete(user);
            return "users/manager/operationsOnUsers/successfulDeletion";
        } else {
            return "users/manager/operationsOnUsers/failedDeletion";
        }
    }
}
