import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import jku.se.Category;
import jku.se.Controller.AddInvoiceController;
import jku.se.Controller.UserDashboardController;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.*;
import javafx.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceTest {

    // Testdaten
    private static final String TEST_EMAIL = "test@example.com";
    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final LocalDateTime TEST_DATETIME = LocalDateTime.now();
    private static final Category TEST_CATEGORY = Category.RESTAURANT;
    private static final Status TEST_STATUS = Status.PROCESSING;
    private static final String TEST_URL = "http://example.com/invoice.pdf";


    /*@BeforeAll
    static void initJFX() {
        Platform.startup(() -> {});
    }
     */

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
        assertEquals("Amount has to be positive", exception.getMessage());
    }

    @Test
    void constructor_WithZeroAmount_ShouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Invoice(TEST_EMAIL, TEST_DATE, 0.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0)
        );
        assertEquals("Amount has to be positive", exception.getMessage());
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
        assertEquals("Refund does not comply with the rules", exception.getMessage());
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
        assertEquals("Nur JPG/PNG/PDF-Dateien sind erlaubt", exception.getMessage());
    }

    @Test
    void validateFile_WithNonExistentFile_ShouldThrow() {
        File nonExistentFile = new File("definitely_not_here.pdf");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Invoice.validateFile(nonExistentFile)
        );

        assertTrue(exception.getMessage().toLowerCase().contains("datei"));
    }

    @Test
    void validateFile_WithValidJpeg_ShouldNotThrow(@TempDir Path tempDir) throws IOException {
        File validFile = tempDir.resolve("valid.jpeg").toFile();
        validFile.createNewFile();
        assertDoesNotThrow(() -> Invoice.validateFile(validFile));
    }

    /*@Test
    void upload_MultipleInvoicesPerDay_ShouldReturnError() throws IOException {
        // Arrange
        InvoiceRepository repo = spy(new InvoiceRepository());
        Connection mockConnection = mock(Connection.class);

        // Erstelle echte temporäre Datei
        File dummyFile = File.createTempFile("invoice", ".pdf");

        Invoice invoice = new Invoice(
                "test@example.com",
                LocalDate.of(2025, 4, 2),
                120.0,
                Category.SUPERMARKET,
                Status.PROCESSING,
                dummyFile.getAbsolutePath(),
                LocalDateTime.now(),
                3.5
        );

        // Simuliere, dass eine Rechnung für diesen Tag bereits existiert
        doReturn(true).when(repo).invoiceExists(eq(mockConnection), eq("test@example.com"), any());

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                repo.saveInvoiceWithDuplicationCheck(mockConnection, invoice)
        );

        assertEquals("Invoice already submitted for this date", ex.getMessage());
    }*/



    /*@Test
    void uploadInvoiceOnWeekend_ShouldThrowError() throws IOException {
        // Test-Modus aktivieren
        InvoiceRepository.TEST_MODE = true;

        // Temporäre Datei erstellen
        File tempFile = File.createTempFile("invoice", ".pdf");
        tempFile.deleteOnExit();

        // Verbindung mocken
        Connection mockConnection = mock(Connection.class);

        // Samstag: 5. April 2025
        LocalDate weekendDate = LocalDate.of(2025, 4, 5);

        // Rechnung erzeugen
        Invoice invoiceOnWeekend = new Invoice(
                "test@example.com",
                weekendDate,
                100.0,
                Category.RESTAURANT,
                Status.PROCESSING,
                tempFile.getAbsolutePath(),
                LocalDateTime.now(),
                3.0
        );

        // Assertion
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> InvoiceRepository.saveInvoiceInfo(
                        mockConnection,
                        invoiceOnWeekend.getUserEmail(),
                        java.sql.Date.valueOf(invoiceOnWeekend.getDate()),
                        invoiceOnWeekend.getAmount(),
                        invoiceOnWeekend.getCategory(),
                        invoiceOnWeekend.getStatus(),
                        invoiceOnWeekend.getFile_Url(),
                        invoiceOnWeekend.getCreatedAt(),
                        invoiceOnWeekend.getReimbursement(),
                        tempFile // <- wichtig: echte Datei übergeben
                )
        );

        assertEquals("Upload only allowed on weekdays", exception.getMessage());

        // Test-Modus aufräumen
        InvoiceRepository.TEST_MODE = false;
    }*/


    /*@Test
    void enterValidAmount_ShouldSaveCorrectly() throws Exception {
        Connection mockConnection = mock(Connection.class);

        File dummyFile = File.createTempFile("test_invoice", ".pdf");
        dummyFile.deleteOnExit();

        Invoice invoice = new Invoice(
                TEST_EMAIL,
                LocalDate.now(),
                15.50,
                TEST_CATEGORY,
                TEST_STATUS,
                dummyFile.getAbsolutePath(),
                LocalDateTime.now(),
                3.0
        );

        assertEquals(15.50, invoice.getAmount(), 0.001);

        assertDoesNotThrow(() ->
                InvoiceRepository.saveInvoiceInfo(mockConnection,
                        invoice.getUserEmail(),
                        java.sql.Date.valueOf(invoice.getDate()),
                        invoice.getAmount(),
                        invoice.getCategory(),
                        invoice.getStatus(),
                        invoice.getFile_Url(),
                        invoice.getCreatedAt(),
                        invoice.getReimbursement(),
                        dummyFile)
        );
    }*/

    @Test
    void enterNegativeAmount_ShouldThrowError() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Invoice(
                        "test@example.com",
                        LocalDate.now(),
                        -1.0, // <- NEGATIVER Betrag
                        Category.RESTAURANT,
                        Status.PROCESSING,
                        "dummy.pdf",
                        LocalDateTime.now(),
                        3.0
                )
        );
        assertEquals("Amount has to be positive", ex.getMessage());
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


    @Test
    void invoice_WithAmountOverLimit_ShouldThrow() { //hat noch Warnungen
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Invoice(TEST_EMAIL, TEST_DATE, 1500.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0)
        );
        assertEquals("Amount cannot be greater than 1000 €", ex.getMessage());
    }

    /*@Test
    void submitCompleteInvoice_ShouldDisplayConfirmation() throws IOException {
        AddInvoiceController controller = new AddInvoiceController();

        // Set FXML components manually
        DatePicker picker = new DatePicker(TEST_DATE);
        TextField amount = new TextField("100.0");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().add(TEST_CATEGORY.name());
        categoryCombo.getSelectionModel().select(TEST_CATEGORY.name());

        Label statusLabel = new Label();  // Wichtig für späteren Check

        controller.setDatePicker(picker);
        controller.setAmountField(amount);
        controller.setCategoryCombo(categoryCombo);
        controller.setSelectedFile(new File("validInvoice.pdf")); // Dummy-Datei
        controller.setStatusLabel(statusLabel);

        // Simuliere eingeloggte User
        UserDashboardController.setCurrentUserEmail(TEST_EMAIL);

        // Trigger Upload
        ActionEvent dummyEvent = mock(ActionEvent.class);
        controller.handleUpload(dummyEvent);

        // Verify confirmation
        assertEquals("Invoice and file uploaded successfully.", controller.getStatusLabel().getText());
    }*/




    @Test
    void categoryValueOf_WithNull_ShouldThrowMeaningfulError() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> Category.valueOf(null)
        );
        assertEquals("Name is null", exception.getMessage());
    }



    /*@Test
    void submitInvoiceWithoutAmount_ShouldThrowError() throws IOException { //Warunung
        AddInvoiceController controller = new AddInvoiceController();

        controller.datePicker = new DatePicker(TEST_DATE);
        controller.amountField = new TextField("0.0");
        controller.categoryCombo = new ComboBox<>();
        controller.categoryCombo.getItems().add(TEST_CATEGORY.name());
        controller.categoryCombo.getSelectionModel().select(TEST_CATEGORY.name());
        controller.statusLabel = new Label();

        File dummyFile = File.createTempFile("dummy", ".pdf");
        dummyFile.deleteOnExit();
        controller.selectedFile = dummyFile;

        UserDashboardController.setCurrentUserEmail(TEST_EMAIL);

        InvoiceRepository.TEST_MODE = true;

        controller.handleUpload(new ActionEvent());

        assertEquals("Amount must be greater than 0", controller.statusLabel.getText());
    }
     */
}