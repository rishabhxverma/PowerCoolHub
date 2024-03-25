package ca.powercool.powercoolhub.services;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.assertj.core.api.Assertions.assertThat;

import ca.powercool.powercoolhub.controllers.UserController;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;

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
    public void whenAccessRoot_thenRedirectToLogin() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void canLogInAsManager() throws Exception {
        String plainPassword = "correctpassword";
        String email = "valid@example.com";

        String hashedPassword = "hashedpassword"; 

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(hashedPassword); 
        mockUser.setRole(UserRole.MANAGER);

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        
        when(passwordEncoder.matches(plainPassword, hashedPassword)).thenReturn(true);

        mockMvc.perform(post("/login")
                .param("email", email)
                .param("password", plainPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager"))
                .andExpect(result -> {
                    HttpSession session = result.getRequest().getSession(false); // if no session, return null
                    assertThat(session).isNotNull();
                    @SuppressWarnings("null")
                    User user = (User) session.getAttribute("user");
                    assertThat(user).isNotNull();
                    assertThat(user.getRole()).isEqualTo(UserRole.MANAGER);
                });
    }

    @Test
    public void canLogInAsTechnician() throws Exception {
        String plainPassword = "correctpassword";
        String email = "valid@example.com";

        String hashedPassword = "hashedpassword"; 

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(hashedPassword); 
        mockUser.setRole(UserRole.TECHNICIAN);

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(passwordEncoder.matches(plainPassword, hashedPassword)).thenReturn(true);

        mockMvc.perform(post("/login")
            .param("email", email)
            .param("password", plainPassword))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/technician"))
            .andExpect(result -> {
                HttpSession session = result.getRequest().getSession(false);  // if no session, return null
                assertThat(session).isNotNull();
                @SuppressWarnings("null")
                User user = (User) session.getAttribute("user");
                assertThat(user).isNotNull();
                assertThat(user.getRole()).isEqualTo(UserRole.TECHNICIAN);
            });
    }

    @Test
    public void whenLoggedInAsManager_thenRedirectedToManagerPage() throws Exception {
        User mockUser = new User();
        mockUser.setRole(UserRole.MANAGER);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", mockUser);

        mockMvc.perform(get("/manager").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("users/manager/dashboard"));
    }

    @Test
    public void whenLoggedInAsTechnician_thenRedirectedToTechnicianPage() throws Exception {
        User mockUser = new User();
        mockUser.setRole(UserRole.TECHNICIAN);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", mockUser);

        mockMvc.perform(get("/technician").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("users/technician/dashboard"));
    }

    @Test
    public void whenLoggedInAsManager_thenCantAccessTechnicianPage() throws Exception {
        User mockUser = new User();
        mockUser.setRole(UserRole.MANAGER);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", mockUser);

        mockMvc.perform(get("/technician").session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void whenLoggedInAsTechnician_thenCantAccessManagerPage() throws Exception {
        User mockUser = new User();
        mockUser.setRole(UserRole.TECHNICIAN);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", mockUser);

        mockMvc.perform(get("/manager").session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void whenLoggedOut_thenSessionInvalidatedAndRedirectedToLogin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        mockMvc.perform(get("/logout").session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));

        assertThat(session.isInvalid()).isTrue();
    }

    


    
    
}

