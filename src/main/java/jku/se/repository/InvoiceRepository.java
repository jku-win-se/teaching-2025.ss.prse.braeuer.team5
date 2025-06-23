package jku.se.repository;

import jku.se.Category;
import jku.se.DatabaseConnection;
import jku.se.Invoice;
import jku.se.Status;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for handling database operations related to invoices.
 * Provides methods to retrieve, update, insert, and delete invoice data.
 */
public class InvoiceRepository {

    private static final Logger LOGGER = Logger.getLogger(InvoiceRepository.class.getName());

    private static final String SELECT_ALL_INVOICES = "SELECT * FROM invoice";
    private static final String SELECT_ALL_INVOICES_USER = "SELECT * FROM invoice WHERE user_email = ?";
    private static final String UPDATE_REIMBURSEMENT = "UPDATE invoice SET reimbursement = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_AMOUNT = "UPDATE invoice SET amount = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_STATUS = "UPDATE invoice SET status = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_DATE = "UPDATE invoice SET date = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_CATEGORY = "UPDATE invoice SET category = ? WHERE user_email = ? AND date = ?";
    private static final String DELETE_INVOICE = "DELETE FROM invoice WHERE  user_email = ? AND date = ?";

    /**
     * Retrieves all invoices from the database.
     * @return List of all invoices.
     */
    public static List<Invoice> getAllInvoicesAdmin() {
        List<Invoice> invoices = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SELECT_ALL_INVOICES);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                invoices.add(createInvoiceFromResultSet(rs));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving invoices: ", e);
        }

        return invoices;
    }

    /**
     * Retrieves all invoices for a given user.
     * @param userEmail Email address of the user.
     * @return List of invoices associated with the user.
     */
    public static List<Invoice> getAllInvoicesUser(String userEmail) {
        List<Invoice> invoices = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SELECT_ALL_INVOICES_USER)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(createInvoiceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving invoices: ", e);
        }

        return invoices;
    }

    /**
     * Saves a new invoice record in the database and uploads associated file.
     * @param connection Database connection.
     * @param userEmail User email.
     * @param date Invoice date.
     * @param amount Invoice amount.
     * @param category Invoice category.
     * @param status Invoice status.
     * @param fileUrl File URL.
     * @param createdAt Timestamp of creation.
     * @param reimbursement Reimbursement amount.
     * @param imageFile File object to upload.
     */
    public static void saveInvoiceInfo(Connection connection, String userEmail, Date date, double amount, Category category,
                                       Status status, String fileUrl, LocalDateTime createdAt, double reimbursement, File imageFile) {
        String insertInvoiceSql = "INSERT INTO invoice (user_email, date, amount, category, status, file_url, created_at, reimbursement) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            String uploadedFileUrl = DatabaseConnection.uploadFileToBucket(imageFile);
            if (uploadedFileUrl == null || uploadedFileUrl.isEmpty()) {
                throw new IOException("File upload failed or returned empty URL");
            }

            try (PreparedStatement stmt = connection.prepareStatement(insertInvoiceSql)) {
                stmt.setString(1, userEmail);
                stmt.setDate(2, date);
                stmt.setDouble(3, amount);
                stmt.setString(4, category.name());
                stmt.setString(5, status.name());
                stmt.setString(6, uploadedFileUrl);
                stmt.setObject(7, createdAt);
                stmt.setDouble(8, reimbursement);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    connection.commit();
                } else {
                    connection.rollback();
                    LOGGER.warning("No rows affected while saving invoice info, rollback performed.");
                }
            } catch (SQLException e) {
                connection.rollback();
                LOGGER.log(Level.SEVERE, "Database error during invoice save, rollback performed: ", e);
            }
        } catch (SQLException | IOException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Rollback error: ", ex);
            }
            LOGGER.log(Level.SEVERE, "Error in saveInvoiceInfo: ", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error resetting auto-commit: ", ex);
            }
        }
    }

    /**
     * Checks if an invoice already exists for a user on a given date.
     * @param connection Database connection.
     * @param userEmail Email of the user.
     * @param date Date to check.
     * @return true if invoice exists, false otherwise.
     */
    public static boolean invoiceExists(Connection connection, String userEmail, Date date) {
        String sql = "SELECT 1 FROM invoice WHERE user_email = ? AND date = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setDate(2, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while searching for invoice existence: ", e);
        }
        return false;
    }

    /**
     * Creates an Invoice object from a SQL ResultSet.
     * Maps database columns to the Invoice fields.
     * @param rs ResultSet containing invoice data.
     * @return Invoice object.
     * @throws SQLException if data extraction fails.
     */
    private static Invoice createInvoiceFromResultSet(ResultSet rs) throws SQLException {
        return new Invoice(
                rs.getString("user_email"),
                rs.getDate("date").toLocalDate(),
                rs.getDouble("amount"),
                Category.valueOf(rs.getString("category").toUpperCase()),
                Status.valueOf(rs.getString("status").toUpperCase()),
                rs.getString("file_url"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getDouble("reimbursement")
        );
    }

    /**
     * Updates the invoice date in the database.
     * @param invoice Invoice to update.
     */
    public static void updateInvoiceDate(Invoice invoice) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_DATE)) {
            stmt.setDate(1, Date.valueOf(invoice.getDate()));
            stmt.setString(2, invoice.getUserEmail());
            stmt.setDate(3, java.sql.Date.valueOf(invoice.getDate()));  // missing in your original? Added for completeness
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating invoice date: ", e);
        }
    }

    /**
     * Updates the invoice amount in the database.
     * @param invoice Invoice to update.
     */
    public static void updateInvoiceAmount(Invoice invoice) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_AMOUNT)) {
            stmt.setDouble(1, invoice.getAmount());
            stmt.setString(2, invoice.getUserEmail());
            stmt.setDate(3, java.sql.Date.valueOf(invoice.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating invoice amount: ", e);
        }
    }

    /**
     * Updates the invoice status in the database.
     * @param invoice Invoice to update.
     */
    public static void updateInvoiceStatus(Invoice invoice) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_STATUS)) {
            stmt.setString(1, invoice.getStatus().name());
            stmt.setString(2, invoice.getUserEmail());
            stmt.setDate(3, java.sql.Date.valueOf(invoice.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating invoice status: ", e);
        }
    }

    /**
     * Updates the invoice category in the database.
     * @param invoice Invoice to update.
     */
    public static void updateInvoiceCategory(Invoice invoice) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CATEGORY)) {
            stmt.setString(1, invoice.getCategory().name());
            stmt.setString(2, invoice.getUserEmail());
            stmt.setDate(3, java.sql.Date.valueOf(invoice.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating invoice category: ", e);
        }
    }

    /**
     * Updates the invoice reimbursement value in the database.
     * @param invoice Invoice to update.
     */
    public static void updateInvoiceReimbursement(Invoice invoice) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_REIMBURSEMENT)) {
            stmt.setDouble(1, invoice.getReimbursement());
            stmt.setString(2, invoice.getUserEmail());
            stmt.setDate(3, java.sql.Date.valueOf(invoice.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating invoice reimbursement: ", e);
        }
    }

    /**
     * Retrieves all declined invoices for a user in the current month.
     * @param userEmail Email of the user.
     * @return List of declined invoices.
     */
    public static List<Invoice> getDeclinedInvoicesCurrentMonth(String userEmail) {
        List<Invoice> declinedInvoices = new ArrayList<>();
        LocalDate now = LocalDate.now();

        String query = "SELECT * FROM invoice WHERE user_email = ? AND status = 'DECLINED'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = createInvoiceFromResultSet(rs);

                    if (invoice.getDate().getMonth() == now.getMonth() &&
                            invoice.getDate().getYear() == now.getYear()) {
                        declinedInvoices.add(invoice);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading declined invoices: ", e);
        }

        return declinedInvoices;
    }

    /**
     * Retrieves all accepted invoices for a user in the current month.
     * @param userEmail Email of the user.
     * @return List of accepted invoices.
     */
    public static List<Invoice> getAcceptedInvoicesCurrentMonth(String userEmail) {
        List<Invoice> acceptedInvoices = new ArrayList<>();
        LocalDate now = LocalDate.now();

        String query = "SELECT * FROM invoice WHERE user_email = ? AND status = 'APPROVED'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = createInvoiceFromResultSet(rs);

                    if (invoice.getDate().getMonth() == now.getMonth() &&
                            invoice.getDate().getYear() == now.getYear()) {
                        acceptedInvoices.add(invoice);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading accepted invoices: ", e);
        }

        return acceptedInvoices;
    }

    /**
     * Updates all editable fields of an invoice.
     * @param invoice Invoice to update.
     * @throws SQLException if database error occurs.
     */
    public static void updateInvoice(Invoice invoice) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            updateInvoiceAmount(invoice);
            updateInvoiceDate(invoice);
            updateInvoiceCategory(invoice);
            updateInvoiceStatus(invoice);
            updateInvoiceReimbursement(invoice);

            con.commit();
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Returns the SQL condition to match the current month and year.
     * Used in WHERE clauses to filter invoices of the current month.
     * @return SQL string for current month condition.
     */
    private static String getCurrentMonthCondition() {
        return "EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                "AND EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM CURRENT_DATE)";
    }

    /**
     * Retrieves a list of user emails that have submitted invoices in the current month.
     * @return List of user emails.
     * @throws SQLException if database access fails.
     */
    public static List<String> getActiveUsersThisMonth() throws SQLException {
        String sql = "SELECT DISTINCT user_email FROM invoice WHERE " + getCurrentMonthCondition();
        List<String> users = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(rs.getString("user_email"));
            }
        }
        return users;
    }

    /**
     * Counts how many invoices a user submitted in the current month.
     * @param userEmail User email.
     * @return Number of invoices.
     * @throws SQLException if database access fails.
     */
    public static int getInvoiceCountForUserThisMonth(String userEmail) throws SQLException {
        String sql = "SELECT COUNT(*) FROM invoice WHERE user_email = ? AND " + getCurrentMonthCondition();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Sums the reimbursement of all invoices for a user in the current month.
     * @param userEmail User email.
     * @return Total reimbursement amount.
     * @throws SQLException if database access fails.
     */
    public static double getTotalReimbursementForUserThisMonth(String userEmail) throws SQLException {
        String sql = "SELECT SUM(reimbursement) FROM invoice WHERE user_email = ? AND " + getCurrentMonthCondition();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    /**
     * Sums the reimbursement of all invoices in the current month.
     * @return Total reimbursement amount.
     * @throws SQLException if database access fails.
     */
    public static double getTotalReimbursementThisMonth() throws SQLException {
        String sql = "SELECT SUM(reimbursement) FROM invoice WHERE " + getCurrentMonthCondition();

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    /**
     * Deletes a specific invoice from the database.
     * @param invoice Invoice to delete.
     */
    //delete an invoice from a user as admin
    public static void deleteInvoice(Invoice invoice) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(DELETE_INVOICE)) {

            stmt.setString(1, invoice.getUserEmail());
            stmt.setDate(2, java.sql.Date.valueOf(invoice.getDate()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                LOGGER.info("Invoice deleted successfully.");
            } else {
                LOGGER.warning("No invoice found to delete.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting invoice: ", e);
        }
    }
}
