package ca.powercool.powercoolhub.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;

@Service
public class TechnicianWorkLogServiceImpl implements TechnicianWorkLogService {
    /**
     * Group a list of TechnicianWorkLog by localDate.
     * Ex:
     * "2024-03-21" => { a list of TechnicianWorkLog objects }
     * "2024-03-20" => { a list of TechnicianWorkLog objects }
     * ...
     * 
     * @return a mapped LocalDate string to a list of TechnicianWorkLog.
     */
    @Override
    public Map<LocalDate, List<TechnicianWorkLog>> groupWorkLogsByDate(List<TechnicianWorkLog> workLogs) {
        return workLogs.stream()
                .collect(Collectors.groupingBy(log -> log.getCreatedAt().toLocalDate()));
    }
}