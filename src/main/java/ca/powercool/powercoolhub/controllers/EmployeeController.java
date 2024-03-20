package ca.powercool.powercoolhub.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

@Controller
public class EmployeeController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/employee")
    public String getEmployeeDashboard(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        // Pass model attribute to the view.
        model.addAttribute("user", user);

        return "users/employee/dashboard";
    }
}
