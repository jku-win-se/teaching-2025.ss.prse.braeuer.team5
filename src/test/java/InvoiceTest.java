import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import jku.se.Category;
import jku.se.Controller.AddInvoiceController;
import jku.se.Controller.UserDashboardController;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class InvoiceTest {

    // Testdaten
    private static final String TEST_EMAIL = "test@example.com";
    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final LocalDateTime TEST_DATETIME = LocalDateTime.now();
    private static final Category TEST_CATEGORY = Category.RESTAURANT;
    private static final Status TEST_STATUS = Status.PROCESSING;
    private static final String TEST_URL = "http://example.com/invoice.pdf";

    @Test
    void constructor_WithValidAmount_ShouldNotThrow() {
        assertDoesNotThrow(() ->
                new Invoice(TEST_EMAIL, TEST_DATE, 100.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0)
        );
    }

    @Test
    void getAmount_ShouldReturnCorrectValue() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0);
        assertEquals(100.0, invoice.getAmount(), 0.001);
    }

    @Test
    void constructor_WithNegativeAmount_ShouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Invoice(TEST_EMAIL, TEST_DATE, -100.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0)
        );
        assertEquals("Betrag muss positiv sein", exception.getMessage());
    }

    @Test
    void constructor_WithZeroAmount_ShouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Invoice(TEST_EMAIL, TEST_DATE, 0.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0)
        );
        assertEquals("Betrag muss positiv sein", exception.getMessage());
    }

    @Test
    void getReimbursement_ShouldReturnCorrectValue() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 50.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0);
        assertEquals(3.0, invoice.getReimbursement(), 0.001);
    }

    @Test
    void constructor_WithInvalidReimbursement_ShouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Invoice(TEST_EMAIL, TEST_DATE, 50.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 5.0)
        );
        assertEquals("Rückerstattung entspricht nicht den Regeln", exception.getMessage());
    }

    @Test
    void validateFile_WithValidPdf_ShouldNotThrow(@TempDir Path tempDir) throws IOException {
        File validFile = tempDir.resolve("valid.pdf").toFile();
        validFile.createNewFile();
        assertDoesNotThrow(() -> Invoice.validateFile(validFile));
    }

    @Test
    void validateFile_WithInvalidFormat_ShouldThrow(@TempDir Path tempDir) throws IOException {
        File invalidFile = tempDir.resolve("invalid.zip").toFile();
        invalidFile.createNewFile();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Invoice.validateFile(invalidFile)
        );
        assertEquals("Only JPG/PNG/PDF allowed", exception.getMessage());
    }

    @Test
    void validateFile_WithNonExistentFile_ShouldThrow() {
        File nonExistentFile = new File("nonexistent.pdf");
        assertThrows(IllegalArgumentException.class, () -> Invoice.validateFile(nonExistentFile));
    }

    @Test
    void validateFile_WithValidJpeg_ShouldNotThrow(@TempDir Path tempDir) throws IOException {
        File validFile = tempDir.resolve("valid.jpeg").toFile();
        validFile.createNewFile();
        assertDoesNotThrow(() -> Invoice.validateFile(validFile));
    }

    @Test
    void upload_MultipleInvoicesPerDay_ShouldReturnError() {
        // Mock die Connection
        Connection mockConnection = mock(Connection.class);

        // Simuliere die Hinzufügung einer Rechnung für denselben Tag
        Invoice firstInvoice = new Invoice("test@example.com", LocalDate.of(2025, 4, 2), 100.0, Category.RESTAURANT, Status.PROCESSING, "file_url", LocalDateTime.now(), 3.0);
        assertDoesNotThrow(() -> InvoiceRepository.saveInvoiceInfo(mockConnection, "test@example.com", java.sql.Date.valueOf(LocalDate.of(2025, 4, 2)), 100.0, Category.RESTAURANT, Status.PROCESSING, "file_url", LocalDateTime.now(), 3.0, new File("file_url")));

        // Simuliere eine zweite Rechnung für denselben Tag
        Invoice secondInvoice = new Invoice("test@example.com", LocalDate.of(2025, 4, 2), 120.0, Category.SUPERMARKET, Status.PROCESSING, "file_url", LocalDateTime.now(), 3.5);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> InvoiceRepository.saveInvoiceInfo(mockConnection, "test@example.com", java.sql.Date.valueOf(LocalDate.of(2025, 4, 2)), 120.0, Category.SUPERMARKET, Status.PROCESSING, "file_url", LocalDateTime.now(), 3.5, new File("file_url"))
        );

        assertEquals("Upload Limit: One Invoice per day", exception.getMessage());
    }


    @Test
    void uploadInvoiceOnWeekend_ShouldThrowError() {
        // Mock die Connection
        Connection mockConnection = mock(Connection.class);

        // Setze das Datum auf Samstag (z.B., 5. April 2025)
        Invoice invoiceOnWeekend = new Invoice("test@example.com", LocalDate.of(2025, 4, 5), 100.0, Category.RESTAURANT, Status.PROCESSING, "file_url", LocalDateTime.now(), 3.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> InvoiceRepository.saveInvoiceInfo(mockConnection, "test@example.com", java.sql.Date.valueOf(LocalDate.of(2025, 4, 5)), 100.0, Category.RESTAURANT, Status.PROCESSING, "file_url", LocalDateTime.now(), 3.0, new File("file_url"))
        );
        assertEquals("Upload only allowed on weekdays", exception.getMessage());
    }

    @Test
    void enterValidAmount_ShouldSaveCorrectly() {
        // Mock die Connection
        Connection mockConnection = mock(Connection.class);

        // Erstelle eine Invoice mit gültigem Betrag
        Invoice invoice = new Invoice("test@example.com", LocalDate.now(), 15.50, Category.RESTAURANT, Status.PROCESSING, "file_url", LocalDateTime.now(), 3.0);

        assertEquals(15.50, invoice.getAmount(), 0.001);

        // Überprüfe, ob der Betrag korrekt gespeichert wird
        assertDoesNotThrow(() -> InvoiceRepository.saveInvoiceInfo(mockConnection, "test@example.com", java.sql.Date.valueOf(LocalDate.now()), 15.50, Category.RESTAURANT, Status.PROCESSING, "file_url", LocalDateTime.now(), 3.0, new File("file_url")));
    }

    @Test
    void enterNegativeAmount_ShouldThrowError() {
        // Mock die Connection
        Connection mockConnection = mock(Connection.class);

        // Erstelle eine neue Invoice mit negativem Betrag
        Invoice invoice = new Invoice(
                "test@example.com",  // Benutzer-E-Mail
                LocalDate.now(),     // Aktuelles Datum
                -1.0,                // Negativer Betrag
                Category.RESTAURANT, // Kategorie
                Status.PROCESSING,   // Status
                "file_url",          // Dateipfad
                LocalDateTime.now(), // Erstellungsdatum
                3.0                  // Rückerstattungsbetrag
        );

        // Teste, ob eine IllegalArgumentException geworfen wird, wenn saveInvoiceInfo aufgerufen wird
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> InvoiceRepository.saveInvoiceInfo(mockConnection,
                        invoice.getUserEmail(),
                        java.sql.Date.valueOf(invoice.getDate()),
                        invoice.getAmount(),
                        invoice.getCategory(),
                        invoice.getStatus(),
                        invoice.getFile_Url(),
                        invoice.getCreatedAt(),
                        invoice.getReimbursement(),
                        new File(invoice.getFile_Url())) // Datei (dies ist ein Dummy-Pfad)
        );

        // Überprüfe, dass die Ausnahme die erwartete Nachricht enthält
        assertEquals("Invalid amount", exception.getMessage());
    }

    @Test
    void invoiceEditableWithinSameMonth_ShouldReturnTrue() { // Rechnung sollte editierbar sein
        LocalDateTime createdAt = LocalDateTime.now().minusDays(3);
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, createdAt, 3.0);

        assertTrue(invoice.isEditable(), "Invoice should be editable before the end of the same month");
    }

    @Test
    void invoiceNotEditableAfterMonthPassed_ShouldReturnFalse() {// Rechnung sollte nicht mehr editierbar sein
        LocalDateTime createdAt = LocalDateTime.now().minusMonths(2);
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, createdAt, 3.0);

        assertFalse(invoice.isEditable(), "Invoice should NOT be editable after the submission month has passed");
    }




    /*@Test
    void enterAmountExceedingMaxLimit_ShouldThrowError() {
        // Mock die Connection
        Connection mockConnection = mock(Connection.class);

        // Erstelle eine Invoice mit Betrag größer als 1000 €
        Invoice invoice = new Invoice("test@example.com", LocalDate.now(), 1500.0, Category.RESTAURANT, Status.PROCESSING, "file_url", LocalDateTime.now(), 3.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> InvoiceRepository.saveInvoiceInfo(mockConnection, "test@example.com", java.sql.Date.valueOf(LocalDate.now()), 1500.0, Category.RESTAURANT, Status.PROCESSING, "file_url", LocalDateTime.now(), 3.0, new File("file_url"))
        );
        assertEquals("Amount cannot be greater than 1000 €", exception.getMessage());
    }

    @Test
    void submitCompleteInvoice_ShouldDisplayConfirmation() {
        // Mock die Connection
        Connection mockConnection = mock(Connection.class);

        // Erstelle den Controller und initialisiere alle UI-Elemente explizit
        AddInvoiceController controller = new AddInvoiceController();

        // Initialisiere den DatePicker und setze den Wert
        controller.datePicker = new DatePicker(); // Initialisiere den DatePicker manuell
        controller.datePicker.setValue(TEST_DATE); // Setze das Datum auf TEST_DATE

        // Initialisiere andere UI-Elemente, die verwendet werden
        controller.amountField = new javafx.scene.control.TextField("100.0");
        controller.categoryCombo = new ComboBox<>();
        controller.categoryCombo.getItems().add(TEST_CATEGORY.name());
        controller.categoryCombo.getSelectionModel().select(TEST_CATEGORY.name());

        // Simuliere eine Datei (du musst sicherstellen, dass eine echte Datei im Test verwendet wird)
        controller.selectedFile = new File("validInvoice.pdf");

        // Simuliere das Benutzer-Email direkt über UserDashboardController
        String TEST_EMAIL = "test@example.com"; // Beispiel-E-Mail-Adresse
        UserDashboardController.setCurrentUserEmail(TEST_EMAIL);

        // Erstelle das ActionEvent (Leeres ActionEvent)
        javafx.event.ActionEvent event = mock(javafx.event.ActionEvent.class); // Leeres ActionEvent, das im Test verwendet werden kann

        // Wir testen die handleUpload-Methode, die die Rechnung übermittelt
        controller.handleUpload(event);

        // Überprüfe, ob die Bestätigungsmeldung korrekt gesetzt wurde
        assertEquals("Invoice and file uploaded successfully.", controller.statusLabel.getText());
    }

    /*@Test
    void submitInvoiceWithoutCategory_ShouldThrowError() {
        // Mock die Connection
        Connection mockConnection = mock(Connection.class);

        // Setze eine Rechnung ohne Kategorie
        AddInvoiceController controller = new AddInvoiceController();
        controller.datePicker.setValue(TEST_DATE);
        controller.amountField.setText("100.0");
        controller.categoryCombo.getSelectionModel().clearSelection(); // Keine Kategorie ausgewählt

        // Simuliere eine Datei (du musst sicherstellen, dass eine echte Datei im Test verwendet wird)
        controller.selectedFile = new File("validInvoice.pdf");

        // Simuliere das Benutzer-Email
        UserDashboardController.setCurrentUserEmail(TEST_EMAIL);

        // Führe den Upload durch
        controller.handleUpload(new ActionEvent());

        // Überprüfe, ob die Fehlermeldung für fehlende Kategorie angezeigt wird
        assertEquals("Please select a category", controller.statusLabel.getText());
    }

    @Test
    void submitInvoiceWithoutAmount_ShouldThrowError() {
        // Mock die Connection
        Connection mockConnection = mock(Connection.class);

        // Setze eine Rechnung ohne Betrag (0.0)
        AddInvoiceController controller = new AddInvoiceController();
        controller.datePicker.setValue(TEST_DATE);
        controller.amountField.setText("0.0"); // Betrag auf 0 setzen
        controller.categoryCombo.getSelectionModel().select(TEST_CATEGORY.name());

        // Simuliere eine Datei (du musst sicherstellen, dass eine echte Datei im Test verwendet wird)
        controller.selectedFile = new File("validInvoice.pdf");

        // Simuliere das Benutzer-Email
        UserDashboardController.setCurrentUserEmail(TEST_EMAIL);

        // Führe den Upload durch
        controller.handleUpload(new ActionEvent());

        // Überprüfe, ob die Fehlermeldung für fehlenden Betrag angezeigt wird
        assertEquals("Amount must be greater than 0", controller.statusLabel.getText());
    }

     */
}