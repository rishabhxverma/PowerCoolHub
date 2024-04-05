package ca.powercool.powercoolhub.technician;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import ca.powercool.powercoolhub.repositories.TechnicianWorkLogRepository;
import ca.powercool.powercoolhub.utilities.LocalDateTimeUtility;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.models.technician.data.GroupedWorkLogsData;

@SpringBootTest
public class TechnicianWorkLogTest {

    @Mock
    private TechnicianWorkLogRepository workLogRepository;

    @Test
    public void testGetFirstDayOfWeek() {
        // Given
        TechnicianWorkLog workLog = createWorkLog("clock-in", "2024-03-20T00:00:00.000");

        // When
        LocalDateTime firstDayOfWeek = LocalDateTimeUtility.getFirstDayOfWeek(workLog.getCreatedAt());

        // Then
        LocalDateTime expectedFirstDay = LocalDateTime.parse("2024-03-17T00:00:00.000");
        assertEquals(expectedFirstDay, firstDayOfWeek);
        assertEquals(DayOfWeek.SUNDAY, firstDayOfWeek.getDayOfWeek());
    }

    @Test
    public void testGetLastDayOfWeek() {
        // Given
        TechnicianWorkLog workLog = createWorkLog("clock-in", "2024-03-20T00:00:00.000");

        // When
        LocalDateTime lastDayOfWeek = LocalDateTimeUtility.getLastDayOfWeek(workLog.getCreatedAt());

        // Then
        LocalDateTime expectedLastDay = LocalDateTime.parse("2024-03-23T23:59:59.999999999");
        assertEquals(expectedLastDay, lastDayOfWeek);
        assertEquals(DayOfWeek.SATURDAY, lastDayOfWeek.getDayOfWeek());
    }

    @Test
    public void testGetFirstDayOfMonth() {
        // Given
        TechnicianWorkLog workLog = createWorkLog("clock-in", "2024-03-20T00:00:00.000");

        // When
        LocalDateTime firstDayOfMonth = LocalDateTimeUtility.getFirstDayOfMonth(workLog.getCreatedAt());

        // Then
        LocalDateTime expectedFirstDay = LocalDateTime.parse("2024-03-01T00:00:00.000");
        assertEquals(expectedFirstDay, firstDayOfMonth);
        assertEquals(1, firstDayOfMonth.getDayOfMonth());
    }

    @Test
    public void testGetLastDayOfMonth() {
        // Given
        TechnicianWorkLog workLog = createWorkLog("clock-in", "2024-03-20T00:00:00.000");

        // When
        LocalDateTime lastDayOfMonth = LocalDateTimeUtility.getLastDayOfMonth(workLog.getCreatedAt());

        // Then
        LocalDateTime expectedLastDay = LocalDateTime.parse("2024-03-31T23:59:59.999999999");
        assertEquals(expectedLastDay, lastDayOfMonth);
        assertEquals(31, lastDayOfMonth.getDayOfMonth());
    }

    @Test
    public void testGetFirstDayOfYear() {
        // Given
        TechnicianWorkLog workLog = createWorkLog("clock-in", "2024-03-20T00:00:00.000");

        // When
        LocalDateTime firstDayOfYear = LocalDateTimeUtility.getFirstDayOfYear(workLog.getCreatedAt());

        // Then
        LocalDateTime expectedFirstDay = LocalDateTime.parse("2024-01-01T00:00:00.000");
        assertEquals(expectedFirstDay, firstDayOfYear);
        assertEquals(1, firstDayOfYear.getDayOfYear());
    }

    @Test
    public void testGetLastDayOfYear() {
        // Given
        TechnicianWorkLog workLog = createWorkLog("clock-in", "2024-03-20T00:00:00.000");

        // When
        LocalDateTime lastDayOfYear = LocalDateTimeUtility.getLastDayOfYear(workLog.getCreatedAt());

        // Then
        LocalDateTime expectedLastDay = LocalDateTime.parse("2024-12-31T23:59:59.999999999");
        assertEquals(expectedLastDay, lastDayOfYear);
        assertEquals(366, lastDayOfYear.getDayOfYear()); // Leap year
    }

    @Test
    public void testCalculateSumOfDurations() {
        // Given
        List<TechnicianWorkLog> workLogs = new ArrayList<>();
        
        // 3 hours
        workLogs.add(createWorkLog("clock-in", "2024-03-20T01:00:00.000"));
        workLogs.add(createWorkLog("clock-out", "2024-03-20T04:00:00.000"));

        // 3 hours
        workLogs.add(createWorkLog("clock-in", "2024-03-20T14:00:00.000"));
        workLogs.add(createWorkLog("clock-out", "2024-03-20T17:00:00.000"));
        
        // 2 hours
        workLogs.add(createWorkLog("clock-in", "2024-03-20T19:00:00.000"));
        workLogs.add(createWorkLog("clock-out", "2024-03-20T21:00:00.000"));
        
        GroupedWorkLogsData groupedWorkLogsData = new GroupedWorkLogsData(LocalDateTime.now().toLocalDate(), workLogs);

        // When
        Long sumOfDurations = groupedWorkLogsData.calculateSumOfDurations();

        // Then
        // 3 + 3 + 2 = 8 hours * 60 mins = 480 mins.
        assertEquals(480, sumOfDurations);
    }

    private TechnicianWorkLog createWorkLog(String action, String createdAt) {
        TechnicianWorkLog workLog = new TechnicianWorkLog();
        workLog.setAction(action);
        workLog.setCreatedAt(LocalDateTime.parse(createdAt));
        return workLog;
    }
}