package jku.se.repository;

import jku.se.DatabaseConnection;
import jku.se.Invoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRepository { //#15 - Magdalena

    private static final String SELECT_ALL_INVOICES = "SELECT *  FROM invoice";

    public static List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SELECT_ALL_INVOICES);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Invoice invoice = new Invoice(rs.getString("user_email"),
                        rs.getDate("date").toLocalDate(),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("status"),
                        rs.getString("fileUrl"),
                        rs.getDate("createdAt").toLocalDate().atStartOfDay(),
                        rs.getDouble("reimbursement")
                );
                invoices.add(invoice);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving invoices: " + e.getMessage());
        }

        return invoices;
    }}