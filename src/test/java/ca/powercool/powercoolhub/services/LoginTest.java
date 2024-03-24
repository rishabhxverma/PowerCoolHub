package ca.powercool.powercoolhub.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import ca.powercool.powercoolhub.controllers.UserController;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.repositories.UserRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class LoginTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void canLogInAsManager() throws Exception {
        // Arrange
        String plainPassword = "correctpassword";
        String email = "valid@example.com";

        // We don't actually call encode here because it's a mock.
        // Just need to provide a string to represent a hashed password.
        String hashedPassword = "hashedpassword"; 

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(hashedPassword); 
        mockUser.setRole(UserRole.MANAGER);

        // Act
        // Mock the behavior of findByEmail to return our user
        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        
        // Mock the behavior of passwordEncoder.matches to return true when the correct password is provided
        when(passwordEncoder.matches(plainPassword, hashedPassword)).thenReturn(true);

        // Assert
        mockMvc.perform(post("/login")
                .param("email", email)
                .param("password", plainPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager"));
    }

    @Test
    public void canLogInAsTechnician() throws Exception {
        // Arrange
        String plainPassword = "correctpassword";
        String email = "valid@example.com";

        // We don't actually call encode here because it's a mock.
        // Just need to provide a string to represent a hashed password.
        String hashedPassword = "hashedpassword"; 

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(hashedPassword); 
        mockUser.setRole(UserRole.TECHNICIAN);

        // Act
        // Mock the behavior of findByEmail to return our user
        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        
        // Mock the behavior of passwordEncoder.matches to return true when the correct password is provided
        when(passwordEncoder.matches(plainPassword, hashedPassword)).thenReturn(true);

        // Assert
        mockMvc.perform(post("/login")
                .param("email", email)
                .param("password", plainPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/technician"));
    }
}

