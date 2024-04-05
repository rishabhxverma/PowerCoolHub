package ca.powercool.powercoolhub.models.technician.data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;

public class GroupedWorkLogsData {
    public final int BREAK_TIME = 45;
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
     * @return formatted date string, ex: "March 23, 2024"
     */
    public String getFormattedDate() {
        return this.date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    }

    /**
     * @return formatted date URI string, ex: "2024-03-23"
     */
    public String getDateURI() {
        return this.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Returns a formatted string representing the duration in hours and minutes.
     * If the duration is less than one hour, only the minutes are displayed.
     * If the duration is a whole hour, only the hours are displayed.
     * If the duration is greater than one hour, both hours and remaining minutes
     * are displayed.
     *
     * @return A formatted string representing the duration.
     */
    public String getFormattedDuration() {
        // Calculate the total hours and remaining minutes
        int hours = (int) this.minutes / 60;
        int remainingMinutes = (int) minutes % 60;

        // If the duration is less than one hour, display only the minutes
        if (hours == 0) {
            return remainingMinutes + " mins";
        }
        // If the duration is a whole hour, display only the hours
        else if (remainingMinutes == 0) {
            return hours + " hours";
        }
        // If the duration is greater than one hour, display both hours and remaining
        // minutes
        else {
            return hours + " hours, " + remainingMinutes + " mins";
        }
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

        // Ensures sumOfDurations > 0
        return (Long) ((sumOfDurations - BREAK_TIME > 0) ? sumOfDurations - BREAK_TIME > 0 : 0);
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
