package ca.powercool.powercoolhub.models;

import java.time.LocalDate;

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
    private LocalDate lastServiced;
    private LocalDate nextService;

    /*
     * The state of the customer, which can be one of the following: ARCHIVED (job finished and no future jobs planned)
     * UPCOMING or ARCHIVED or REQUESTING APPOINTMENT
     * 
     */
    public enum CustomerState {
        ARCHIVED,
        UPCOMING,
        REQUESTING_APPOINTMENT;
    }

    @Enumerated(EnumType.STRING)
    private CustomerState state;
    public Customer() {
    }

    public Customer(Integer id, String name, String address, String phoneNumber, String notes, LocalDate lastServiced,
    LocalDate nextService, CustomerState state) {
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
    public Customer(String name, String address, String phoneNumber, String email, CustomerState state, String notes) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.notes = notes;
        this.state = state; // Default state for new customers
    }

    // Getters and setters

    // Other methods

    public boolean isArchived() {
        return state == CustomerState.ARCHIVED;
    }

    public boolean hasUpcomingAppointment() {
        return state == CustomerState.UPCOMING;
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
            case UPCOMING:
                return "Upcoming";
            case REQUESTING_APPOINTMENT:
                return "Requesting Appointment";
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

    public LocalDate getLastServiced() {
        return lastServiced;
    }

    public void setLastServiced(LocalDate lastServiced) {
        this.lastServiced = lastServiced;
    }

    public LocalDate getNextService() {
        return nextService;
    }

    public void setNextService(LocalDate nextService) {
        this.nextService = nextService;
    }

    public CustomerState getState() {
        return state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }
}
