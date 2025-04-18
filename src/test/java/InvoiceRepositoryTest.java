

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
        // add test invoices to db
        insertTestInvoice("testuser1@lunchify.com", LocalDate.of(2025, 3, 11), 23.99, Category.RESTAURANT, Status.PROCESSING, "https://example.com/file.pdf", LocalDateTime.now(), 3);
        insertTestInvoice("testuser2@lunchify.com", LocalDate.of(2025, 4, 2), 30.00, Category.SUPERMARKET, Status.PROCESSING, "https://example.com/file.pdf2", LocalDateTime.now(), 2.5);

        // Test-Methode aufrufen
        List<Invoice> invoices = invoiceRepository.getAllInvoicesAdmin();


        // search for test invoices
        Invoice firstInvoice = invoices.stream()
                .filter(i -> i.getUserEmail().equals("testuser1@lunchify.com"))
                .findFirst()
                .orElse(null);

        Invoice secondInvoice = invoices.stream()
                .filter(i -> i.getUserEmail().equals("testuser2@lunchify.com"))
                .findFirst()
                .orElse(null);

        //check if expected data and data are the same
        assertNotNull(firstInvoice, "The first invoice should exist.");
        assertEquals(LocalDate.of(2025, 3, 11), firstInvoice.getDate());
        assertEquals(23.99, firstInvoice.getAmount());
        assertEquals(Category.RESTAURANT, firstInvoice.getCategory());
        assertEquals(Status.PROCESSING.name(), firstInvoice.getStatusString());
        assertEquals("https://example.com/file.pdf" ,firstInvoice.getFile_Url());
        assertEquals(3, firstInvoice.getCategory().getRefundAmount());

        assertNotNull(secondInvoice, "The second invoice should exist.");
        assertEquals(LocalDate.of(2025, 4, 2), secondInvoice.getDate());
        assertEquals(30.00, secondInvoice.getAmount());
        assertEquals(Category.SUPERMARKET, secondInvoice.getCategory());
        assertEquals(Status.PROCESSING.name(), secondInvoice.getStatusString());
        assertEquals("https://example.com/file.pdf2", secondInvoice.getFile_Url());
        assertEquals(2.5, secondInvoice.getCategory().getRefundAmount());
    }

    @Test
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