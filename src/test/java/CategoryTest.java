import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final LocalDateTime TEST_DATETIME = LocalDateTime.now();
    private static final Status TEST_STATUS = Status.PROCESSING;
    private static final String TEST_URL = "http://example.com/invoice.pdf";

    @Test
    void testSetCustomRefundAmount() {
        Category.setCustomRefundAmount(Category.RESTAURANT, 4.0);
        assertEquals(4.0, Category.RESTAURANT.customRefundAmount, "Refund amount for restaurant should be updated");
    }

    @Test
    void testSetInvalidCustomRefundAmount_ShouldThrowException() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> Category.setCustomRefundAmount(Category.RESTAURANT, -5.0)
        );

        assertEquals("Amount has to be positive", thrown.getMessage());
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
    void selectCategory_NoSelection_ShouldThrowNullPointer() {
        NullPointerException thrown = assertThrows(
                NullPointerException.class,
                () -> new Invoice(
                        TEST_EMAIL,
                        TEST_DATE,
                        100.0,
                        null,
                        TEST_STATUS,
                        TEST_URL,
                        TEST_DATETIME,
                        0.0
                )
        );

        assertEquals("Category cannot be null", thrown.getMessage());
    }

    @Test
    void testGetRefundAmount_DefaultRestaurant() {
        Category.RESTAURANT.customRefundAmount = -1;
        assertEquals(3.00, Category.RESTAURANT.getRefundAmount(), 0.001);
    }

    @Test
    void testGetRefundAmount_DefaultSupermarket() {
        Category.SUPERMARKET.customRefundAmount = -1;
        assertEquals(2.50, Category.SUPERMARKET.getRefundAmount(), 0.001);
    }

    @Test
    void testGetRefundAmount_WithCustomValue() {
        Category.setCustomRefundAmount(Category.SUPERMARKET, 5.75);
        assertEquals(5.75, Category.SUPERMARKET.getRefundAmount(), 0.001);
    }

    @org.junit.jupiter.api.AfterEach
    void resetCustomRefundAmounts() {
        Category.RESTAURANT.customRefundAmount = -1;
        Category.SUPERMARKET.customRefundAmount = -1;
    }
}
