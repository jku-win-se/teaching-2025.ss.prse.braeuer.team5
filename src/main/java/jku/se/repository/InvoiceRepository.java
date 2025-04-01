package jku.se.repository;

import jku.se.DatabaseConnection;
import jku.se.Invoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRepository {

    private static final String SELECT_ALL_INVOICES = "SELECT * FROM invoices";

    public static List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SELECT_ALL_INVOICES);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Invoice invoice = new Invoice(rs.getInt("id"),
                        rs.getString("user_email"),
                        rs.getDate("submission_date").toLocalDate(),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("status")
                );
                invoices.add(invoice);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving invoices: " + e.getMessage());
        }

        return invoices;
    }}