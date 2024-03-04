package ca.powercool.powercoolhub.models;
import java.sql.Date;
import jakarta.persistence.*;

@Entity
@Table(name="customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    //Can be changed - some basic data about each customer
    private String name;
    private String address;
    private String phoneNumber;
    private String notes;
    private Date lastServiced;
    private Date installationDate;
    private String state; //Can be a set of strings we assign to this, ie Archived, requested service, requested install.
    private boolean paymentReceived; //true / false for the payment received or pending
    public Customer() {
    }
    public Customer(Integer id, String name, String address, String phoneNumber, String notes, Date lastServiced,
            Date installationDate, String state, boolean paymentReceived) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.notes = notes;
        this.lastServiced = lastServiced;
        this.installationDate = installationDate;
        this.state = state;
        this.paymentReceived = paymentReceived;
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
    public Date getInstallationDate() {
        return installationDate;
    }
    public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public boolean isPaymentReceived() {
        return paymentReceived;
    }
    public void setPaymentReceived(boolean paymentReceived) {
        this.paymentReceived = paymentReceived;
    }
    

}
