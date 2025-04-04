import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryTest {

    private static Connection connection;

    private static final String TEST_EMAIL = "test@example.com";
    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final LocalDateTime TEST_DATETIME = LocalDateTime.now();
    private static final Category TEST_CATEGORY = Category.RESTAURANT;
    private static final Status TEST_STATUS = Status.PROCESSING;
    private static final String TEST_URL = "http://example.com/invoice.pdf";

    @Test
    void testSetCustomRefundAmount() {
        Category.setCustomRefundAmount(Category.RESTAURANT, 4.0);
        assertEquals(4.0, Category.RESTAURANT.customRefundAmount, "Refund amount for restaurant should be updated");
    }

    @Test
    void testSetInvalidCustomRefundAmount() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Category.setCustomRefundAmount(Category.RESTAURANT, -5.0);
        });
        assertEquals("Betrag muss positiv sein", thrown.getMessage());
    }

    @Test
    void selectCategory_Restaurant_ShouldSaveCorrectly() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, Category.RESTAURANT, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0);
        assertEquals(Category.RESTAURANT, invoice.getCategory());
    }

    @Test
    void selectCategory_Supermarket_ShouldSaveCorrectly() {
        // Beispiel für einen gültigen Rückerstattungsbetrag
        double validReimbursement = 2.5;  // Beispiel für einen gültigen Rückerstattungsbetrag
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, Category.SUPERMARKET, TEST_STATUS, TEST_URL, TEST_DATETIME, validReimbursement);

        // Test, ob die Kategorie korrekt gesetzt wurde
        assertEquals(Category.SUPERMARKET, invoice.getCategory());

        // Test, ob der Rückerstattungsbetrag korrekt gesetzt wurde
        assertEquals(validReimbursement, invoice.getReimbursement(), "Reimbursement should be correctly set");
    }

    @Test
    void selectCategory_NoSelection_ShouldThrowError() throws SQLException {
        // Erstelle eine gemockte Connection und PreparedStatement
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        // Simuliere das Verhalten des PreparedStatement
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);

        // Erstelle eine Invoice-Instanz mit allen notwendigen Parametern
        Invoice invoice = new Invoice(
                "test@example.com",          // Benutzer-E-Mail
                LocalDate.now(),             // Datum
                100.0,                       // Betrag
                null,                        // Kategorie absichtlich null (Fehlerfall)
                Status.PROCESSING,           // Status
                "file_url",                  // Dateipfad
                LocalDateTime.now(),         // Erstellungsdatum
                0.0                          // Rückerstattung
        );

        // Teste, ob die Ausnahme "Please select a category" geworfen wird
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> InvoiceRepository.saveInvoiceInfo(mockConnection,
                        invoice.getUserEmail(),
                        java.sql.Date.valueOf(invoice.getDate()),
                        invoice.getAmount(),
                        invoice.getCategory(),       // Hier wird null für Kategorie übergeben
                        invoice.getStatus(),
                        invoice.getFile_Url(),
                        invoice.getCreatedAt(),
                        invoice.getReimbursement(),
                        new File(invoice.getFile_Url())) // Sicherstellen, dass die Verbindung funktioniert
        );

        // Überprüfe, ob die erwartete Fehlermeldung geworfen wurde
        assertEquals("Please select a category", exception.getMessage());
    }

}
