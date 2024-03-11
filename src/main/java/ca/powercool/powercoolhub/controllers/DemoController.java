package ca.powercool.powercoolhub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {
    /**
     * Display demo login page.
     */
    @GetMapping("/demo/login")
    public String showLogin() {
        return "login";
    }

    /**
     * Display demo login error page.
     */
    @GetMapping("/demo/login/error")
    public String showLoginError() {
        return "login/error";
    }

    /**
     * Display demo register page.
     */
    @GetMapping("/demo/register")
    public String showRegister() {
        return "register";
    }

    /**
     * Display demo register page.
     */
    @GetMapping("/demo/register/error")
    public String showRegisterError() {
        return "register/error";
    }
}
