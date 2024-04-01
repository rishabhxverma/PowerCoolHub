package ca.powercool.powercoolhub.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import ca.powercool.powercoolhub.forms.LoginForm;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("/login");
    }

    @GetMapping("/login")
    public String getLogin(LoginForm loginForm, HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");

        // Ensure the user is redirected to a correct dashboard.
        if (user != null) {
            return (user.getRole().equals(UserRole.MANAGER)) ? "redirect:/manager"
                    : "redirect:/technician";
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
        if (!passwordEncoder.matches(password, user.getPassword())) {
            try {
                Thread.sleep(250); // Delay to prevent brute force attacks
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            bindingResult.addError(new FieldError("loginForm", "password", "Incorrect password."));
            return "login";
        }

        return (user.getRole().equals(UserRole.MANAGER)) ? "redirect:/manager"
                : "redirect:/technician";
    }

    // Logs user out
    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
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

        String hashedPassword = passwordEncoder.encode(employeePassword);

        User newUser = new User();
        newUser.setName(employeeName);
        newUser.setEmail(employeeEmail);
        newUser.setPassword(hashedPassword);
        newUser.setRole(userRole);
        userRepository.save(newUser);
        if (isPasswordValid(employeePassword)) {
            return "redirect:/login";
        } else {
            return "loginFailed";
        }

    }

    @GetMapping("/users/manager/employeeManagementSystem")
    public String getAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users/manager/employeeManagementSystem";
    }

    @GetMapping("/users/manager/operationsOnUsers/editUsers/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "users/manager/operationsOnUsers/editUsers";
    }

    @PostMapping("/users/manager/operationsOnUsers/editUsers/{id}")
    @ResponseBody
    public ResponseEntity<?> updateEmployee(@PathVariable("id") Long id, @ModelAttribute("user") User userDetails,
            @RequestParam String action) {
        boolean success = false;
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        if ("delete".equals(action)) {
            userRepository.delete(existingUser);
            return ResponseEntity.ok(Collections.singletonMap("message", "User deleted successfully"));
        }
        existingUser.setName(userDetails.getName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setRole(userDetails.getRole());
        if (isPasswordValid(userDetails.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Update failed"));
        }

        userRepository.save(existingUser);

        success = true;

        if (success) {
            return ResponseEntity.ok(Collections.singletonMap("message", "User updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Update failed"));
        }
    }

    private boolean isPasswordValid(String password) {
        boolean isLongEnough = password.length() > 5;
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
        boolean result = isLongEnough && hasUppercase && hasNumber && hasSpecialChar;
        return result;
    }
}
