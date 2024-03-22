package ca.powercool.powercoolhub.services;

import java.util.List;
import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;
import ca.powercool.powercoolhub.models.technician.data.GroupedWorkLogsData;

public interface TechnicianWorkLogService {
    List<GroupedWorkLogsData> getTechnicianHistoryData(List<TechnicianWorkLog> workLogs);
}
