package ca.powercool.powercoolhub.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import ca.powercool.powercoolhub.repositories.UserRepository;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MailService {

    @Autowired
    private UserRepository userRepository;

    private final JavaMailSender javaMailSender;

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    //Used to turn the Duration object into a readible string
    public String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
            "%d hours, %02d minutes, %02d seconds",
            absSeconds / 3600,
            (absSeconds % 3600) / 60,
            absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    public void sendBookingConfirmation(String to, String customerName, LocalDate appointmentDate,
            String serviceAddress, String serviceRequest, List<String> technicianAssigned) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String subject = "Booking Confirmation: HVAC Service Appointment";
        String body = "Dear " + customerName + ",\n\n" +
                "We are pleased to confirm your appointment for HVAC service with PowerCool. " +
                "Below are the details of your booking:\n\n" +
                "- Appointment Date: " + appointmentDate.format(formatter) + "\n" +
                "- Service Address: " + serviceAddress + "\n" +
                "- Service Request: " + serviceRequest + "\n" +
                "- Technician Assigned: " + String.join(", ", technicianAssigned) + "\n\n" +
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String subject = "Cancellation Confirmation: HVAC Service Appointment";
        String body = "Dear " + customerName + ",\n\n" +
                "We are writing to confirm the cancellation of your HVAC service appointment with PowerCool. " +
                "Below are the details of the cancelled booking:\n\n" +
                "- Appointment Date: " + appointmentDate.format(formatter) + "\n" +
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

    public void notifyManagersOfOutOfRangeClockOut(Long technicianId, String clockOutAddress) {
        String technicianName = userRepository.findNameById(technicianId);
        String to = "menzies23@gmail.com";
        String subject = "Technician Clocked Out Outside of Range";
        String body = "Technician with name: " + technicianName + " has clocked out at an unauthorized location: " + clockOutAddress + ". Please follow up with technician.";
        sendEmail(to, subject, body);
    }

    public void notifyManagersOfLateClockOutTime(Long technicianId, Duration duration, LocalDateTime clockOutTime){
        String technicianName = userRepository.findNameById(technicianId);
        String to = "rishabhverma2503@gmail.com";
        String subject = "Technician Clocked Out Late";
        String formattedDuration = formatDuration(duration);
        String body = "Technician with name: " + technicianName + " has clocked out at " + clockOutTime + ".\n"+  "They were clocked in for a duration of: " 
                        + formattedDuration + ". Please follow up with technician.";
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
