package ca.powercool.powercoolhub.services;

import static org.mockito.ArgumentMatchers.any;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import ca.powercool.powercoolhub.controllers.UserController;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.repositories.JobRepository;
import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;


public class CalendarTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobRepository jobRepository; 

    // @Test
    // public void shouldReturnJobsForWeek() throws Exception {
    //     // Arrange
    //     List<Job> jobs = new ArrayList<>();
    //     // Populate jobs list with test data

    //     when(jobRepository.findJobsBetweenDates(any(), any())).thenReturn(jobs);

    //     // Act & Assert
    //     mockMvc.perform(get("/jobs/getWeek?startDate=2024-03-18&endDate=2024-03-24"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$", hasSize(jobs.size())));
    // }
}
