// package ca.powercool.powercoolhub.managerSideTests;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.when;
// import static org.hamcrest.Matchers.containsString;
// import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.mock.web.MockHttpSession;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.test.web.servlet.MockMvc;
// import ca.powercool.powercoolhub.controllers.UserController;
// import ca.powercool.powercoolhub.models.User;
// import ca.powercool.powercoolhub.models.UserRole;
// import ca.powercool.powercoolhub.repositories.UserRepository;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(UserController.class)
// public class EmployeeInformationUpdateTest {
//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private UserRepository userRepository;

//     private static final String USERS_MANAGER_VIEW = "users/manager/employeeManagementSystem";
//     private static final String EDIT_USERS_VIEW = "users/manager/operationsOnUsers/editUsers";
//     private User testUser;

//     @Configuration
//     static class AdditionalConfig {
//         @Bean
//         BCryptPasswordEncoder passwordEncoder() {
//             return new BCryptPasswordEncoder();
//         }
//     }
    
//     @BeforeEach
//     public void setUp() {
//         testUser = new User();
//         testUser.setId(1L);
//         testUser.setName("manager");
//         testUser.setEmail("man@email");
//         testUser.setPassword("man123");
//         testUser.setRole(UserRole.MANAGER);
//     }

//     @Test
//     public void whenAccessRoot_thenRedirectToLogin() throws Exception {
//         mockMvc.perform(get("/"))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/login"));
//     }

//     @Test
//     public void whenAccessRoot_thenReturns404() throws Exception {
//         mockMvc.perform(get("/"))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     public void whenLoggedInAsManager_thenRedirectedToManagerPage() throws Exception {

//         testUser.setRole(UserRole.MANAGER);
//         testUser.setEmail("man@email");
//         testUser.setPassword("man123");
//         MockHttpSession session = new MockHttpSession();
//         session.setAttribute("user", testUser);

//         mockMvc.perform(get("/manager").session(session))
//                 .andExpect(status().is2xxSuccessful())
//                 .andExpect(view().name("users/manager/dashboard"));
//     }

//     @Test
//     public void testGetAllUsers_ReturnsCorrectModelAndView() throws Exception {
//         List<User> users = Collectmanagerions.singletonList(testUser);
//         when(userRepository.findAll()).thenReturn(users);

//         mockMvc.perform(get("/users/manager/employeeManagementSystem"))
//                 .andExpect(status().is2xxSuccessful())
//                 .andExpect(view().name(USERS_MANAGER_VIEW))
//                 .andExpect(model().attributeExists("users"))
//                 .andExpect(model().attribute("users", hasSize(1)))
//                 .andExpect(model().attribute("users", users));
//     }

//     @Test
//     public void testShowEditUserForm_WithValidId_ReturnsCorrectModelAndView() throws Exception {
//         when(userRepository.findById(eq(87L))).thenReturn(Optional.of(testUser));

//         mockMvc.perform(get("/users/manager/operationsOnUsers/editUsers/{id}", 1L))
//                 .andExpect(status().is2xxSuccessful())
//                 .andExpect(view().name(EDIT_USERS_VIEW))
//                 .andExpect(model().attributeExists("user"))
//                 .andExpect(model().attribute("user", testUser));
//     }

//     @Test
//     public void testShowEditUserForm_WithInvalidId_ThrowsException() throws Exception {
//         when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

//         mockMvc.perform(get("/users/manager/operationsOnUsers/editUsers/{id}", 99L))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     public void testUpdateEmployee_DeleteAction_ReturnsSuccessMessage() throws Exception {
//         when(userRepository.findById(eq(1L))).thenReturn(Optional.of(testUser));
//         doNothing().when(userRepository).delete(any(User.class));

//         mockMvc.perform(post("/users/manager/operationsOnUsers/editUsers/{id}", 1L)
//                 .param("action", "delete"))
//                 .andExpect(status().is2xxSuccessful())
//                 .andExpect(content().string(containsString("User deleted successfully")));
//     }
// }
