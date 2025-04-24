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


public class InvoiceRepository {

    private static final String SELECT_ALL_INVOICES = "SELECT * FROM invoice";
    private static final String SELECT_ALL_INVOICES_USER = "SELECT * FROM invoice WHERE user_email = ?";
    private static final String UPDATE_REIMBURSEMENT = "UPDATE invoice SET reimbursement = ? WHERE user_email = ? AND date = ?";
    private static final String FIND_BY_ID = "SELECT * FROM invoice WHERE id = ?";
    private static final String FIND_BY_USER = "SELECT * FROM invoice WHERE user_email = ?";
    private static final String UPDATE_CATEGORY_REFUND = "UPDATE invoice SET reimbursement = ? WHERE category = ? AND status = 'PENDING'";
    private static final String UPDATE_AMOUNT = "UPDATE invoice SET amount = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_STATUS = "UPDATE invoice SET status = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_DATE = "UPDATE invoice SET date = ? WHERE user_email = ? AND date = ?";
    private static final String UPDATE_CATEGORY = "UPDATE invoice SET category = ? WHERE user_email = ? AND date=?";
    public static boolean TEST_MODE = false;



    //admin view includes all invoices also for Statistics
    public static List<Invoice> getAllInvoicesAdmin() { //#15-Magda
        List<Invoice> invoices = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SELECT_ALL_INVOICES);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                invoices.add(createInvoiceFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving invoices: " + e.getMessage());
        }

        return invoices;
    }


    //user view includes only their invoices
    public static List<Invoice> getAllInvoicesUser(String userEmail) { //#9-Magda
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
            System.err.println("Error retrieving invoices: " + e.getMessage());
        }

        return invoices;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CHECK / UPLOAD  OF INVOICE FILES AND INFORMATION -----------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // AI generated-------------------------------------------------------------------------------------------------------------------------------------------------------------

    // Save the invoice data into the database
    public static void saveInvoiceInfo(Connection connection, String user_email, Date date, double amount, Category category, Status status, String file_url, LocalDateTime createdAt, double reimbursement, File imageFile) {
        String INSERT_INVOICE_INFO_SQL = "INSERT INTO invoice (user_email, date, amount, category, status, file_url, created_at, reimbursement) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false); // Deaktiviere Auto-Commit für Transaktionen


            // Hier lade die Datei hoch und erhalte die URL
            String fileUrl = DatabaseConnection.uploadFileToBucket(imageFile);
            if (fileUrl == null) {
                throw new RuntimeException("File url could not be uploaded");
            }

            // Verwende try-with-resources, um das PreparedStatement automatisch zu schließen
            try (PreparedStatement stmt = connection.prepareStatement(INSERT_INVOICE_INFO_SQL)) {
                stmt.setString(1, user_email);
                stmt.setDate(2, date);
                stmt.setDouble(3, amount);
                stmt.setString(4, category.name());
                stmt.setString(5, status.name());
                stmt.setString(6, file_url);  // Speichern der URL
                stmt.setObject(7, createdAt);
                stmt.setDouble(8, reimbursement);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    connection.commit();  // Commit der Transaktion, wenn erfolgreich
                } else {
                    connection.rollback();  // Rollback, wenn keine Zeilen betroffen sind
                }
            } catch (SQLException e) {
                connection.rollback();  // Rollback bei Fehler im PreparedStatement
                System.err.println("Database error: " + e.getMessage());
            }
        } catch (SQLException | IOException e) {
            try {
                connection.rollback();  // Rollback bei Fehler in der Verbindung oder Dateioperation
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);  // Stelle Auto-Commit wieder her
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean invoiceExists(Connection connection, String user_email, Date date) {
        String sql = "SELECT 1 FROM invoice WHERE user_email = ? AND date = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user_email);
            pstmt.setDate(2, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Gibt true zurück, wenn ein Eintrag existiert
            }
        } catch (SQLException e) {
            System.out.println("Error while searching: " + e.getMessage());
        }
        return false;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ENDS HERE --------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

    //change invoice date
    public static void updateInvoiceDate(Invoice invoice, LocalDate oldDate) {

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_DATE)) {
            stmt.setDate(1, Date.valueOf(invoice.getDate()));
            stmt.setString(2, invoice.getUserEmail());
            stmt.setDate(3, java.sql.Date.valueOf(oldDate));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating invoice date: " + e.getMessage());
        }
    }
    //change invoice amount
    public static void updateInvoiceAmount(Invoice invoice) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_AMOUNT)) {

            stmt.setDouble(1, invoice.getAmount());

            stmt.setString(2, invoice.getUserEmail());

            stmt.setDate(3, java.sql.Date.valueOf(invoice.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating invoice amount: " + e.getMessage());
        }
    }

    //change invoice status
    public static void updateInvoiceStatus(Invoice invoice) {

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_STATUS)) {

            stmt.setString(1, invoice.getStatus().name());
            stmt.setString(2, invoice.getUserEmail());
            stmt.setDate(3, java.sql.Date.valueOf(invoice.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating invoice status: " + e.getMessage());
        }
    }

    //change invoice category
    public static void updateInvoiceCategory(Invoice invoice) {


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CATEGORY)) {

            stmt.setString(1, invoice.getCategory().name());
            stmt.setString(2, invoice.getUserEmail());
            stmt.setDate(3, java.sql.Date.valueOf(invoice.getDate()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating invoice category: " + e.getMessage());
        }
    }
   //change reimbursement
    public static void updateInvoiceReimbursement(Invoice invoice) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_REIMBURSEMENT)) {

            stmt.setDouble(1, invoice.getReimbursement());

            stmt.setString(2, invoice.getUserEmail());

            stmt.setDate(3, java.sql.Date.valueOf(invoice.getDate()));
            stmt.executeUpdate();
            } catch (SQLException e) {
            System.err.println("Error updating invoice amount: " + e.getMessage());
        }
    }


    public static boolean deleteInvoiceIfEditable(int invoiceId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM invoice WHERE id = ?")) {

            stmt.setInt(1, invoiceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDate date = createdAt.toLocalDate();
                LocalDateTime endOfMonth = date.withDayOfMonth(date.lengthOfMonth()).atTime(23, 59);

                // Wenn wir uns noch im selben Monat befinden (also vor Monatsende)
                if (LocalDateTime.now().isBefore(endOfMonth)) {
                    try (PreparedStatement deleteStmt = con.prepareStatement("DELETE FROM invoice WHERE id = ?")) {
                        deleteStmt.setInt(1, invoiceId);
                        int rowsAffected = deleteStmt.executeUpdate();
                        return rowsAffected > 0;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveInvoiceWithDuplicationCheck(Connection connection, Invoice invoice) {
        if (invoiceExists(connection, invoice.getUserEmail(), java.sql.Date.valueOf(invoice.getDate()))) {
            throw new RuntimeException("Invoice already submitted for this date");
        }

        // Wenn kein Duplikat, dann normal speichern
        saveInvoiceInfo(
                connection,
                invoice.getUserEmail(),
                java.sql.Date.valueOf(invoice.getDate()),
                invoice.getAmount(),
                invoice.getCategory(),
                invoice.getStatus(),
                invoice.getFile_Url(),
                invoice.getCreatedAt(),
                invoice.getReimbursement(),
                new File(invoice.getFile_Url())
        );
    }
}