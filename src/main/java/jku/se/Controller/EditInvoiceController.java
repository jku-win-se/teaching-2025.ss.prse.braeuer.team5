package jku.se.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.DatabaseConnection;
import jku.se.repository.InvoiceRepository;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EditInvoiceController {
    private Invoice selectedInvoice;

    @FXML private ComboBox<Invoice> invoiceComboBox;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> categoryComboBox;

    private static final String SELECT_REJECTED_CURRENT_MONTH =
            "SELECT * FROM invoice WHERE user_email = ? " +
                    "AND status = 'DECLINED' ";

    @FXML
    public void initialize() {
        System.out.println("Initializing EditInvoiceController...");

        try (Connection con = DatabaseConnection.getConnection()) {
            System.out.println("Database connection established");

            String userEmail = UserDashboardController.getCurrentUserEmail();
            System.out.println("Current user: " + userEmail);

            if (userEmail == null || userEmail.isEmpty()) {
                showAlert("Error", "No user logged in");
                return;
            }

            // Test: Manuelle Datumsberechnung zur Überprüfung
            LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate firstDayNextMonth = firstDayOfMonth.plusMonths(1);
            System.out.println("Date range for query: " + firstDayOfMonth + " to " + firstDayNextMonth);

            try (PreparedStatement stmt = con.prepareStatement(SELECT_REJECTED_CURRENT_MONTH)) {
                stmt.setString(1, userEmail);

                // Debug: Ausgabe der tatsächlich ausgeführten Query
                System.out.println("Executing query: " + stmt.toString());

                ResultSet rs = stmt.executeQuery();
                List<Invoice> rejectedInvoices = new ArrayList<>();

                while (rs.next()) {
                    LocalDate invoiceDate = rs.getDate("date").toLocalDate();
                    System.out.println("Found invoice: " + invoiceDate + " | " + rs.getString("status"));

                    rejectedInvoices.add(new Invoice(
                            rs.getString("user_email"),
                            invoiceDate,
                            rs.getDouble("amount"),
                            Category.valueOf(rs.getString("category")),
                            Status.valueOf(rs.getString("status")),
                            rs.getString("file_url"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getDouble("reimbursement")
                    ));
                }

                if (rejectedInvoices.isEmpty()) {
                    System.out.println("No matching invoices found");
                    showAlert("Info", "No rejected invoices found for current month");
                    return;
                }

                // ComboBox befüllen
                Platform.runLater(() -> {
                    invoiceComboBox.setItems(FXCollections.observableArrayList(rejectedInvoices));
                    invoiceComboBox.setConverter(new StringConverter<Invoice>() {
                        @Override
                        public String toString(Invoice invoice) {
                            return invoice != null ?
                                    String.format("%s | %.2f€ | %s",
                                            invoice.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                                            invoice.getAmount(),
                                            invoice.getCategory()) : "";
                        }

                        @Override
                        public Invoice fromString(String string) {
                            return null;
                        }
                    });
                    System.out.println("ComboBox populated with " + rejectedInvoices.size() + " items");
                });

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Invoice> loadInvoicesFromResultSet(ResultSet rs) throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        while (rs.next()) {
            invoices.add(new Invoice(
                    rs.getString("user_email"),
                    rs.getDate("date").toLocalDate(),
                    rs.getDouble("amount"),
                    Category.valueOf(rs.getString("category")),
                    Status.valueOf(rs.getString("status")),
                    rs.getString("file_url"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getDouble("reimbursement")
            ));
        }
        return invoices;
    }

    private void configureComboBox(List<Invoice> invoices) {
        ObservableList<Invoice> observableList = FXCollections.observableArrayList(invoices);
        invoiceComboBox.setItems(observableList);

        // Anzeigeformat für ComboBox
        invoiceComboBox.setConverter(new StringConverter<Invoice>() {
            @Override
            public String toString(Invoice invoice) {
                if (invoice == null) return "";
                return String.format("%s | %.2f€ | %s",
                        invoice.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        invoice.getAmount(),
                        invoice.getCategory());
            }

            @Override
            public Invoice fromString(String string) {
                return null;
            }
        });

        // Listener für Auswahl
        invoiceComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedInvoice = newVal;
                        amountField.setText(String.format("%.2f", newVal.getAmount()));
                        datePicker.setValue(newVal.getDate());
                        categoryComboBox.setValue(newVal.getCategory().name());
                    }
                });
    }

    @FXML
    private void handleSaveChanges(ActionEvent event) {
        if (selectedInvoice == null) {
            showAlert("Error", "No invoice selected");
            return;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            // Werte aktualisieren
            selectedInvoice.setAmount(Double.parseDouble(amountField.getText()));
            selectedInvoice.setDate(datePicker.getValue());
            selectedInvoice.setCategory(Category.valueOf(categoryComboBox.getValue()));
            selectedInvoice.setStatus(Status.PROCESSING); // Status ändern

            // Datenbank aktualisieren
            updateInvoiceInDatabase(con, selectedInvoice);

            con.commit();
            showAlert("Success", "Invoice updated successfully");
            returnToDashboard(event);

        } catch (Exception e) {
            showAlert("Error", "Failed to update invoice: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateInvoiceInDatabase(Connection con, Invoice invoice) throws SQLException {
        String updateSql = "UPDATE invoice SET amount = ?, date = ?, category = ?, status = ? " +
                "WHERE user_email = ? AND date = ?";

        try (PreparedStatement stmt = con.prepareStatement(updateSql)) {
            stmt.setDouble(1, invoice.getAmount());
            stmt.setDate(2, Date.valueOf(invoice.getDate()));
            stmt.setString(3, invoice.getCategory().name());
            stmt.setString(4, invoice.getStatus().name());
            stmt.setString(5, invoice.getUserEmail());
            stmt.setDate(6, Date.valueOf(invoice.getDate()));
            stmt.executeUpdate();
        }
    }

    @FXML
    private void cancelEdit(ActionEvent event) {
        returnToDashboard(event);
    }

    private void returnToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard1.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to return to dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}