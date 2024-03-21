package ca.powercool.powercoolhub.models;

import java.sql.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Basic data about each customer
    private String name;
    private String address;
    private String email;
    private String phoneNumber;
    private String notes;
    private Date lastServiced;
    private Date nextService;

    /*
     * The state of the customer, which can be one of the following: ARCHIVED (job finished and no future jobs planned)
     * UPCOMING_APPOINTMENT_SERVICE, UPCOMING_APPOINTMENT_INSTALL,
     * UPCOMING_APPOINTMENT_REPAIR
     * 
     */
    public enum CustomerState {
        ARCHIVED,
        UPCOMING_APPOINTMENT_SERVICE,
        UPCOMING_APPOINTMENT_INSTALL,
        UPCOMING_APPOINTMENT_REPAIR
    }

    @Enumerated(EnumType.STRING)
    private CustomerState state;
    public Customer() {
    }

    public Customer(Integer id, String name, String address, String phoneNumber, String notes, Date lastServiced,
            Date nextService, CustomerState state) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.notes = notes;
        this.lastServiced = lastServiced;
        this.nextService = nextService;
        this.state = state;
    }

    // Constructor for adding a new customer, no id, no last serviced, no next
    public Customer(String name, String address, String phoneNumber, String notes) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.notes = notes;
        this.state = CustomerState.UPCOMING_APPOINTMENT_SERVICE; // Default state for new customers
    }

    // Getters and setters

    // Other methods

    public boolean isArchived() {
        return state == CustomerState.ARCHIVED;
    }

    public boolean hasUpcomingAppointment() {
        return state != CustomerState.ARCHIVED;
    }

    public boolean requestingAppointment() {
        return (state == CustomerState.ARCHIVED && nextService == null);
    }


    public void archive() {
        state = CustomerState.ARCHIVED;
        nextService = null;
    }



    public String getStateString() {
        switch (state) {
            case ARCHIVED:
                return "Archived";
            case UPCOMING_APPOINTMENT_SERVICE:
                return "Upcoming Service";
            case UPCOMING_APPOINTMENT_INSTALL:
                return "Upcoming Install";
            case UPCOMING_APPOINTMENT_REPAIR:
                return "Upcoming Repair";
            default:
                return "Unknown";
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getLastServiced() {
        return lastServiced;
    }

    public void setLastServiced(Date lastServiced) {
        this.lastServiced = lastServiced;
    }

    public Date getNextService() {
        return nextService;
    }

    public void setNextService(Date nextService) {
        this.nextService = nextService;
    }

    public CustomerState getState() {
        return state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }
}
