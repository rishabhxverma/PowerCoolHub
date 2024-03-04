package ca.powercool.powercoolhub.models;

import jakarta.persistence.*;

import jakarta.persistence.Entity;

@Entity
@Table(name="customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String firstName;
    private String lastName;
    private String address;
    private String notes;
    private String lastServiced;
    private String installationDate;
    private String state;
    public Customer() {
    }
    public Customer(String firstName, String lastName, String address, String notes, String lastServiced,
            String installationDate, String state) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.notes = notes;
        this.lastServiced = lastServiced;
        this.installationDate = installationDate;
        this.state = state;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getLastServiced() {
        return lastServiced;
    }
    public void setLastServiced(String lastServiced) {
        this.lastServiced = lastServiced;
    }
    public String getInstallationDate() {
        return installationDate;
    }
    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    
}
