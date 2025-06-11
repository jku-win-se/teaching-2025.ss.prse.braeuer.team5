import jku.se.Notification;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @BeforeEach
    void clearBefore() {
        Notification.clearMessages();
    }

    @Test
    void constructor_WithMessageForAdminAndTargetUser_ShouldInitializeFields() {
        String message = "New invoice submitted";
        boolean isForAdmin = true;
        String email = "admin@lunchify.com";

        Notification notification = new Notification(message, isForAdmin, email);

        assertEquals(message, notification.getMessage());
        assertNotNull(notification);
    }

    @Test
    void constructor_WithMessageOnly_ShouldSetMessageAndTimestamp() {
        String msg = "System maintenance";
        Notification notification = new Notification(msg);

        assertEquals(msg, notification.getMessage());
        assertNotNull(notification);
    }

    @Test
    void constructor_Empty_ShouldSetDefaults() {
        Notification notification = new Notification();

        assertEquals("", notification.getMessage());
        assertNotNull(notification);
    }

    @Test
    void clearMessages_ShouldEmptyTheMessageList() {
        Notification.MESSAGES_SENT.add("TestMessage");
        assertFalse(Notification.MESSAGES_SENT.isEmpty());

        Notification.clearMessages();

        assertTrue(Notification.MESSAGES_SENT.isEmpty());
    }
}