import jku.se.Notification;
import jku.se.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @AfterEach
    void clearMessages() {
        Notification.clearMessages();
    }

    /*@Test
    void testInAppNotificationAfterSubmission() {
        User user = new User("Test User", "test@example.com", "pw", false);

        Notification notification = new Notification("Invoice submitted successfully.");
        notification.sendInApp(user, notification.getMessage());

        List<String> messages = Notification.messagesSent;
        assertEquals(1, messages.size());
        assertTrue(messages.getFirst().contains("In-App"));
        assertTrue(messages.getFirst().contains("Invoice submitted successfully."));
    }

   /* @Test
    void testNotificationAfterApproval() {
        User user = new User("Max", "max@example.com", "pw", false);

        Notification notification = new Notification("Your invoice from 01.05.2025 was approved.");
        notification.sendInApp(user, notification.getMessage());

        assertEquals(1, Notification.messagesSent.size());
        assertTrue(Notification.messagesSent.getFirst().contains("approved"));
    }*/
}
