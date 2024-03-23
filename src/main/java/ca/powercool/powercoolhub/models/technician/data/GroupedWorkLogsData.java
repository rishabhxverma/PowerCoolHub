package ca.powercool.powercoolhub.models.technician.data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;

public class GroupedWorkLogsData {
    private LocalDate date;
    private List<TechnicianWorkLog> logs;
    private float minutes;

    public GroupedWorkLogsData(LocalDate date, List<TechnicianWorkLog> logs) {
        this.date = date;
        this.logs = logs;
        this.minutes = (float) this.calculateSumOfDurations();
    }

    public LocalDate getDate() {
        return this.date;
    }

    public List<TechnicianWorkLog> getLogs() {
        return this.logs;
    }

    public float getMinutes() {
        return this.minutes;
    }

    /**
     * Sum up the time difference (duration) between `clock-in` and `clock-out`.
     * 
     * @param logs
     * @return Long minutes
     */
    public Long calculateSumOfDurations() {
        List<LocalDateTime> clockInTimes = new ArrayList<>();
        List<LocalDateTime> clockOutTimes = new ArrayList<>();

        // Separate clock-in and clock-out times
        for (int i = 0; i < this.logs.size(); i++) {
            if (logs.get(i).getAction().equals(TechnicianWorkLog.CLOCK_IN)) {
                clockInTimes.add(logs.get(i).getCreatedAt());
            } else if (logs.get(i).getAction().equals(TechnicianWorkLog.CLOCK_OUT)) {
                clockOutTimes.add(logs.get(i).getCreatedAt());
            }
        }

        // Calculate sum of durations between clock-in and clock-out times
        Long sumOfDurations = 0L;
        for (int i = 0; i < Math.min(clockInTimes.size(), clockOutTimes.size()); i++) {
            Duration duration = Duration.between(clockInTimes.get(i), clockOutTimes.get(i));
            sumOfDurations += duration.toMinutes();
        }

        return sumOfDurations;
    }

    @Override
    public String toString() {
        return "GroupedWorkLogsData{" +
                "date=" + date +
                ", logs=" + logs +
                ", minutes=" + minutes +
                '}';
    }
}
