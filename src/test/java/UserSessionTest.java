import jku.se.User;
import jku.se.UserSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @AfterEach
    void tearDown() {
        UserSession.setCurrentUser(null);
    }

    @Test
    void testSetAndGetCurrentUser_ShouldStoreAndReturnUser() {
        User user = new User("Max Mustermann", "max@lunchify.com", "geheim", false);

        UserSession.setCurrentUser(user);
        User current = UserSession.getCurrentUser();

        assertNotNull(current);
        assertEquals("max@lunchify.com", current.getEmail());
        assertEquals("Max Mustermann", current.getName());
        assertFalse(current.isAdministrator());
    }

    @Test
    void testSetCurrentUserToNull_ShouldClearSession() {
        UserSession.setCurrentUser(null);
        assertNull(UserSession.getCurrentUser());
    }
}
