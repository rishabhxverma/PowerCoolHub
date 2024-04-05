package ca.powercool.powercoolhub.models.report;

import java.time.LocalDate;

public class TechnicianWorkLogReport implements Comparable<TechnicianWorkLogReport> {
    private String technicianName;
    private LocalDate date;
    private String clockInTime;
    private String clockOutTime;
    private String hours;

    public TechnicianWorkLogReport(String technicianName, LocalDate date, String clockInTime, String clockOutTime, String hours) {
        this.technicianName = technicianName;
        this.date = date;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.hours = hours;
    }

    public String getTechnicianName() {
        return this.technicianName;
    }
    
    public LocalDate getDate() {
        return this.date;
    }
    
    public String getClockInTime() {
        return this.clockInTime;
    }
    
    public String getClockOutTime() {
        return this.clockOutTime;
    }
    
    public String getHours() {
        return this.hours;
    }

    @Override
    public String toString() {
        return this.technicianName + ", " + this.date + ", " + this.clockInTime + ", " + this.clockOutTime + ", " + this.hours;
    }

    @Override
    public int compareTo(TechnicianWorkLogReport other) {
        return other.date.compareTo(this.date);
    }
}
