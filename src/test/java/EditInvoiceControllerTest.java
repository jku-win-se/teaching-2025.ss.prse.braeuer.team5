import jku.se.Category;
import jku.se.DatabaseConnection;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EditInvoiceControllerTest {

    private Connection connection;
    private Invoice testInvoice;

    @BeforeEach
    public void setUp() throws Exception {
        // Establish connection to the database
        connection = DatabaseConnection.getConnection();

        // Clean up any existing data with the same user_email and date
        String deleteQuery = "DELETE FROM invoice WHERE user_email = ? AND date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setString(1, "max@lunchify.com");
            stmt.setDate(2, Date.valueOf(LocalDate.of(2025, 4, 1)));
            stmt.executeUpdate();
        }

        // Create a new test invoice with a unique email and date
        testInvoice = new Invoice(
                "max@lunchify.com",
                LocalDate.of(2025, 4, 1),
                100.00,
                Category.RESTAURANT,
                Status.PROCESSING,
                "testfile_url",
                LocalDateTime.now(),
                2.5
        );

        // Insert the test invoice into the database
        String insertQuery = "INSERT INTO invoice (user_email, date, amount, category, status, file_url, created_at, reimbursement) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setString(1, testInvoice.getUserEmail());
            stmt.setDate(2, Date.valueOf(testInvoice.getDate()));
            stmt.setDouble(3, testInvoice.getAmount());
            stmt.setString(4, testInvoice.getCategory().name());
            stmt.setString(5, testInvoice.getStatus().name());
            stmt.setString(6, testInvoice.getFile_Url());
            stmt.setObject(7, testInvoice.getCreatedAt());
            stmt.setDouble(8, testInvoice.getReimbursement());
            stmt.executeUpdate();
        }
    }

    @Test
    public void testUpdateInvoiceStatusToProcessing() throws Exception {
        // Update the invoice status
        testInvoice.setStatus(Status.PROCESSING);
        InvoiceRepository.updateInvoiceStatus(testInvoice);

        // Retrieve the updated invoice from the database
        String selectQuery = "SELECT * FROM invoice WHERE user_email = ? AND date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
            stmt.setString(1, testInvoice.getUserEmail());
            stmt.setDate(2, Date.valueOf(testInvoice.getDate()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String updatedStatus = rs.getString("status");
                    assertEquals("PROCESSING", updatedStatus);
                }
            }
        }
    }

    @Test
    public void testUpdateInvoiceAmount() throws Exception {
        // Update the invoice amount
        testInvoice.setAmount(200.00);
        InvoiceRepository.updateInvoiceAmount(testInvoice);

        // Retrieve the updated invoice from the database
        String selectQuery = "SELECT * FROM invoice WHERE user_email = ? AND date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
            stmt.setString(1, testInvoice.getUserEmail());
            stmt.setDate(2, Date.valueOf(testInvoice.getDate()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double updatedAmount = rs.getDouble("amount");
                    assertEquals(200.00, updatedAmount);
                }
            }
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Clean up test data from the database
        String deleteQuery = "DELETE FROM invoice WHERE user_email = ? AND date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setString(1, testInvoice.getUserEmail());
            stmt.setDate(2, Date.valueOf(testInvoice.getDate()));
            stmt.executeUpdate();
        }

        // Close the connection
        if (connection != null) {
            connection.close();
        }
    }
}
