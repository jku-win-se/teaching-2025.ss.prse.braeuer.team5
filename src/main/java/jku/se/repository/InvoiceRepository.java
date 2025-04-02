package jku.se.repository;

import jku.se.Category;
import jku.se.DatabaseConnection;
import jku.se.Invoice;
import jku.se.Status;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class InvoiceRepository { //#15 - Magdalena

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

        //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // CHECK / UPLOAD  OF INVOICE FILES AND INFORMATION -----------------------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        private static final String INSERT_INVOICE_INFO_SQL = "INSERT INTO invoice (user_email, date, amount, category, status, file_url, created_at, reimbursement) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Save the invoice data into the database
        public static void saveInvoiceInfo(Invoice invoice) {
            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(INSERT_INVOICE_INFO_SQL)) {

                stmt.setString(1, invoice.getUserEmail());
                stmt.setObject(2, invoice.getDate());
                stmt.setDouble(3, invoice.getAmount());
                stmt.setString(4, invoice.getCategoryString());
                stmt.setString(5, invoice.getStatusString());
                stmt.setString(6, invoice.getFile_Url());
                stmt.setObject(7, invoice.getCreatedAt());
                stmt.setDouble(8, invoice.getReimbursement());

                stmt.executeUpdate();
                System.out.println("Invoice info successfully saved");
            } catch (SQLException e) {
                System.err.println("Error saving the invoice information " + e.getMessage());
            }
        }

        // Method to handle upload errors
        public static void handleUploadError(HttpURLConnection connection) throws IOException {
            System.err.println("Error uploading the file: " + connection.getResponseMessage());
            try (java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getErrorStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.err.println("Error details: " + response);
            }
        }

        // Method to check if the file already exists in the Supabase Storage
        public static boolean fileExistsInStorage(String fileUrl) {
            // Extrahiere den Dateinamen aus der URL
            String fileName = extractFileNameFromUrl(fileUrl);

            // Baue die URL zusammen, um den Speicherort der Datei im Supabase Storage zu überprüfen
            String checkUrl = "https://dljjtuynbgxgmhkcdypu.supabase.co/storage/v1/object/public/invoicefiles/" + fileName;

            // Überprüfe, ob die Datei bereits im Supabase Storage vorhanden ist
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(checkUrl).openConnection();
                connection.setRequestMethod("HEAD");  // HEAD request, um nur das Vorhandensein zu prüfen
                connection.setRequestProperty("Authorization", "Bearer " + DatabaseConnection.API_KEY);

                int responseCode = connection.getResponseCode();

                // Wenn die Datei im Storage existiert, return true (sie existiert bereits)
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("File exists in storage.");
                    return true;  // Datei existiert
                } else {
                    System.out.println("File does not exist in storage. Response code: " + responseCode);
                }
            } catch (IOException e) {
                System.err.println("Error checking file existence in storage: " + e.getMessage());
            }

            return false;  // Die Datei existiert nicht im Storage
        }

        // Extract file name from URL
        private static String extractFileNameFromUrl(String fileUrl) {
            String[] parts = fileUrl.split("/");
            return parts[parts.length - 1];  // Extrahiert den Dateinamen aus der URL
        }

        // Method to save an invoice after checking file existence
        public static void saveInvoice(Invoice invoice) {
            if (fileExistsInStorage(invoice.getFile_Url())) {
                System.out.println("Error: This file already exists in the storage.");
                return; // Abort if file exists
            }

            try {
                String uploadedFileUrl = DatabaseConnection.uploadFileToBucket(new File(invoice.getFile_Url()));
                if (uploadedFileUrl != null) {
                    invoice.setFileUrl(uploadedFileUrl);
                    saveInvoiceInfo(invoice);
                    System.out.println("Invoice and image uploaded successfully.");
                } else {
                    System.out.println("Failed to upload file.");
                }
            } catch (IOException e) {
                System.err.println("Error uploading file: " + e.getMessage());
            }
        }


        //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // ENDS HERE --------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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