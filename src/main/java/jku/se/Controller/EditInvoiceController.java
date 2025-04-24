package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;

import java.io.IOException;
import java.util.List;

public class EditInvoiceController {
    private Invoice invoice;

    @FXML
    private ComboBox<Invoice> invoiceComboBox;
    @FXML
    private TextField amountField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> classificationComboBox;
    @FXML
    private Button changeButton;

    @FXML
    private void cancelEdit(ActionEvent event) throws IOException {
        // Load the correct dashboard (adjust path if needed)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard1.fxml"));
        Scene dashboardScene = new Scene(loader.load());

        // Get the current stage and switch scenes
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(dashboardScene);
        stage.show();
    }

    // Optional: Set the invoice safely
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        // Add validation here if needed (e.g., check isEditable())
    }

    @FXML
    private void initialize() {
        String userEmail = UserDashboardController.getCurrentUserEmail();
        List<Invoice> declinedInvoices = InvoiceRepository.getDeclinedInvoicesCurrentMonth(userEmail);

        invoiceComboBox.getItems().setAll(declinedInvoices);

        invoiceComboBox.setOnAction(e -> {
            Invoice selected = invoiceComboBox.getValue();
            if (selected != null) {
                fillFields(selected);
            }
        });
    }

    private void fillFields(Invoice invoice) {
        this.invoice = invoice;
        amountField.setText(String.valueOf(invoice.getAmount()));
        datePicker.setValue(invoice.getDate());
        classificationComboBox.setValue(invoice.getCategory().name());
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    private void handleResubmit(ActionEvent event) {
        if (invoice == null) return;

        try {
            double newAmount = Double.parseDouble(amountField.getText());
            invoice.setAmount(newAmount);
            invoice.setDate(datePicker.getValue());
            invoice.setCategory(Category.valueOf(classificationComboBox.getValue().toUpperCase()));
            invoice.setStatus(Status.PROCESSING);  // 🟡 zurück in Prüfstatus

            // Optional: timestamp aktualisieren?
            // invoice.setCreatedAt(LocalDateTime.now());

            InvoiceRepository.updateInvoice(invoice);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Resubmission successful");
            alert.setHeaderText(null);
            alert.setContentText("Your invoice has been resubmitted and will be reviewed again.");
            alert.showAndWait();

            loadPage("dashboard1.fxml", event);  // zurück zur Übersicht

        } catch (NumberFormatException e) {
            showAlert("Invalid amount. Please enter a valid number.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Something went wrong. Please try again.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}