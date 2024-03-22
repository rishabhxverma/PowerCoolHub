package ca.powercool.powercoolhub.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.models.technician.data.GroupedWorkLogsData;
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

        // Pass model attribute to the view.
        model.addAttribute("user", user);

        return "users/employee/dashboard";
    }

    @GetMapping("/employee/history")
    public String getEmployeeHistory(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = LocalDateTimeUtility.getFirstDayOfWeek(currentDateTime);
        LocalDateTime endDateTime = LocalDateTimeUtility.getLastDayOfWeek(currentDateTime);

        List<TechnicianWorkLog> workLogs = this.technicianWorkLogRepository.findWorkLogsBetween(user.getId(),
                startDateTime, endDateTime);

        List<GroupedWorkLogsData> historyData = this.technicianWorkLogService.getTechnicianHistoryData(workLogs);

        // Pass model attribute to the view.
        model.addAttribute("user", user);
        model.addAttribute("workLogs", historyData);

        return "users/employee/history";
    }

    @GetMapping("/employee/history/details")
    public String getEmployeeHistoryDetails(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        // Pass model attribute to the view.
        model.addAttribute("user", user);

        return "users/employee/history/details";
    }
}
