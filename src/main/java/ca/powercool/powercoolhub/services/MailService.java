package ca.powercool.powercoolhub.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendBookingConfirmation(String to, String customerName, LocalDate appointmentDate,
                                        String serviceAddress, String serviceRequest, List<String> technicianAssigned) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String subject = "Booking Confirmation: HVAC Service Appointment";
        String body = "Dear " + customerName + ",\n\n" +
                      "We are pleased to confirm your appointment for HVAC service with PowerCool. " +
                      "Below are the details of your booking:\n\n" +
                      "- Appointment Date: " + dateFormat.format(appointmentDate) + "\n" +
                      "- Service Address: " + serviceAddress + "\n" +
                      "- Service Request: " + serviceRequest + "\n" +
                      "- Technician Assigned: " + technicianAssigned + "\n\n" +
                      "Please ensure someone over the age of 18 will be present at the service location " +
                      "during the scheduled appointment time. If there are any changes or concerns regarding " +
                      "this appointment, please contact us at " + "info@powercool.ca" + "\n\n" +
                      "Thank you for choosing PowerCool. We look forward to serving you and addressing your HVAC needs.\n\n" +
                      "Best regards,\n" +
                      "Kamal Lakha\n" +
                      "PowerCool";

        sendEmail(to, subject, body);
    }

    public void sendCancellationConfirmation(String to, String customerName, LocalDate appointmentDate,
                                            String serviceAddress, String serviceRequest) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String subject = "Cancellation Confirmation: HVAC Service Appointment";
        String body = "Dear " + customerName + ",\n\n" +
                      "We are writing to confirm the cancellation of your HVAC service appointment with PowerCool. " +
                      "Below are the details of the cancelled booking:\n\n" +
                      "- Appointment Date: " + dateFormat.format(appointmentDate) + "\n" +
                      "- Service Address: " + serviceAddress + "\n" +
                      "- Service Request: " + serviceRequest + "\n\n" +
                      "If you have any questions or concerns regarding this cancellation, please contact us at " +
                      "info@powercool.ca or call us at 604-715-7478.\n\n" + 
                        "Thank you for considering PowerCool for your HVAC needs.\n\n" +
                        "Best regards,\n" +
                        "Kamal Lakha\n" +
                        "PowerCool";
                        
        sendEmail(to, subject, body);
    }


    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("powercool205@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }
}
