package ca.powercool.powercoolhub.models;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int customerId;
    private Date serviceDate;
    private String note;
    // technician ids assigned to this job
    private List<Integer> technicianIds;
    private JobType jobType;
    private boolean jobDone;
    private boolean paymentReceived;

    public enum JobType {
        SERVICE,
        INSTALL,
        REPAIR
    }

    public Job() {}

    public Job(Integer id, int customerId, Date serviceDate, String note, JobType jobType, boolean jobDone) {
        this.id = id;
        this.customerId = customerId;
        this.serviceDate = serviceDate;
        this.note = note;
        this.jobType = jobType;
        this.jobDone = jobDone;
    }


    public boolean isActive() {
        return !jobDone;
    }

    public List<Integer> getTechnicianIds() {
        return technicianIds;
    }

    public void setTechnicianIds(List<Integer> technicianIds) {
        this.technicianIds = technicianIds;
    }

    public void addTechnicianById(int technicianId) {
        technicianIds.add(technicianId);
    }

    public boolean isPaymentReceived() {
        return paymentReceived;
    }

    public boolean pendingPayment() {
        return !paymentReceived;
    }

    public void setPaymentReceived(boolean paymentReceived) {
        this.paymentReceived = paymentReceived;
    }

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    //returns the job type as a string, not an enum, no Job
    public String getJobType() {
        return String.valueOf(jobType).toLowerCase();
    }

    public void setJobType(String jobType) {
        this.jobType = JobType.valueOf(jobType);
    }

    public boolean isJobDone() {
        return jobDone;
    }

    public void setJobDone(boolean jobDone) {
        this.jobDone = jobDone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
