import jku.se.DatabaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    //Test database connection
    @Test
    void testDatabaseConnection() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(2), "Connection should be valid");
        } catch (Exception e) {
            fail("Exception during connection: " + e.getMessage());
        }
    }
    //Test correct url
    @Test
    void testGetPublicFileUrl() {
        String testFileName = "test_invoice.pdf";
        String expectedUrlStart = "https://dljjtuynbgxgmhkcdypu.supabase.co/storage/v1/object/invoicefiles/";

        String actualUrl = DatabaseConnection.getPublicFileUrl(testFileName);

        assertNotNull(actualUrl, "Generated URL should not be null");
        assertTrue(actualUrl.startsWith(expectedUrlStart), "URL should start with the expected Supabase base URL");
        assertTrue(actualUrl.endsWith(testFileName), "URL should end with the provided file name");
    }

}