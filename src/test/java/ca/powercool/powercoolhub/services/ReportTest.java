package ca.powercool.powercoolhub.services;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import ca.powercool.powercoolhub.controllers.ReportController;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.models.report.TechnicianWorkLogReport;
import ca.powercool.powercoolhub.repositories.UserRepository;
import ca.powercool.powercoolhub.services.TechnicianWorkLogService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebMvcTest(ReportController.class)
public class ReportTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TechnicianWorkLogService technicianWorkLogService;

    private List<User> techs;
    private MockHttpSession session = new MockHttpSession();

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setRole(UserRole.MANAGER);

        session.setAttribute("user", mockUser);

        User technician = new User();
        technician.setName("Expected Technician Name");
        technician.setRole(UserRole.TECHNICIAN);
        technician.setId(1L);
        
        techs = Arrays.asList(technician); 
        given(userRepository.findByRole(UserRole.TECHNICIAN)).willReturn(techs);
    }

    @Test
    void getReportPage_ShouldReturnReportViewWithTechnicians() throws Exception {
        mockMvc.perform(get("/manager/report").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("users/manager/report"))
            .andExpect(model().attribute("technicians", hasSize(techs.size())))
            .andExpect(model().attribute("technicians", hasItem(
                allOf(
                    hasProperty("name", is("Expected Technician Name")),
                    hasProperty("role", is(UserRole.TECHNICIAN))
                )
            )));
    }

    @Test
    void downloadTechCSV_ShouldGenerateZip() throws Exception {
        // Given
        String startDate = "2021-01-01";
        String endDate = "2021-01-07";
        String[] technicianIds = {"1", "2", "3"};
        List<TechnicianWorkLogReport> mockWorkLogs = Arrays.asList(
            // Create some mock TechnicianWorkLogReport objects with dummy data
            new TechnicianWorkLogReport("Technician 1", LocalDate.of(2021, 1, 1), "08:00 AM", "8:04 AM", "0 mins"),
            new TechnicianWorkLogReport("Technician 1", LocalDate.of(2021, 1, 2), "08:00 AM", "16:00 PM", "7 hours, 15 mins")
        );

        // Mock the service layer to return a list of work logs for each technician ID
        for (String id : technicianIds) {
            User mockUser = new User();
            mockUser.setId(Long.parseLong(id));
            mockUser.setName("Technician " + id);

            given(userRepository.findById(Long.parseLong(id))).willReturn(Optional.of(mockUser));

            given(technicianWorkLogService.getTechnicianWorkLogReport(
                any(), eq(startDate), eq(endDate))
            ).willReturn(mockWorkLogs);
        }

        String expectedZipName = "attachment; filename=\"r" + startDate + "_" + endDate + ".zip\"";

        // Perform a POST request to the download CSV endpoint
        MockHttpServletResponse response = mockMvc.perform(post("/manager/report/downloadTechCSV")
                .param("start-date", startDate)
                .param("end-date", endDate)
                .param("technicianIds", technicianIds).session(session))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/zip"))
            .andReturn().getResponse();

        // Assert that the response has a Content-Disposition header for file download
        String contentDisposition = response.getHeader(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.matches(expectedZipName));
    }

}
