package jku.se.repository;

import jku.se.Category;
import jku.se.DatabaseConnection;
import jku.se.Invoice;
import jku.se.Status;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRepository {

    private static final String SELECT_ALL_INVOICES = "SELECT *  FROM invoice";
    private static final String SELECT_ALL_INVOICES_USER = "SELECT * FROM invoice WHERE user_email = ?";
    private static final String UPDATE_REIMBURSEMENT = "UPDATE invoice SET reimbursement = ?, status = ? WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM invoice WHERE id = ?";
    private static final String FIND_BY_USER = "SELECT * FROM invoice WHERE user_email = ?";
    private static final String UPDATE_CATEGORY_REFUND = "UPDATE invoice SET reimbursement = ? WHERE category = ? AND status = 'PENDING'";


    //admin view includes all invoices
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



    public boolean save(Invoice invoice) {
        String sql = "INSERT INTO invoice (user_email, date, amount, category, status, file_Url, created_at, reimbursement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, invoice.getUserEmail());
            stmt.setDate(2, Date.valueOf(invoice.getDate()));
            stmt.setDouble(3, invoice.getAmount());
            stmt.setString(4, invoice.getCategory().name());
            stmt.setString(5, invoice.getStatus().name());
            stmt.setString(6, invoice.getFileUrl());
            stmt.setTimestamp(7, Timestamp.valueOf(invoice.getCreatedAt()));
            stmt.setDouble(8, invoice.calculateRefund());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Fehler beim Speichern: " + e.getMessage());
            return false;
        }
    }

    public Invoice findById(String id) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(FIND_BY_ID)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createInvoiceFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding invoice: " + e.getMessage());
        }
        return null;
    }

    public List<Invoice> findByUser(String userEmail) {
        List<Invoice> invoices = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(FIND_BY_USER)) {

            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                invoices.add(createInvoiceFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding user invoices: " + e.getMessage());
        }

        return invoices;
    }

    public boolean updateReimbursement(String invoiceId, double newAmount, Status newStatus) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_REIMBURSEMENT)) {

            stmt.setDouble(1, newAmount);
            stmt.setString(2, newStatus.name());
            stmt.setString(3, invoiceId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating reimbursement: " + e.getMessage());
            return false;
        }
    }

    public int updateCategoryRefunds(Category category, double newAmount) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_CATEGORY_REFUND)) {

            stmt.setDouble(1, newAmount);
            stmt.setString(2, category.name());

            return stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating category refunds: " + e.getMessage());
            return 0;
        }
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


}