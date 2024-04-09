package ca.powercool.powercoolhub.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    public void downloadTechCSV(@RequestParam Map<String, String> form, @RequestParam String[] technicianIds, HttpServletResponse response) throws IOException {
        String startDate = form.get("start-date");
        String endDate = form.get("end-date");
        List<Long> techIds = new ArrayList<>();
        
        System.out.println(technicianIds);
        
        for (String id : technicianIds) {
            techIds.add(Long.parseLong(id));
        }
        // Set the response content type for a zip file
        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);

        // Define a filename for the zip file
        String zipFileName = "R" + startDate + "_" + endDate + ".zip";

        // Set the Content-Disposition header so the zip file is downloaded
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
            .filename(zipFileName)
            .build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        // Create a ZipOutputStream to write to the response output stream
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            // Iterate over each technician ID
            for (Long id : techIds) {
                Optional<User> techOpt = userRepository.findById(id);
                if (!techOpt.isPresent()) {
                    continue; // Skip if the technician is not found
                }
                User tech = techOpt.get();

                List<TechnicianWorkLogReport> workLogs = technicianWorkLogService.getTechnicianWorkLogReport(tech, startDate, endDate);

                // Generate the file name for the individual CSV file
                String[] nameParts = tech.getName().split("\\s+");
                String firstName = nameParts[0];
                String lastNameInitial = nameParts.length > 1 ? nameParts[nameParts.length - 1].substring(0, 1) : "";
                String fileName = String.format("%s%s_%s_%s.csv", firstName, lastNameInitial, startDate, endDate);

                // Add a new zip entry to the ZipOutputStream for the CSV file
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);

                // Define CSV header (the column titles)
                String[] csvHeader = {"Name", "Date", "Clock In Time", "Clock Out Time", "Hours", "Bad Data"};
                
                // Use StringWriter and CSVPrinter to write the CSV content
                try (StringWriter stringWriter = new StringWriter();
                    CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT.withHeader(csvHeader))) {

                    // Write CSV rows for each workLog
                    for (TechnicianWorkLogReport workLog : workLogs) {
                        boolean error = false;
                        String hoursStr = workLog.getHours();

                        if (hoursStr.startsWith("-")) {
                            error = true;
                        } 
                        else {
                            String[] parts = hoursStr.split(" ");
                            if (parts.length > 1 && parts[1].equalsIgnoreCase("hours")) {
                                try {
                                    int hours = Integer.parseInt(parts[0]);
                                    if (hours > 12) { // @MAX_HOURS_PER_DAY
                                        error = true;
                                    }
                                } catch (NumberFormatException e) {
                                    // Handle the case where the number is not properly formatted
                                    error = true;
                                }
                            }
                        }

                        csvPrinter.printRecord(
                            workLog.getTechnicianName(),
                            workLog.getDate().toString(),
                            workLog.getClockInTime(),
                            workLog.getClockOutTime(),
                            hoursStr,
                            error ? "*****" : ""
                        );
                    }
                    // Ensure all data is written to the stringWriter
                    csvPrinter.flush();

                    // Write the CSV content to the ZipOutputStream
                    String csvContent = stringWriter.toString();
                    zipOut.write(csvContent.getBytes(StandardCharsets.UTF_8));
                }
                
                // Close the current entry
                zipOut.closeEntry();
            }
            // Finish zip process
            zipOut.finish();
        } catch (IOException e) {
            // Handle IOException
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
    

