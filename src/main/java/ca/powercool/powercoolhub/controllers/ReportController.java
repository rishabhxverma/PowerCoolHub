package ca.powercool.powercoolhub.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.LocalDate;


import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.report.TechnicianWorkLogReport;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.repositories.TechnicianWorkLogRepository;
import ca.powercool.powercoolhub.repositories.UserRepository;
import ca.powercool.powercoolhub.services.TechnicianWorkLogService;
import ca.powercool.powercoolhub.services.TechnicianWorkLogServiceImpl;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping("/manager/report")
public class ReportController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    TechnicianWorkLogService technicianWorkLogService;

    
    @GetMapping("")
    public String getReportPage(Model model){
        List<User> techs = userRepository.findByRole("technician");
        model.addAttribute("technicians", techs);

        return "users/manager/report";
    }

    @PostMapping("/downloadTechCSV")
    public void downloadTechCSV(@RequestParam Map<String, String> form, HttpServletResponse response) throws IOException {
        String startDate = form.get("start-date");
        String endDate = form.get("end-date");
        Long techId = Long.parseLong(form.get("tech-select"));
        Optional<User> techOpt = userRepository.findById(techId);

        if (!techOpt.isPresent()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        User tech = techOpt.get();

        List<TechnicianWorkLogReport> workLogs = technicianWorkLogService.getTechnicianWorkLogReport(tech, startDate, endDate);


        String fileName;
        // Split the technician's name to extract the first name and the first letter of the last name
        String[] nameParts = tech.getName().split("\\s+");
        String firstName = nameParts[0];
        String lastNameInitial = nameParts.length > 1 ? nameParts[nameParts.length - 1].substring(0, 1) : "";

        fileName = String.format("%s%s_%s_%s.csv", firstName, lastNameInitial, startDate, endDate);

        response.setContentType("text/csv");
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(fileName)
                .build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        // Define CSV header (the column titles)
        String[] csvHeader = {" Name", "Date", "Clock In Time", "Clock Out Time", "Hours"};

        // Write the CSV to PrintWriter
        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader(csvHeader))) {
            // Write CSV rows for each workLog
            for (TechnicianWorkLogReport workLog : workLogs) {
                csvPrinter.printRecord(
                    workLog.getTechnicianName(),
                    workLog.getDate().toString(),
                    workLog.getClockInTime(),
                    workLog.getClockOutTime(),
                    workLog.getHours()
                );
            }
        }
    }

    // @PostMapping("/downloadAllCSV")
    // public void downloadAllCSV(@RequestParam Map<String, String> form, HttpServletResponse response) throws IOException {
    //     String startDate = form.get("start-date");
    //     String endDate = form.get("end-date");
    //     Long techId = Long.parseLong(form.get("tech-select"));
    //     Optional<User> techOpt = userRepository.findById(techId);

    //     if (!techOpt.isPresent()) {
    //         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    //         return;
    //     }
    //     User tech = techOpt.get();

    //     List<TechnicianWorkLogReport> workLogs = technicianWorkLogService.getTechnicianWorkLogReport(tech, startDate, endDate);

    //     String fileName;
    //     // Split the technician's name to extract the first name and the first letter of the last name
    //     String[] nameParts = tech.getName().split("\\s+");
    //     String firstName = nameParts[0];
    //     String lastNameInitial = nameParts.length > 1 ? nameParts[nameParts.length - 1].substring(0, 1) : "";

    //     fileName = String.format("%s%s%s%s%s.csv", firstName, lastNameInitial, startDate, "_", endDate);

    //     response.setContentType("text/csv");
    //     ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
    //             .filename(fileName)
    //             .build();
    //     response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

    //     // Define CSV header (the column titles)
    //     String[] csvHeader = {" Name", "Date", "Clock In Time", "Clock Out Time", "Hours"};

    //     // Write the CSV to PrintWriter
    //     try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader(csvHeader))) {
    //         // Write CSV rows for each workLog
    //         for (TechnicianWorkLogReport workLog : workLogs) {
    //             csvPrinter.printRecord(
    //                 workLog.getTechnicianName(),
    //                 workLog.getDate().toString(),
    //                 workLog.getClockInTime(),
    //                 workLog.getClockOutTime(),
    //                 workLog.getHours()
    //             );
    //         }
    //     }
    // }
}
    

