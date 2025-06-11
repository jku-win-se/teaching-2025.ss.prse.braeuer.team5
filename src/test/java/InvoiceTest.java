import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;


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
    void validateFile_WithValidJpeg_ShouldNotThrow(@TempDir Path tempDir) throws IOException {
        File validFile = tempDir.resolve("valid.jpeg").toFile();
        validFile.createNewFile();
        assertDoesNotThrow(() -> Invoice.validateFile(validFile));
    }

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

    @Test
    void categoryValueOf_WithNull_ShouldThrowMeaningfulError() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> Category.valueOf(null)
        );
        assertEquals("Name is null", exception.getMessage());
    }

    @Test
    void testIsDateOnWeekday() {
        Invoice invoice = new Invoice(("testuser1@lunchify.com"), LocalDate.of(2025, 3, 11), 23.99, Category.RESTAURANT, Status.PROCESSING, "https://example.com/file.pdf", LocalDateTime.now(), 3);
        assertTrue(invoice.isDateOnWeekday(LocalDate.of(2025, 4, 21)), "This day is a working day");
        assertTrue(invoice.isDateOnWeekday(LocalDate.of(2025, 4, 24)), "Donnerstag sollte ein Werktag sein");

        assertFalse(invoice.isDateOnWeekday(LocalDate.of(2025, 4, 20)), "This day is a working day");
        assertFalse(invoice.isDateOnWeekday(LocalDate.of(2025, 4, 19)), "This day is a working day");
    }

    @Test
    void testIsInCurrentMonth() {
        Invoice invoice = new Invoice(("testuser1@lunchify.com"), LocalDate.of(2025, 3, 11), 23.99, Category.RESTAURANT, Status.PROCESSING, "https://example.com/file.pdf", LocalDateTime.now(), 3);
        LocalDate now = LocalDate.of(2025, 4, 22);

        assertTrue(invoice.isInCurrentMonth(LocalDate.of(2025, 4, 1), now), "Date in the same month and year");
        assertFalse(invoice.isInCurrentMonth(LocalDate.of(2025, 3, 31), now), "Date is not in the same month");
        assertFalse(invoice.isInCurrentMonth(LocalDate.of(2024, 4, 22), now), "Date is not in the same year");
    }

    @Test
    void testIsValidAmount() {
        Invoice invoice = new Invoice(("testuser1@lunchify.com"), LocalDate.of(2025, 3, 11), 23.99, Category.RESTAURANT, Status.PROCESSING, "https://example.com/file.pdf", LocalDateTime.now(), 3);
        assertTrue(invoice.isValidAmount(0.0), "0  is valid");
        assertTrue(invoice.isValidAmount(999.99), "Amount is not valid");
        assertTrue(invoice.isValidAmount(1000.0), "Amount is valid");

        assertFalse(invoice.isValidAmount(-1.0), "Amount is not valid");
        assertFalse(invoice.isValidAmount(1000.01), "Amount is not valid");
    }

    @Test
    void validateDate_WithValidWeekday_ShouldReturnDate() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0);
        LocalDate weekday = LocalDate.of(2025, 6, 11);
        assertEquals(weekday, invoice.validateDate(weekday));
    }

    @Test
    void validateDate_WithWeekend_ShouldThrow() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0);
        LocalDate weekend = LocalDate.of(2025, 6, 8);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoice.validateDate(weekend));
        assertEquals("Invoices only on weekdays allowed.", exception.getMessage());
    }

    @Test
    void getCreatedAtString_ShouldReturnFormattedDate() {
        LocalDateTime testDateTime = LocalDateTime.of(2025, 6, 11, 15, 30);
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, testDateTime, 3.0);
        assertEquals("11.06.2025", invoice.getCreatedAtString());
    }

    @Test
    void calculateRefund_WithLowerAmount_ShouldReturnAmount() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 3.0, TEST_CATEGORY, TEST_STATUS, TEST_URL, TEST_DATETIME, 3.0);
        assertEquals(3.0, invoice.calculateRefund());
    }

    @Test
    void calculateRefund_WithHigherAmount_ShouldReturnMaxRefund() {
        Category mockCategory = mock(Category.class);
        when(mockCategory.getRefundAmount()).thenReturn(5.0);
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 10.0, mockCategory, TEST_STATUS, TEST_URL, TEST_DATETIME, 5.0);
        assertEquals(5.0, invoice.calculateRefund());
    }

    @Test
    void getCategoryString_ShouldReturnCategoryName() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, Category.RESTAURANT, Status.PROCESSING, TEST_URL, TEST_DATETIME, 3.0);
        assertEquals("RESTAURANT", invoice.getCategoryString());
    }

    @Test
    void getStatusString_ShouldReturnStatusName() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, Category.RESTAURANT, Status.APPROVED, TEST_URL, TEST_DATETIME, 3.0);
        assertEquals("APPROVED", invoice.getStatusString());
    }

    @Test
    void approve_ShouldSetStatusToApproved() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, TEST_CATEGORY, Status.PROCESSING, TEST_URL, TEST_DATETIME, 3.0);
        invoice.approve();
        assertEquals(Status.APPROVED, invoice.getStatus());
    }

    @Test
    void declined_ShouldSetStatusToDeclined() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, TEST_CATEGORY, Status.PROCESSING, TEST_URL, TEST_DATETIME, 3.0);
        invoice.declined();
        assertEquals(Status.DECLINED, invoice.getStatus());
    }

    @Test
    void correct_ShouldUpdateFieldsAndSetStatusToProcessing() {
        Invoice invoice = new Invoice(TEST_EMAIL, TEST_DATE, 100.0, TEST_CATEGORY, Status.DECLINED, TEST_URL, TEST_DATETIME, 3.0);
        LocalDate newDate = LocalDate.of(2025, 5, 20);
        invoice.correct(80.0, Category.RESTAURANT, newDate);

        assertEquals(80.0, invoice.getAmount());
        assertEquals(Category.RESTAURANT, invoice.getCategory());
        assertEquals(newDate, invoice.getDate());
        assertEquals(Status.PROCESSING, invoice.getStatus());
    }

    @Test
    void toString_ShouldReturnFormattedInvoiceString() {
        LocalDate testDate = LocalDate.of(2025, 6, 11);
        Invoice invoice = new Invoice(TEST_EMAIL, testDate, 123.45, TEST_CATEGORY, Status.PROCESSING, TEST_URL, TEST_DATETIME, 3.0);

        String expected = "11.06.2025 | 123,45€ | PROCESSING".replace(",", ".");
        assertEquals(expected, invoice.toString().replace(",", "."));
    }
}