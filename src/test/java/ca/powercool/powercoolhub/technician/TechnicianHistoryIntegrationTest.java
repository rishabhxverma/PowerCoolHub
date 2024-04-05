package ca.powercool.powercoolhub.technician;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.services.TechnicianWorkLogService;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * TODO: A proper integration testing is to use Selenium that runs a browser in
 * a VM to simulate user actions. However, this written tests should be
 * sufficient for many general use cases.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TechnicianHistoryIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TechnicianWorkLogService technicianWorkLogService;

    @Test
    @SuppressWarnings("null")
    public void testTechnicianHistoryFilterPage() throws Exception {
        MockHttpSession mockedTechnicianSession = mockTechnicianSession();

        mockMvc.perform(get("/technician/history").session(mockedTechnicianSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<option value=\"weekly\">Weekly</option>")))
                .andExpect(content().string(containsString("<option value=\"monthly\">Monthly</option>")))
                .andExpect(content().string(containsString("<option value=\"yearly\">Yearly</option>")));
    }

    @Test
    @SuppressWarnings("null")
    public void testTechnicianHistoryPageEmptyTable() throws Exception {
        MockHttpSession mockedTechnicianSession = mockTechnicianSession();
        mockMvc.perform(get("/technician/history").session(mockedTechnicianSession))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("<td>"))));
    }

    private MockHttpSession mockTechnicianSession() {
        // Set up a mock technician user
        User mockedUser = new User();
        mockedUser.setEmail("mock@email");
        mockedUser.setName("mockName");
        mockedUser.setRole(UserRole.TECHNICIAN);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", mockedUser);

        return session;
    }

    private TechnicianWorkLog createWorkLog(String action, String createdAt) {
        TechnicianWorkLog workLog = new TechnicianWorkLog();
        workLog.setAction(action);
        workLog.setCreatedAt(LocalDateTime.parse(createdAt));
        return workLog;
    }
}
