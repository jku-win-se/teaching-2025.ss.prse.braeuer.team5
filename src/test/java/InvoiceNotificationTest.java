import jku.se.Invoice;
import jku.se.User;
import jku.se.Notification;
import jku.se.Category;
import jku.se.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceNotificationTest {

    private User user;
    private Invoice invoice;
    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.clearMessages();

        user = new User("Test User", "test@lunchify.com", "secret", false);
        user.setNotification(notification);

        invoice = new Invoice(
                user.getEmail(),
                LocalDate.now(),
                50.00,
                Category.RESTAURANT,
                Status.PROCESSING,
                "test.pdf",
                LocalDateTime.now(),
                3.0
        );
    }

    /*@Test
    void testConfirmationMessageContainsCorrectReimbursementAmount() {
        user.changeNotificationSettings("In-App");
        user.uploadInvoice(invoice);
        assertFalse(Notification.messagesSent.isEmpty(), "Notification should be sent");
        String message = Notification.messagesSent.get(0);
        assertTrue(message.contains("3.0"), "Message should contain the reimbursement amount");
    }

   @Test
    void testConfirmationEmailIsSentIfPreferenceIsEmail() {
        user.changeNotificationSettings("Email");
        user.uploadInvoice(invoice);
        assertFalse(Notification.messagesSent.isEmpty(), "Email should be sent");
        String message = Notification.messagesSent.get(0);
        assertTrue(message.startsWith("[Email]"), "Message should be sent via email");
        assertTrue(message.contains("3.0"), "Email message should contain the reimbursement amount");
    }



    @Test
    void testConfirmationAppearsInAppIfPreferenceIsInApp() {
        user.changeNotificationSettings("In-App");
        user.uploadInvoice(invoice);
        assertFalse(Notification.messagesSent.isEmpty(), "In-app message should be sent");
        String message = Notification.messagesSent.get(0);
        assertTrue(message.startsWith("[In-App]"), "Message should appear in-app");
        assertTrue(message.contains("3.0"), "In-app message should contain reimbursement amount");
    }
     */
}