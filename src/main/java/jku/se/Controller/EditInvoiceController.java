package jku.se.Controller;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for editing declined invoices.
 * Allows a user to select a declined invoice, change its details and resubmit it.
 */
public class EditInvoiceController {
    private Invoice invoice;

    @FXML private ComboBox<Invoice> invoiceComboBox;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Button changeButton;

    /**
     * Initializes the controller by loading declined invoices and configuring the ComboBox.
     */
    @FXML
    private void initialize() {
        String userEmail = UserDashboardController.getCurrentUserEmail();
        List<Invoice> declinedInvoices = InvoiceRepository.getDeclinedInvoicesCurrentMonth(userEmail);

        // configure ComboBox
        configureComboBox(declinedInvoices);

        // Set button text based on the number of invoices
        if (declinedInvoices.isEmpty()) {
            invoiceComboBox.setPromptText("No declined invoices");
        } else {
            invoiceComboBox.setPromptText(declinedInvoices.size() + " declined invoice(s)");
        }

        // Listener for selection
        invoiceComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        fillFields(newVal);
                    }
                });
    }

    /**
     * Fills the form fields with the selected invoice's data.
     * @param invoice the selected invoice
     */
    private void fillFields(Invoice invoice) {
        this.invoice = invoice;
        amountField.setText(String.valueOf(invoice.getAmount()));
        datePicker.setValue(invoice.getDate());
        categoryComboBox.setValue(invoice.getCategory().name());
    }

    /**
     * Loads invoices from a SQL ResultSet into a list.
     * @param rs the SQL ResultSet
     * @return list of Invoice objects
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Configures the invoice ComboBox and sets its display format.
     * @param invoices the list of declined invoices
     */
    private void configureComboBox(List<Invoice> invoices) {
        ObservableList<Invoice> observableList = FXCollections.observableArrayList(invoices);
        invoiceComboBox.setItems(observableList);

        // Display format for the dropdown list
        invoiceComboBox.setConverter(new StringConverter<Invoice>() {
            @Override
            public String toString(Invoice invoice) {
                if (invoice == null) {
                    return invoices.isEmpty() ? "No declined invoices" : invoices.size() + " declined invoice(s)";
                }
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

        // Listener for selection
        invoiceComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        invoice = newVal;
                        amountField.setText(String.format("%.2f", newVal.getAmount()));
                        datePicker.setValue(newVal.getDate());
                        categoryComboBox.setValue(newVal.getCategory().name());
                    }
                });
    }

    /**
     * Handles the save changes action.
     * Updates the invoice in the database.
     * @param event the action event
     */
    @FXML
    private void handleSaveChanges(ActionEvent event) {
        if (invoice == null) {
            showAlert("Error", String.valueOf(Alert.AlertType.valueOf("No invoice selected")));
            return;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            // update values
            invoice.setAmount(Double.parseDouble(amountField.getText()));
            invoice.setDate(datePicker.getValue());
            invoice.setCategory(Category.valueOf(categoryComboBox.getValue()));
            invoice.setStatus(Status.PROCESSING); // change status

            // update Database
            updateInvoiceInDatabase(con, invoice);

            con.commit();
            showAlert("Success", String.valueOf(Alert.AlertType.valueOf("Invoice updated successfully")));
            cancelEdit(event);

        } catch (Exception e) {
            showAlert("Error", String.valueOf(Alert.AlertType.valueOf("Failed to update invoice: " + e.getMessage())));
            e.printStackTrace();
        }
    }

    /**
     * Executes the update statement in the database.
     * @param con the database connection
     * @param invoice the invoice to update
     * @throws SQLException if a database error occurs
     */
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

    /**
     * Cancels the editing and returns to the user dashboard.
     * @param event the action event
     * @throws IOException if the dashboard cannot be loaded
     */
    @FXML
    private void cancelEdit(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard1.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", String.valueOf(Alert.AlertType.valueOf("Failed to return to dashboard: " + e.getMessage())));
        }
    }

    /**
     * Shows an alert dialog with the given title and message.
     * @param title the alert title
     * @param message the alert message
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Loads another FXML page.
     * @param fxmlFile the FXML file name
     * @param event the action event
     * @throws IOException if the FXML file cannot be loaded
     */
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles resubmitting a previously declined invoice.
     * Updates the invoice and status, and notifies the user.
     * @param event the action event
     */
    @FXML
    private void handleResubmit(ActionEvent event) {
        if (invoice == null) {
            showAlert("Error", "No invoice selected");
            return;
        }

        try {
            // Update invoice with new values
            invoice.setAmount(Double.parseDouble(amountField.getText()));
            invoice.setDate(datePicker.getValue());
            invoice.setCategory(Category.valueOf(categoryComboBox.getValue().toUpperCase()));
            invoice.setStatus(Status.PROCESSING);

            // Update in database
            InvoiceRepository.updateInvoice(invoice);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Resubmission successful");
            alert.setHeaderText(null);
            alert.setContentText("Your invoice has been resubmitted and will be reviewed again.");
            alert.showAndWait();

            // Return to dashboard
            loadPage("dashboard1.fxml", event);

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount. Please enter a valid number.");
        } catch (IllegalArgumentException e) {
            showAlert("Error", "Invalid category selected.");
        } catch (SQLException e) {
            showAlert("Error", "Failed to update invoice in database.");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred.");
            e.printStackTrace();
        }
    }
}