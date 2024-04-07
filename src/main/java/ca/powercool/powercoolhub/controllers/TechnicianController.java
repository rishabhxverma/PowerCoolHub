package ca.powercool.powercoolhub.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.powercool.powercoolhub.models.Job;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.models.technician.data.GroupedWorkLogsData;
import ca.powercool.powercoolhub.models.technician.data.WorkLogsFilter;
import ca.powercool.powercoolhub.services.TechnicianService;
import ca.powercool.powercoolhub.services.TechnicianWorkLogService;
import ca.powercool.powercoolhub.utilities.LocalDateTimeUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

@Controller
public class TechnicianController {

    @Autowired
    private TechnicianWorkLogService technicianWorkLogService;

    @Autowired
    private TechnicianService technicianService;

    @GetMapping("/technician")
    public String getTechnicianDashboard(HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        LocalDate startDate = LocalDateTimeUtility.getFirstDayOfWeek(LocalDateTime.now()).toLocalDate();
        LocalDate endDate = LocalDateTimeUtility.getLastDayOfWeek(LocalDateTime.now()).toLocalDate();

        List<Job> upcomingJobs = this.technicianService.getUpcomingJobs(user, startDate.toString(), endDate.toString());
        String clockState = this.technicianWorkLogService.getClockState(user);
        
        // Pass model attribute to the view.
        model.addAttribute("upcomingJobs", upcomingJobs);
        model.addAttribute("clockButtonState", clockState);
        model.addAttribute("user", user);

        return "users/technician/dashboard";
    }

    @GetMapping("/technician/history")
    public String getTechnicianHistory(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        List<GroupedWorkLogsData> workLogs = this.technicianWorkLogService.getTechnicianHistoryData(user,
                WorkLogsFilter.BY_MONTH);

        String clockState = this.technicianWorkLogService.getClockState(user);
        model.addAttribute("clockButtonState", clockState);
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
        String clockState = this.technicianWorkLogService.getClockState(user);
        model.addAttribute("clockButtonState", clockState);

        // Return the HTML template string.
        return "fragments/technician/history/history-table-data :: history-table-data";
    }

    @GetMapping("/technician/history/{date}")
    public String getTechnicianHistoryDetails(@PathVariable("date") String date, HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        GroupedWorkLogsData workLogsData = this.technicianWorkLogService.getTechnicianWorkLogByDate(user, date);
        String clockState = this.technicianWorkLogService.getClockState(user);
        model.addAttribute("clockButtonState", clockState);
    
        // Pass model attribute to the view.
        model.addAttribute("user", user);
        model.addAttribute("workLogsData", workLogsData);

        return "users/technician/history/details";
    }

    // @GetMapping("/technician/getAddress/{techId}")
    // public ResponseEntity<?> getLatestAddress(@PathVariable("techId") Long techId) {
    //     try {
    //         String latestAddress = this.technicianService.getLatestCompletedJobAddress(techId);
    //         if (latestAddress != null && !latestAddress.isEmpty()) {
    //             return ResponseEntity.ok(latestAddress);
    //         } else {
    //             // Handle the case where the address is null or empty
    //             return ResponseEntity.notFound().build();
    //         }
    //     } catch (Exception e) {
    //         // Log the exception details and return a proper error message or code
    //         // Depending on your logging framework, adjust the following line
    //         // e.g., log.error("Error fetching latest address", e);
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching latest address");
    //     }
    // }

    @PostMapping("/technician/clock")
    @ResponseBody
    public ResponseEntity<?> clock(@RequestBody TechnicianWorkLog clockData, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        TechnicianWorkLog savedLog = this.technicianWorkLogService.saveWorkLog(user, clockData);
        return ResponseEntity.ok(savedLog);
    }

    @PostMapping("/technician/checkLocation/{techId}")
    public ResponseEntity<?> checkLocation(@PathVariable("techId") Long techId, @RequestBody Map<String, Double> locationDetails) {
        double latitude = locationDetails.get("latitude");
        double longitude = locationDetails.get("longitude");
        
        // // Now, invoke the service method to process the location and check if it's within range
        boolean isWithinRange = technicianService.isTechnicianWithinRange(
            techId,
            latitude,
            longitude
        );
        //I dont have this set up yet so I have it commented out. Eventually we will invoke the mailService
        // If it's out of range, send an email
        // if (!isWithinRange) {
        //     mailService.notifyManagersOfOutOfRangeClockOut(technicianId);
        // }

        // Return appropriate HTTP response
        return isWithinRange 
            ? ResponseEntity.ok().build()
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Technician clocked out outside of required range");
    }

}
