package ca.powercool.powercoolhub.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.models.technician.data.GroupedWorkLogsData;
import ca.powercool.powercoolhub.models.technician.data.WorkLogsFilter;
import ca.powercool.powercoolhub.repositories.TechnicianWorkLogRepository;
import ca.powercool.powercoolhub.services.TechnicianWorkLogService;
import ca.powercool.powercoolhub.utilities.LocalDateTimeUtility;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

@Controller
public class EmployeeController {

    @Autowired
    private TechnicianWorkLogRepository technicianWorkLogRepository;

    @Autowired
    private TechnicianWorkLogService technicianWorkLogService;

    @GetMapping("/employee")
    public String getEmployeeDashboard(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        model.addAttribute("user", user);
        List<TechnicianWorkLog> logs = technicianWorkLogRepository.findLatestWorkLogByUserId(user.getId());
        String clockButtonState = "clock_in"; // Assume default state
        if (!logs.isEmpty()) {
            TechnicianWorkLog latestLog = logs.get(0); // Get the latest log
            clockButtonState = latestLog.getAction().equals(TechnicianWorkLog.CLOCK_OUT) ? "clock_in" : "clock_out";
        }
        model.addAttribute("clockButtonState", clockButtonState);

        return "users/employee/dashboard";
    }

    @GetMapping("/employee/history")
    public String getEmployeeHistory(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = LocalDateTimeUtility.getFirstDayOfMonth(currentDateTime);
        LocalDateTime endDateTime = LocalDateTimeUtility.getLastDayOfMonth(currentDateTime);

        List<TechnicianWorkLog> workLogs = this.technicianWorkLogRepository.findWorkLogsBetween(user.getId(),
                startDateTime, endDateTime);

        List<GroupedWorkLogsData> historyData = this.technicianWorkLogService.getTechnicianHistoryData(workLogs);
        model.addAttribute("user", user);
        model.addAttribute("workLogs", historyData);

        return "users/employee/history";
    }

    /**
     * Retrieves filtered history data based on the specified filter.
     *
     * @param filter  The filter criteria to apply (e.g., "by week", "by month", "by
     *                year").
     * @param request The HTTP servlet request.
     * @param model   The model to which history data will be added.
     * @return The updated part of the template containing history data.
     */
    @GetMapping("/employee/history/filter")
    public String filterHistory(@RequestParam("by") String filter, HttpServletRequest request, Model model) {

        User user = (User) request.getSession().getAttribute("user");

        // TODO: Move this business logic to a service.
        LocalDateTime currentDateTime = LocalDateTime.now();

        LocalDateTime startDateTime = LocalDateTimeUtility.getFirstDayOfMonth(currentDateTime);
        LocalDateTime endDateTime = LocalDateTimeUtility.getLastDayOfMonth(currentDateTime);

        // Filter by weekly.
        if (filter.equals(WorkLogsFilter.BY_WEEK)) {
            startDateTime = LocalDateTimeUtility.getFirstDayOfWeek(currentDateTime);
            endDateTime = LocalDateTimeUtility.getLastDayOfWeek(currentDateTime);
        }
        // Filter by monthly.
        else if (filter.equals(WorkLogsFilter.BY_MONTH)) {
            startDateTime = LocalDateTimeUtility.getFirstDayOfMonth(currentDateTime);
            endDateTime = LocalDateTimeUtility.getLastDayOfMonth(currentDateTime);
        }
        // Filter by yearly.
        else if (filter.equals(WorkLogsFilter.BY_YEAR)) {
            startDateTime = LocalDateTimeUtility.getFirstDayOfYear(currentDateTime);
            endDateTime = LocalDateTimeUtility.getLastDayOfYear(currentDateTime);
        }

        List<TechnicianWorkLog> workLogs = this.technicianWorkLogRepository.findWorkLogsBetween(user.getId(),
                startDateTime, endDateTime);

        List<GroupedWorkLogsData> historyData = this.technicianWorkLogService.getTechnicianHistoryData(workLogs);

        // Add history work log data to the model
        model.addAttribute("workLogs", historyData);

        // Return the HTML template string.
        return "fragments/employee/history/history-table-data :: history-table-data";
    }

    @GetMapping("/employee/history/details")
    public String getEmployeeHistoryDetails(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        return "users/employee/history/details";
    }

    @PostMapping("/employee/clock")
    @ResponseBody
    public ResponseEntity<?> clock(@RequestBody TechnicianWorkLog clockData, HttpServletRequest request) {
    User user = (User) request.getSession().getAttribute("user");
    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }
    clockData.setTechnicianId(user.getId());
    TechnicianWorkLog savedLog = technicianWorkLogRepository.save(clockData);
    return ResponseEntity.ok(savedLog);
}
}
