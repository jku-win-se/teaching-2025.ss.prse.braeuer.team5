import jku.se.*;
import jku.se.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserTest {

    private User user;

    @BeforeEach
    void setup() {
        user = new User("Max Mustermann", "max@lunchify.com", "secret123", false);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("Max Mustermann", user.getName());
        assertEquals("max@lunchify.com", user.getEmail());
        assertEquals("secret123", user.getPassword());
        assertFalse(user.isAdministrator());
    }

    @Test
    void testLogin_ShouldLogInfoLevel() {
        assertDoesNotThrow(() -> user.login());
    }

    @Test
    void testReceiveNotification_ShouldSendNotification() {
        Notification mockNotification = mock(Notification.class);
        user.setNotification(mockNotification);

        user.receiveNotification("Test Nachricht");
        assertDoesNotThrow(() -> user.receiveNotification("Hallo"));
    }

    @Test
    void testViewHistory_ShouldReturnEmptyListInitially() {
        List<Invoice> history = user.viewHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty(), "New user should have empty history");
    }

    @Test
    void testViewAllInvoices_ShouldCallRepository() {
        try (MockedStatic<InvoiceRepository> mocked = mockStatic(InvoiceRepository.class)) {
            mocked.when(() -> InvoiceRepository.getAllInvoicesUser("max@lunchify.com"))
                    .thenReturn(List.of(new Invoice()));

            List<Invoice> result = user.viewAllInvoices();
            assertEquals(1, result.size());
        }
    }
}
