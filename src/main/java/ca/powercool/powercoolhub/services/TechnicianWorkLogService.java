package ca.powercool.powercoolhub.services;

import java.util.List;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.models.technician.data.GroupedWorkLogsData;

public interface TechnicianWorkLogService {
    List<GroupedWorkLogsData> getTechnicianHistoryData(User user, String filter);

    GroupedWorkLogsData getTechnicianWorkLogByDate(User user, String date);

    String getClockState(User user);

    TechnicianWorkLog saveWorkLog(User user, TechnicianWorkLog clockData);
}
