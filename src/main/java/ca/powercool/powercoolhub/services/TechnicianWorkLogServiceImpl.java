package ca.powercool.powercoolhub.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.models.technician.data.GroupedWorkLogsData;
import ca.powercool.powercoolhub.models.technician.data.WorkLogsFilter;
import ca.powercool.powercoolhub.repositories.TechnicianWorkLogRepository;
import ca.powercool.powercoolhub.utilities.LocalDateTimeUtility;

@Service
public class TechnicianWorkLogServiceImpl implements TechnicianWorkLogService {

    @Autowired
    private TechnicianWorkLogRepository technicianWorkLogRepository;

    /**
     * Retrieves technician history data based on the provided filter.
     *
     * @param user   The user for whom to retrieve history data.
     * @param filter The filter to apply (e.g., "BY_WEEK", "BY_YEAR", "BY_MONTH").
     * @return A list of grouped work logs data.
     */
    @Override
    public List<GroupedWorkLogsData> getTechnicianHistoryData(User user, String filter) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime startDateTime, endDateTime;

        // Filter by weekly.
        if (filter.equals(WorkLogsFilter.BY_WEEK)) {
            startDateTime = LocalDateTimeUtility.getFirstDayOfWeek(currentDateTime);
            endDateTime = LocalDateTimeUtility.getLastDayOfWeek(currentDateTime);
        }
        // Filter by yearly.
        else if (filter.equals(WorkLogsFilter.BY_YEAR)) {
            startDateTime = LocalDateTimeUtility.getFirstDayOfYear(currentDateTime);
            endDateTime = LocalDateTimeUtility.getLastDayOfYear(currentDateTime);
        }
        // Filter by monthly.
        else {
            startDateTime = LocalDateTimeUtility.getFirstDayOfMonth(currentDateTime);
            endDateTime = LocalDateTimeUtility.getLastDayOfMonth(currentDateTime);
        }

        List<TechnicianWorkLog> workLogs = this.technicianWorkLogRepository.findWorkLogsBetween(user.getId(),
                startDateTime, endDateTime);

        List<GroupedWorkLogsData> historyData = this.groupWorkLogs(workLogs);

        return historyData;
    }

    /**
     * Groups the provided technician work logs by date.
     *
     * @param workLogs The list of technician work logs to group.
     * @return A list of grouped work logs data.
     */
    private List<GroupedWorkLogsData> groupWorkLogs(List<TechnicianWorkLog> workLogs) {
        // Group work logs by LocalDate
        Map<LocalDate, List<TechnicianWorkLog>> groupedLogs = workLogs.stream()
                .collect(Collectors.groupingBy(log -> log.getCreatedAt().toLocalDate()));

        // Process grouped logs to calculate hours
        List<GroupedWorkLogsData> groupedWorkLogsData = groupedLogs.entrySet().stream()
                .map(entry -> {
                    List<TechnicianWorkLog> logs = entry.getValue();
                    LocalDate date = entry.getKey();
                    return new GroupedWorkLogsData(date, logs);
                })
                .collect(Collectors.toList());

        return groupedWorkLogsData;
    }

    /**
     * Retrieves the work logs of a technician for a specific date.
     *
     * @param user The user object representing the technician.
     * @param date The date in string format (ISO-8601) for which work logs are to
     *             be retrieved.
     * @return GroupedWorkLogsData object containing the technician's work logs for
     *         the specified date.
     * @throws DateTimeParseException if the date string cannot be parsed into a
     *                                LocalDate object.
     */
    @Override
    public GroupedWorkLogsData getTechnicianWorkLogByDate(User user, String date) {
        LocalDate localDate = LocalDate.parse(date);

        // Group the work log, since we are getting a specific date.
        List<TechnicianWorkLog> workLogs = this.technicianWorkLogRepository.findWorkLogsByDate(user.getId(), localDate);

        // Get the first (and only) group of work logs since there exists only one
        // groupped work logs for a specific date.
        GroupedWorkLogsData workLogsData = this.groupWorkLogs(workLogs).get(0);

        return workLogsData;
    }

    @Override
    public String getClockState(User user) {
        TechnicianWorkLog latestLog = this.technicianWorkLogRepository.findLatestClockWorkLogByUserId(user.getId());

        String clockState = TechnicianWorkLog.CLOCK_IN;

        if (latestLog != null) {
            clockState = latestLog.getAction().equals(TechnicianWorkLog.CLOCK_OUT) ? TechnicianWorkLog.CLOCK_IN
                    : TechnicianWorkLog.CLOCK_OUT;
        }
        return clockState;
    }

    @Override
    public TechnicianWorkLog saveWorkLog(User user, TechnicianWorkLog clockData) {
        clockData.setTechnicianId(user.getId());
        TechnicianWorkLog savedLog = technicianWorkLogRepository.save(clockData);
        return savedLog;
    }
}