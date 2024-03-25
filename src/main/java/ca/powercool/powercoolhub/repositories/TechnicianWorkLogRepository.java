package ca.powercool.powercoolhub.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ca.powercool.powercoolhub.models.technician.TechnicianWorkLog;

@Repository
public interface TechnicianWorkLogRepository extends JpaRepository<TechnicianWorkLog, Long> {
    @Query("SELECT twl FROM TechnicianWorkLog twl WHERE twl.technicianId = :userId")
    List<TechnicianWorkLog> findWorkLogsByUserId(Long userId);

    @Query("SELECT twl FROM TechnicianWorkLog twl " +
            "WHERE twl.createdAt BETWEEN :startDate AND :endDate " +
            "AND twl.technicianId = :userId")
    List<TechnicianWorkLog> findWorkLogsBetween(Long userId, LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("SELECT twl FROM TechnicianWorkLog twl WHERE twl.technicianId = :userId ORDER BY twl.createdAt DESC LIMIT 1")
    TechnicianWorkLog findLatestWorkLogByUserId(Long userId);
    
    @Query("SELECT twl FROM TechnicianWorkLog twl WHERE DATE(twl.createdAt) = :date AND twl.technicianId = :userId")
    List<TechnicianWorkLog> findWorkLogsByDate(Long userId, LocalDate date);
}