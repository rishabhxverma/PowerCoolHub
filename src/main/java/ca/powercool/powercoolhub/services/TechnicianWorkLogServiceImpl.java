package ca.powercool.powercoolhub.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.models.technician.data.GroupedWorkLogsData;

@Service
public class TechnicianWorkLogServiceImpl implements TechnicianWorkLogService {
    @Override
    public List<GroupedWorkLogsData> getTechnicianHistoryData(List<TechnicianWorkLog> workLogs) {
        // Group work logs by LocalDate
        Map<LocalDate, List<TechnicianWorkLog>> groupedLogs = workLogs.stream()
                .collect(Collectors.groupingBy(log -> log.getCreatedAt().toLocalDate()));

        // Process grouped logs to calculate hours
        List<GroupedWorkLogsData> groupedWorkLogsData = groupedLogs.entrySet().stream()
                .map(entry -> {
                    List<TechnicianWorkLog> logs = entry.getValue();
                    // Long sumOfDurations = this.calculateSumOfDurations(logs);
                    LocalDate date = entry.getKey();
                    return new GroupedWorkLogsData(date, logs);
                })
                .collect(Collectors.toList());

        return groupedWorkLogsData;
    }

    /**
     * Sum up the time difference (duration) between `clock-in` and `clock-out`.
     * 
     * @param logs
     * @return Long minutes
     */
    // private Long calculateSumOfDurations(List<TechnicianWorkLog> logs) {
    //     List<LocalDateTime> clockInTimes = new ArrayList<>();
    //     List<LocalDateTime> clockOutTimes = new ArrayList<>();

    //     // Separate clock-in and clock-out times
    //     for (int i = 0; i < logs.size(); i++) {
    //         if (logs.get(i).getAction().equals(TechnicianWorkLog.CLOCK_IN)) {
    //             clockInTimes.add(logs.get(i).getCreatedAt());
    //         } else if (logs.get(i).getAction().equals(TechnicianWorkLog.CLOCK_OUT)) {
    //             clockOutTimes.add(logs.get(i).getCreatedAt());
    //         }
    //     }

    //     // Calculate sum of durations between clock-in and clock-out times
    //     Long sumOfDurations = 0L;
    //     for (int i = 0; i < Math.min(clockInTimes.size(), clockOutTimes.size()); i++) {
    //         Duration duration = Duration.between(clockInTimes.get(i), clockOutTimes.get(i));
    //         sumOfDurations += duration.toMinutes();
    //     }

    //     return sumOfDurations;
    // }
}