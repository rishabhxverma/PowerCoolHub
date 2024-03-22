package ca.powercool.powercoolhub.models.technician.data;

import java.time.LocalDate;
import java.util.List;

import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;

public class GroupedWorkLogsData {
    private LocalDate date;
    private List<TechnicianWorkLog> logs;
    private float minutes;

    public GroupedWorkLogsData(LocalDate date, List<TechnicianWorkLog> logs, Long minutes) {
        this.date = date;
        this.logs = logs;
        this.minutes = minutes;
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

    @Override
    public String toString() {
        return "GroupedWorkLogsData{" +
                "date=" + date +
                ", logs=" + logs +
                ", minutes=" + minutes +
                '}';
    }
}
