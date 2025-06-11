import jku.se.AnomalyDetection;
import jku.se.Category;
import jku.se.Invoice;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class AnomalyDetectionTest {
    @Test
    void testDetectMismatch_AllMatching() {
        Invoice invoice = new Invoice();
        invoice.setDate(LocalDate.of(2024, 6, 1));
        invoice.setAmount(19.99);
        invoice.setCategory(Category.SUPERMARKET);
        invoice.ocrDate = "2024-06-01";
        invoice.ocrAmount = "19.99";
        invoice.ocrCategory = "supermarket";

        assertFalse(AnomalyDetection.detectMismatch(invoice));
    }

    @Test
    void testDetectMismatch_DateMismatch(){
        Invoice invoice = new Invoice();
        invoice.setDate(LocalDate.of(2025, 6, 1));
        invoice.ocrDate = "2025/05/30";
        invoice.setAmount(10.0);
        invoice.setCategory(Category.SUPERMARKET);

        assertTrue(AnomalyDetection.detectMismatch(invoice));
    }

    @Test
    void testDetectMismatch_AmountMismatch() {
        Invoice invoice = new Invoice();
        invoice.setAmount(15.50);
        invoice.ocrAmount = "20,00";
        invoice.setDate(LocalDate.now());
        invoice.setCategory(Category.RESTAURANT);

        assertTrue(AnomalyDetection.detectMismatch(invoice));
    }

    @Test
    void testDetectMismatch_CategoryMismatch() {
        Invoice invoice = new Invoice();
        invoice.setCategory(Category.SUPERMARKET);
        invoice.ocrCategory = "fuel";
        invoice.setAmount(5.0);
        invoice.setDate(LocalDate.now());

        assertTrue(AnomalyDetection.detectMismatch(invoice));
    }

    @Test
    void testDetectMismatch_AmountParseError() {
        Invoice invoice = new Invoice();
        invoice.setAmount(0.0);
        invoice.ocrAmount = "invalid€text";
        invoice.setDate(LocalDate.now());
        invoice.setCategory(Category.SUPERMARKET);

        assertFalse(AnomalyDetection.detectMismatch(invoice)); // treated as 0.0 == 0.0
    }

    @Test
    void testDetectMismatch_NullOCRFields() {
        Invoice invoice = new Invoice();
        invoice.setAmount(10.0);
        invoice.setDate(LocalDate.of(2024, 1, 1));
        invoice.setCategory(Category.RESTAURANT);
        invoice.ocrDate = null;
        invoice.ocrAmount = null;
        invoice.ocrCategory = null;

        assertFalse(AnomalyDetection.detectMismatch(invoice));
    }
}
