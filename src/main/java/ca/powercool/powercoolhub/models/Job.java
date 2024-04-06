package ca.powercool.powercoolhub.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
    private LocalDate serviceDate;
    private String note;
    private String customerMessage; //customer message
    // technician ids assigned to this job
    @ElementCollection
    @CollectionTable(name = "job_technicians")
    @Column(name = "technician_id")
    private List<Integer> technicianIds;
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private JobType jobType;
    private boolean jobDone;
    private String customerName;
    private boolean paymentReceived;

    public enum JobType {
        SERVICE,
        INSTALL,
        REPAIR
    }

    public Job() {}

    public Job(Integer id, int customerId, LocalDate serviceDate, String note, JobType jobType, List<Integer> techIds, boolean jobDone,
            String customerName) {
        this.id = id;
        this.customerId = customerId;
        this.serviceDate = serviceDate;
        this.note = note;
        this.jobType = jobType;
        this.technicianIds = techIds;
        this.jobDone = jobDone;
    }
    
    public String getCustomerMessage() {
        return customerMessage;
    }

    public void setCustomerMessage(String customerMessage) {
        this.customerMessage = customerMessage;
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

    public LocalDate getServiceDate() {
        return this.serviceDate;
    }

    public void setServiceDate(LocalDate date) {
        this.serviceDate = date;
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
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return formatted date string, ex: "March 23, 2024"
     */
    public String getFormattedDate() {
        return this.serviceDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", serviceDate=" + serviceDate +
                ", note='" + note + '\'' +
                ", technicianIds=" + technicianIds +
                ", jobType=" + jobType +
                ", jobDone=" + jobDone +
                ", customerName='" + customerName + '\'' +
                ", paymentReceived=" + paymentReceived +
                '}';
    }

}
