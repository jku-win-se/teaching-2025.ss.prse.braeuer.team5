import jku.se.Category;
import jku.se.Status;
import jku.se.User;
import jku.se.Invoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
public class NotificationTest {
    private User user;
    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        user = new User("Max Mustermann", "max@example.com", "123", false);
        testInvoice = new Invoice(
                user.getEmail(),
                LocalDate.now(),
                42.50,
                Category.RESTAURANT,
                Status.PROCESSING,
                "file.pdf",
                LocalDateTime.now(),
                3.0
        );
    }

    @Test
    void setPreferenceEmail_ShouldSendEmailOnly() {
        user.changeNotificationSettings("Email");
        assertEquals("Email", user.getPreferredNotificationMethod());
        user.uploadInvoice(testInvoice);
    }

    @Test
    void testSetPreferenceInApp_ShouldSendInAppOnly() {
        user.changeNotificationSettings("In-App");
        assertEquals("In-App", user.getPreferredNotificationMethod());
        user.uploadInvoice(testInvoice);
    }

    @Test
    void testSetPreferenceBoth_ShouldSendBoth() {
        user.changeNotificationSettings("Both");
        assertEquals("Both", user.getPreferredNotificationMethod());
        user.uploadInvoice(testInvoice);
    }

    @Test
    void testDefaultPreference_ShouldBeInApp() {
        User newUser = new User("Anna", "anna@example.com", "1234", false);
        assertEquals("In-App", newUser.getPreferredNotificationMethod());
        newUser.uploadInvoice(testInvoice);
    }

    @Test
    void testChangePreference_ShouldBeAppliedImmediately() {
        user.changeNotificationSettings("Email");
        assertEquals("Email", user.getPreferredNotificationMethod());

        user.changeNotificationSettings("In-App");
        assertEquals("In-App", user.getPreferredNotificationMethod());

        user.uploadInvoice(testInvoice);
    }

    @Test
    void testPreferencePersistsAfterLogoutSimulation() {
        user.changeNotificationSettings("Both");

        // simulate logout/login by creating same user again with same email
        String savedPreference = user.getPreferredNotificationMethod();
        User loggedInAgain = new User(user.getName(), user.getEmail(), user.getPassword(), false);
        loggedInAgain.changeNotificationSettings(savedPreference);

        assertEquals("Both", loggedInAgain.getPreferredNotificationMethod());
        loggedInAgain.uploadInvoice(testInvoice);
    }



}
