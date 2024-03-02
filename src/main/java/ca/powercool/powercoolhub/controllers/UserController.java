package ca.powercool.powercoolhub.controllers;

import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserController {
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
            return "redirect:/users/managerDashboard"; 
        }
        
        // Check for employee session attribute
        User employee = (User) request.getSession().getAttribute("employee_session");
        if (employee != null) {
            return "redirect:/users/employeeDashboard"; 
        }
        
        // If neither session attribute is present, return the login page
        return "/users/login";
    }

    @PostMapping("/users/login")
    public String postLogin(@RequestParam Map<String, String> data, Model model, HttpServletRequest request) {
        String name = data.get("username");
        String password = data.get("password");
        User user = userRepo.findByName(name);

        model.addAttribute("username", name);
        // Check if the user exists
        if (user == null) {
            model.addAttribute("loginError", "Username not found.");
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
            return "redirect:/users/managerDashboard";
        } 
        else if (user.getRole().equals("employee")) {
            request.getSession().setAttribute("employee_session", user);
            return "redirect:/users/employeeDashboard";
        }  
        else {
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
    @GetMapping("/users/managerDashboard")
    public String getManagerDashboard(HttpServletRequest request, Model model) {
        User manager = (User) request.getSession().getAttribute("manager_session");
        if (manager == null || !manager.getRole().equals("manager")) {
            model.addAttribute("loginError", "Access Denied. You do not have permission to view this page.");
            request.getSession().invalidate();
            return "/users/login";
        }
        
        return "/users/managerDashboard";
    }

    // Ensures that the user is logged in as an employee
    @GetMapping("users/employeeDashboard")
    public String getEmployeeDashboard(HttpServletRequest request, Model model) {
        User employee = (User) request.getSession().getAttribute("employee_session");
        if (employee == null || !employee.getRole().equals("employee")) {
            model.addAttribute("loginError", "Access Denied. You do not have permission to view this page.");
            request.getSession().invalidate();
            return "/users/login";
        }
        return "/users/employeeDashboard"; 
    }
}
