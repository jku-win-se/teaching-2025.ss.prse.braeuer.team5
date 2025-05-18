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

public class InvoiceRepository {

    private static final Logger LOGGER = Logger.getLogger(InvoiceRepository.class.getName());

    private static final String SELECT_ALL_INVOICES = "SELECT * FROM invoice";
    private static final String SELECT_ALL_INVOICES_USER = "SELECT * FROM invoice WHERE user_email = ?";
    private static final String UPDATE_REIMBURSEMENT = "UPDATE invoice SET reimbursement = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_AMOUNT = "UPDATE invoice SET amount = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_STATUS = "UPDATE invoice SET status = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_DATE = "UPDATE invoice SET date = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_CATEGORY = "UPDATE invoice SET category = ? WHERE user_email = ? AND date = ?";

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

    private static String getCurrentMonthCondition() {
        return "EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                "AND EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM CURRENT_DATE)";
    }

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

    public static int getInvoiceCountForUserThisMonth(String userEmail) throws SQLException {
        String sql = "SELECT COUNT(*) FROM invoice WHERE user_email = ? AND " + getCurrentMonthCondition();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static double getTotalReimbursementForUserThisMonth(String userEmail) throws SQLException {
        String sql = "SELECT SUM(reimbursement) FROM invoice WHERE user_email = ? AND " + getCurrentMonthCondition();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    public static double getTotalReimbursementThisMonth() throws SQLException {
        String sql = "SELECT SUM(reimbursement) FROM invoice WHERE " + getCurrentMonthCondition();

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }
}
