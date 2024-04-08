package ca.powercool.powercoolhub.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private MailService mailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    @Test
    public void testSendBookingConfirmation() {
        String to = "customerForTesting@example.com";
        String customerName = "Alex Tobby";
        String serviceAddress = "123 Main St";
        String serviceRequest = "AC Repair";
        List<String> technicianAssigned = Arrays.asList("Rachel Green", "Tom Holland");

        mailService.sendBookingConfirmation(to, customerName,
                LocalDate.of(2024, 4, 10), serviceAddress, serviceRequest,
                technicianAssigned);

        verify(javaMailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();

        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals("Booking Confirmation: HVAC Service Appointment", sentMessage.getSubject());
    }

    @Test
    public void testSendCancellationConfirmation() {
        String to = "repairemyheater@example.com";
        String customerName = "Jane Marry";
        String serviceAddress = "456 Main St";
        String serviceRequest = "Heater Repair";

        mailService.sendCancellationConfirmation(to, customerName,
                LocalDate.of(2024, 4, 10), serviceAddress,
                serviceRequest);

        verify(javaMailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();

        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals("Cancellation Confirmation: HVAC Service Appointment", sentMessage.getSubject());
    }
}