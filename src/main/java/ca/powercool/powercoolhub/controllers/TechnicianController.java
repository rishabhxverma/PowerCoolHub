package ca.powercool.powercoolhub.controllers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
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
import ca.powercool.powercoolhub.services.MailService;
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

    @Autowired
    private MailService mailService;

    @Value("${google.api.key}")
    private String mapsApiKey;

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

    @PostMapping("/technician/clock")
    @ResponseBody
    public ResponseEntity<?> clock(@RequestBody TechnicianWorkLog clockData, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        //logic to determine if the technician forgot to clock out at a reasonable time. 
        if("clock_out".equals(clockData.getAction())){
            LocalDateTime clockOutTime = clockData.getCreatedAt();
            TechnicianWorkLog latestLog = technicianWorkLogService.latestLogById(user.getId());
            Duration twelveHours = Duration.ofHours(12);
            Duration duration = Duration.between(latestLog.getCreatedAt(),clockOutTime);
            if(duration.compareTo(twelveHours) > 0){
                mailService.notifyManagersOfLateClockOutTime(user.getId(), duration, clockOutTime);
            }
        }
        TechnicianWorkLog savedLog = this.technicianWorkLogService.saveWorkLog(user, clockData);
        return ResponseEntity.ok(savedLog);
    }

    @PostMapping("/technician/checkLocation/{techId}")
    public ResponseEntity<?> checkLocation(@PathVariable("techId") Long techId, @RequestBody Map<String, Double> locationDetails) {
        
        double latitude = locationDetails.get("latitude");
        double longitude = locationDetails.get("longitude");

        // // Now, invoke the service method to process the location and check if it's within range
        try {
            boolean isWithinRange = technicianService.isTechnicianWithinRange(techId, latitude, longitude);
    
            if (!isWithinRange) {
                String clockOutAddress = technicianService.getLastClockOutAddress(techId);
                mailService.notifyManagersOfOutOfRangeClockOut(techId, clockOutAddress);
                return ResponseEntity.ok().body("{\"message\":\"Technician clocked out outside of required range\"}");
            }
    
            return ResponseEntity.ok().body("{}");
        } catch (ServiceException ex) {
            // Log the error or do additional handling
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"" + ex.getMessage() + "\"}");
        }
    }

    @GetMapping("/technician/api-key")
    public ResponseEntity<String> getMapsApiKey() {
        return ResponseEntity.ok(mapsApiKey);
    }   

}
