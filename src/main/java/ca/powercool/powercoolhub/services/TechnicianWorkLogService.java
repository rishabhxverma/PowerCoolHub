package ca.powercool.powercoolhub.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;

public interface TechnicianWorkLogService {
    Map<LocalDate, List<TechnicianWorkLog>> groupWorkLogsByDate(List<TechnicianWorkLog> workLogs);
}
