import jku.se.UserInvoiceData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserInvoiceDataTest {

    @Test
    void testConstructorAndGetters() {
        UserInvoiceData data = new UserInvoiceData("Max Mustermann", "max@lunchify.com", 3);

        assertEquals("Max Mustermann", data.getName());
        assertEquals("max@lunchify.com", data.getEmail());
        assertEquals(3, data.getInvoiceCount());
    }

    @Test
    void testSetInvoiceCount() {
        UserInvoiceData data = new UserInvoiceData("Max", "max@lunchify.com", 2);
        data.setInvoiceCount(5);

        assertEquals(5, data.getInvoiceCount());
    }
}
