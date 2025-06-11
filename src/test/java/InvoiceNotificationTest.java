import jku.se.Invoice;
import jku.se.User;
import jku.se.Notification;
import jku.se.Category;
import jku.se.Status;

import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalDateTime;

class InvoiceNotificationTest {

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

}