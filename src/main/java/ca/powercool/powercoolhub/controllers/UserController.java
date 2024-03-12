package ca.powercool.powercoolhub.controllers;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
            return (user.getRole().equals(UserRole.EMPLOYEE)) ? "redirect:/users/employee/employeeDashboard"
                : "redirect:/users/manager/dashboard";
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
        return (authenticated && user.getRole().equals(UserRole.EMPLOYEE)) ? "redirect:/users/employee/employeeDashboard"
                : "redirect:/users/manager/dashboard";

    }

    // Logs user out
    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/users/login";
    }

    // Ensures that the user is logged in as a manager
    @GetMapping("/users/manager/dashboard")
    public String getManagerDashboard(HttpServletRequest request, Model model) {
        User manager = (User) request.getSession().getAttribute("user");
        if (manager == null || !manager.getRole().equals(UserRole.MANAGER)) {
            model.addAttribute("loginError", "Access Denied. You do not have permission to view this page.");
            request.getSession().invalidate();
            return "/login";
        }

        return "/users/manager/dashboard";
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

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam("email") String email) {
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    @PostMapping("/register")
    public String registerEmployeeIntoDataBase(@RequestParam("email") String employeeEmail,
            @RequestParam("name") String employeeName,
            @RequestParam("password") String employeePassword,
            HttpServletResponse statusSetter) {

        if (userRepository.existsByEmail(employeeEmail)) {
            statusSetter.setStatus(HttpServletResponse.SC_CONFLICT);
            return "register";
        }

        User newUser = new User();
        newUser.setName(employeeName);
        newUser.setEmail(employeeEmail);
        newUser.setPassword(employeePassword);
        newUser.setRole(UserRole.EMPLOYEE);
        userRepository.save(newUser);
        return "redirect:/users/login";
    }
}
