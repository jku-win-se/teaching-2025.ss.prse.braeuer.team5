

import jku.se.Invoice;
import jku.se.Category;
import jku.se.Status;
import jku.se.User;
import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvoiceRepositoryTest {

    private InvoiceRepository invoiceRepository;

    public static final String USER = "postgres.dljjtuynbgxgmhkcdypu";
    public static final String PWD = "LunchifyTeam5!";
    private static final String URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";

    @BeforeEach
    void setUp() {
        invoiceRepository = new InvoiceRepository(); 
        cleanUpTestInvoices();

        //before add user to connect with test invoices
        User testuser1 = new User("testuser1", "testuser1@lunchify.com", "test123", false);
        UserRepository.addUser(testuser1);
        User testuser2 = new User("testuser2", "testuser2@lunchify.com", "test123", false);
        UserRepository.addUser(testuser2);
        // Create invoice instances to calculate refund amounts
        Invoice firstInvoice = new Invoice("testuser1@lunchify.com", LocalDate.of(2025, 3, 11),
                23.99, Category.RESTAURANT, Status.PROCESSING, "https://example.com/file.pdf",
                LocalDateTime.now(), Category.RESTAURANT.getRefundAmount());

        Invoice secondInvoice = new Invoice("testuser2@lunchify.com", LocalDate.of(2025, 4, 2),
                30.00, Category.SUPERMARKET, Status.PROCESSING, "https://example.com/file.pdf2",
                LocalDateTime.now(), Category.SUPERMARKET.getRefundAmount());

        Invoice thirdInvoice = new Invoice("testuser2@lunchify.com", LocalDate.of(2025, 4, 22),
                2.50, Category.SUPERMARKET, Status.APPROVED, "https://example.com/file.pdf2",
                LocalDateTime.now(), Category.SUPERMARKET.getRefundAmount());

        // Add test invoices to DB
        insertTestInvoice(firstInvoice.getUserEmail(), firstInvoice.getDate(), firstInvoice.getAmount(),
                firstInvoice.getCategory(), firstInvoice.getStatus(), firstInvoice.getFile_Url(),
                firstInvoice.getCreatedAt(), firstInvoice.calculateRefund());

        insertTestInvoice(secondInvoice.getUserEmail(), secondInvoice.getDate(), secondInvoice.getAmount(),
                secondInvoice.getCategory(), secondInvoice.getStatus(), secondInvoice.getFile_Url(),
                secondInvoice.getCreatedAt(), secondInvoice.calculateRefund());

        insertTestInvoice(thirdInvoice.getUserEmail(), thirdInvoice.getDate(), thirdInvoice.getAmount(),
                thirdInvoice.getCategory(), thirdInvoice.getStatus(), thirdInvoice.getFile_Url(),
                thirdInvoice.getCreatedAt(), thirdInvoice.calculateRefund());

    }

    @AfterEach
    void tearDown() {
        cleanUpTestInvoices();
        //delete test user after invoice test
        UserRepository.deleteUser("testuser1@lunchify.com");
        UserRepository.deleteUser("testuser2@lunchify.com");

    }

    private void cleanUpTestInvoices() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PWD)) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM invoice WHERE user_email LIKE 'testuser1@lunchify.com'")) {
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM invoice WHERE user_email LIKE 'testuser2@lunchify.com'")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Test
    void testGetAllInvoicesAdmin() {


        List<Invoice> invoices = invoiceRepository.getAllInvoicesAdmin();

        assertNotNull(invoices, "Invoices list should not be null");
        assertTrue(invoices.size() >= 3, "There should be at least three invoices in total.");


        Invoice firstInvoice = invoices.stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        Invoice secondInvoice = invoices.stream()
                .filter(i -> i.getUserEmail().equals("testuser2@lunchify.com"))
                .findFirst()
                .orElse(null);

        Invoice thirdInvoice = invoices.stream()
                .filter(i -> i.getUserEmail().equals("testuser2@lunchify.com"))
                .skip(1)
                .findFirst()
                .orElse(null);


        assertNotNull(firstInvoice, "The first invoice should exist.");
        assertEquals(LocalDate.of(2025, 3, 11), firstInvoice.getDate());
        assertEquals(23.99, firstInvoice.getAmount());
        assertEquals(Category.RESTAURANT, firstInvoice.getCategory());
        assertEquals(Status.PROCESSING.name(), firstInvoice.getStatusString());
        assertEquals("https://example.com/file.pdf", firstInvoice.getFile_Url());
        assertEquals(firstInvoice.getCategory().getRefundAmount(), firstInvoice.getReimbursement(), 0.01);

        assertNotNull(secondInvoice, "The second invoice should exist.");
        assertEquals(LocalDate.of(2025, 4, 2), secondInvoice.getDate());
        assertEquals(30.00, secondInvoice.getAmount());
        assertEquals(Category.SUPERMARKET, secondInvoice.getCategory());
        assertEquals(Status.PROCESSING.name(), secondInvoice.getStatusString());
        assertEquals("https://example.com/file.pdf2", secondInvoice.getFile_Url());
        assertEquals(secondInvoice.getCategory().getRefundAmount(), secondInvoice.getReimbursement(), 0.01);

        assertNotNull(thirdInvoice, "The third invoice should exist.");
        assertEquals(LocalDate.of(2025, 4, 22), thirdInvoice.getDate());
        assertEquals(2.50, thirdInvoice.getAmount());
        assertEquals(Category.SUPERMARKET, thirdInvoice.getCategory());
        assertEquals(Status.APPROVED.name(), thirdInvoice.getStatusString());
        assertEquals("https://example.com/file.pdf2", thirdInvoice.getFile_Url());
        assertEquals(thirdInvoice.getCategory().getRefundAmount(), thirdInvoice.getReimbursement(), 0.01);
    }

    //ai
    @Test
    void testGetAllInvoicesUser() {
        // Fetch invoices for the user
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesUser("testuser2@lunchify.com");

        // Ensure there are invoices to test
        assertNotNull(invoices, "Invoices list should not be null");
        assertTrue(invoices.size() >= 2, "There should be at least two invoices for this user.");

        // Sort invoices by date
        invoices.sort(Comparator.comparing(Invoice::getDate));

        // Check the first invoice
        Invoice firstInvoice = invoices.get(0);
        assertNotNull(firstInvoice, "The first invoice should exist.");
        assertEquals(LocalDate.of(2025, 4, 2), firstInvoice.getDate());
        assertEquals(30.00, firstInvoice.getAmount());
        assertEquals(Category.SUPERMARKET, firstInvoice.getCategory());
        assertEquals(Status.PROCESSING.name(), firstInvoice.getStatusString());
        assertEquals("https://example.com/file.pdf2", firstInvoice.getFile_Url());
        assertEquals(2.5, firstInvoice.getCategory().getRefundAmount());

        // Check the second invoice
        Invoice secondInvoice = invoices.get(1);
        assertNotNull(secondInvoice, "The second invoice should exist.");
        assertEquals(LocalDate.of(2025, 4, 22), secondInvoice.getDate());
        assertEquals(2.50, secondInvoice.getAmount());
        assertEquals(Category.SUPERMARKET, secondInvoice.getCategory());
        assertEquals(Status.APPROVED.name(), secondInvoice.getStatusString());
        assertEquals("https://example.com/file.pdf2", secondInvoice.getFile_Url());
        assertEquals(2.5, secondInvoice.getCategory().getRefundAmount());
    }


    //ai generated
    @Test
    void testAdminApprovesInvoice() {
        //choice invoice to change
        Invoice invoice = InvoiceRepository.getAllInvoicesAdmin().stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(invoice, "The invoice should exist");
        //check if status is processing
        assertEquals(Status.PROCESSING, invoice.getStatus(), "The status should be ‘PROCESSING’ at the beginning");

        // approve invoice
        invoice.setStatus(Status.APPROVED);
        InvoiceRepository.updateInvoiceStatus(invoice);
        // search for invoice again
        Invoice updatedInvoice = InvoiceRepository.getAllInvoicesAdmin().stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedInvoice, "The updated invoice should exist");

        // Check whether the status of the invoice has been set to ‘APPROVED’
        assertEquals(Status.APPROVED, updatedInvoice.getStatus(), "The status should be ‘APPROVED’ after authorisation");

        System.out.println("Invoice status after authorisation: " + updatedInvoice.getStatus());
    }

    @Test
    void testAdminDeclinedInvoice() {
        //choice invoice to change
        Invoice invoice = InvoiceRepository.getAllInvoicesAdmin().stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(invoice, "The invoice should exist");
        //check if status is not declined
        assertNotEquals(Status.DECLINED, invoice.getStatus(), "The status should not be ‘DECLINED’ at the beginning");

        // declined invoice
        invoice.setStatus(Status.DECLINED);
        InvoiceRepository.updateInvoiceStatus(invoice);

        // search for invoice again
        Invoice updatedInvoice = InvoiceRepository.getAllInvoicesAdmin().stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedInvoice, "The updated invoice should exist");

        // check if status is declined
        assertEquals(Status.DECLINED, updatedInvoice.getStatus(), "The status should be ‘DECLINED’ after rejection");

        System.out.println("Invoice status after rejection: " + updatedInvoice.getStatus());
    }


    @Test
    void testUpdateInvoiceAmountReimbursement() {
        Invoice invoice = InvoiceRepository.getAllInvoicesAdmin().stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(invoice);

        double newAmount = 1.99;
        invoice.setAmount(newAmount);
        double expectedReimbursement = invoice.calculateRefund();
        invoice.setReimbursement(expectedReimbursement);

        InvoiceRepository.updateInvoiceAmount(invoice);
        InvoiceRepository.updateInvoiceReimbursement(invoice);

        Invoice updated = InvoiceRepository.getAllInvoicesAdmin().stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(updated);
        assertEquals(newAmount, updated.getAmount(), 0.01);
        assertEquals(expectedReimbursement, updated.getReimbursement(), 0.01);
    }

    @Test
    void testUpdateInvoiceCategoryReimbursement() {
        Invoice invoice = InvoiceRepository.getAllInvoicesAdmin().stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(invoice);

        Category newCategory = Category.SUPERMARKET;
        invoice.setCategory(newCategory);
        double expectedReimbursement = newCategory.getRefundAmount();
        invoice.setReimbursement(expectedReimbursement);

        InvoiceRepository.updateInvoiceCategory(invoice);
        InvoiceRepository.updateInvoiceReimbursement(invoice);

        Invoice updated = InvoiceRepository.getAllInvoicesAdmin().stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(updated);
        assertEquals(newCategory, updated.getCategory());
        assertEquals(expectedReimbursement, updated.getReimbursement(), 0.01);
    }

    /*@Test MagSal
    void testUpdateInvoiceDate() throws SQLException {
        Invoice invoice = InvoiceRepository.getAllInvoicesAdmin().stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(invoice);

        LocalDate oldDate = invoice.getDate();
        LocalDate newDate = LocalDate.now().withDayOfMonth(15);

        while (newDate.getDayOfWeek().getValue() >= 6) {
            newDate = newDate.plusDays(1);
        }

        invoice.setDate(newDate);
        InvoiceRepository.updateInvoiceDate(invoice); // ← Wichtig

        Invoice updated = InvoiceRepository.getAllInvoicesAdmin().stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(updated);
        assertEquals(newDate, updated.getDate());
    }
     */


   /* @Test
    void testUserCannotSubmitMultipleInvoicesOnSameDay() throws Exception {
        String userEmail = "testuser1@lunchify.com";
        LocalDate sameDate = LocalDate.now();

        File dummyFile = File.createTempFile("dummy", ".pdf");
        dummyFile.deleteOnExit();
        String dummyPath = dummyFile.getAbsolutePath();

        Invoice invoice1 = new Invoice(
                userEmail,
                sameDate,
                20.00,
                Category.RESTAURANT,
                Status.PROCESSING,
                dummyPath,
                LocalDateTime.now(),
                3.0
        );

        Invoice invoice2 = new Invoice(
                userEmail,
                sameDate,
                25.00,
                Category.SUPERMARKET,
                Status.PROCESSING,
                dummyPath,
                LocalDateTime.now(),
                2.5
        );

        // Erste Verbindung: Erste Rechnung einfügen
        try (Connection connection1 = DriverManager.getConnection(URL, USER, PWD)) {
            assertDoesNotThrow(() ->
                            InvoiceRepository.saveInvoiceWithDuplicationCheck(connection1, invoice1),
                    "First invoice should be inserted successfully"
            );
        }

        // Zweite Verbindung: Zweite Rechnung – sollte fehlschlagen
        try (Connection connection2 = DriverManager.getConnection(URL, USER, PWD)) {
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                            InvoiceRepository.saveInvoiceWithDuplicationCheck(connection2, invoice2),
                    "Submitting second invoice on same day should throw an exception"
            );
            assertTrue(exception.getMessage().contains("already submitted"), "Expected duplication error message");
        }
    }
    */

    private void insertTestInvoice(String userEmail, LocalDate date, double amount, Category category, Status status, String fileUrl, LocalDateTime createdAt, double reimbursement) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PWD)) {
            String sql = "INSERT INTO invoice (user_email, date, amount, category, status, file_url, created_at, reimbursement) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userEmail);
                statement.setDate(2, Date.valueOf(date));
                statement.setDouble(3, amount);
                statement.setString(4, category.name());
                statement.setString(5, status.name());
                statement.setString(6, fileUrl);
                statement.setTimestamp(7, Timestamp.valueOf(createdAt));
                statement.setDouble(8, reimbursement);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error when inserting the test invoice into the database.");
        }
    }
}