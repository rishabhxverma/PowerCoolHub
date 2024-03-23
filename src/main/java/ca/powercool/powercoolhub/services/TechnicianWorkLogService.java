package ca.powercool.powercoolhub.services;

import java.util.List;

import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.technician.data.GroupedWorkLogsData;

public interface TechnicianWorkLogService {
    List<GroupedWorkLogsData> getTechnicianHistoryData(User user, String filter);
}
