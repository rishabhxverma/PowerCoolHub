package ca.powercool.powercoolhub.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.technician.data.GroupedWorkLogsData;
import ca.powercool.powercoolhub.models.technician.data.WorkLogsFilter;
import ca.powercool.powercoolhub.services.TechnicianWorkLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

@Controller
public class TechnicianController {

    @Autowired
    private TechnicianWorkLogService technicianWorkLogService;

    @GetMapping("/technician")
    public String getTechnicianDashboard(HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        User user = (User) request.getSession().getAttribute("user");

        // Pass model attribute to the view.
        model.addAttribute("user", user);

        return "users/technician/dashboard";
    }

    @GetMapping("/technician/history")
    public String getTechnicianHistory(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        List<GroupedWorkLogsData> workLogs = this.technicianWorkLogService.getTechnicianHistoryData(user,
                WorkLogsFilter.BY_MONTH);

        // Pass model attribute to the view.
        model.addAttribute("user", user);
        model.addAttribute("workLogs", workLogs);

        return "users/technician/history";
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
    @GetMapping("/technician/history/filter")
    public String filterHistory(@RequestParam("by") String filter, HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        List<GroupedWorkLogsData> workLogs = this.technicianWorkLogService.getTechnicianHistoryData(user, filter);

        // Add history work log data to the model
        model.addAttribute("workLogs", workLogs);

        // Return the HTML template string.
        return "fragments/technician/history/history-table-data :: history-table-data";
    }

    @GetMapping("/technician/history/{date}")
    public String getTechnicianHistoryDetails(@PathVariable("date") String date, HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        GroupedWorkLogsData workLogsData = this.technicianWorkLogService.getTechnicianWorkLogByDate(user, date);
    
        // Pass model attribute to the view.
        model.addAttribute("user", user);
        model.addAttribute("workLogsData", workLogsData);

        return "users/technician/history/details";
    }
}
