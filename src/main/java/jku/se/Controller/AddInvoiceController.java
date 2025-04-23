package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.DatabaseConnection;
import jku.se.repository.InvoiceRepository;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class AddInvoiceController {

    @FXML public DatePicker datePicker;
    @FXML public TextField amountField;
    @FXML public ComboBox<String> categoryCombo;
    @FXML public Button removeImageBtn;
    @FXML public Button uploadButton;

    public File selectedFile;
    private double reimbursement;
    private Set<LocalDate> uploadedDates = new HashSet<>();

    @FXML
    private void cancelAdd(ActionEvent event) throws IOException {
        loadPage("dashboard1.fxml", event);
    }

    @FXML
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleFileSelect(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Invoice (max 10MB)");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image/PDF", "*.jpg", "*.jpeg", "*.png", "*.pdf")
        );

        selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            try {
                Invoice.validateFile(selectedFile);
                showAlert(String.format(
                        "✓ Selected: %s (%.2f MB)",
                        selectedFile.getName(),
                        selectedFile.length() / (1024.0 * 1024)
                ), Alert.AlertType.INFORMATION);
            } catch (IllegalArgumentException e) {
                showAlert(e.getMessage(), Alert.AlertType.ERROR);
                selectedFile = null;
            }
        }
    }

    @FXML
    public void handleUpload(ActionEvent event) {
        String userEmail = getCurrentUserEmail();
        if (userEmail == null) return;

        if (!validateDate()) return;

        LocalDate selectedDate = datePicker.getValue();
        if (!validateInvoiceUniqueness(userEmail, selectedDate)) return;

        if (!validateAmount()) return;

        Category selectedCategory = getSelectedCategory();
        if (selectedCategory == null) return;

        if (!validateFileSelection()) return;

        String fileUrl = uploadFile();
        if (fileUrl == null) return;

        Invoice invoice = createInvoice(userEmail, selectedDate, selectedCategory, fileUrl);
        saveInvoice(invoice, fileUrl, selectedDate);
    }

    private String getCurrentUserEmail() {
        String userEmail = UserDashboardController.getCurrentUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            showAlert("User is not logged in or email is missing.", Alert.AlertType.ERROR);
            return null;
        }
        return userEmail;
    }

    private boolean validateDate() {
        if (datePicker.getValue() == null) {
            showAlert("Please select an invoice date.", Alert.AlertType.ERROR);
            return false;
        }

        if (datePicker.getValue().isAfter(LocalDate.now())) {
            showAlert("You cannot use a future date.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private boolean validateInvoiceUniqueness(String userEmail, LocalDate selectedDate) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (InvoiceRepository.invoiceExists(connection, userEmail, java.sql.Date.valueOf(selectedDate))) {
                showAlert("Upload Limit: One Invoice per day.", Alert.AlertType.ERROR);
                return false;
            }
        } catch (SQLException e) {
            showAlert("Database error: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private boolean validateAmount() {
        if (amountField.getText().isEmpty()) {
            showAlert("Please enter an amount.", Alert.AlertType.ERROR);
            return false;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showAlert("Amount must be greater than 0.", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid amount.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private Category getSelectedCategory() {
        try {
            return Category.valueOf(categoryCombo.getValue());
        } catch (IllegalArgumentException | NullPointerException e) {
            showAlert("Please select a category.", Alert.AlertType.ERROR);
            return null;
        }
    }

    private boolean validateFileSelection() {
        if (selectedFile == null) {
            showAlert("Please select a file to upload.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private String uploadFile() {
        try {
            String fileUrl = DatabaseConnection.uploadFileToBucket(selectedFile);
            if (fileUrl == null) {
                showAlert("File upload failed.", Alert.AlertType.ERROR);
            }
            return fileUrl;
        } catch (IOException e) {
            showAlert("Error uploading file: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }

    private Invoice createInvoice(String userEmail, LocalDate selectedDate, Category selectedCategory, String fileUrl) {
        double amount = Double.parseDouble(amountField.getText());
        double reimbursement = selectedCategory.getRefundAmount();
        if (amount < reimbursement) {
            reimbursement = amount;
        }

        Invoice invoice = new Invoice(
                userEmail,
                selectedDate,
                amount,
                selectedCategory,
                Status.PROCESSING,
                "",
                LocalDateTime.now(),
                reimbursement
        );
        invoice.setFileUrl(fileUrl);
        return invoice;
    }

    private void saveInvoice(Invoice invoice, String fileUrl, LocalDate selectedDate) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            InvoiceRepository.saveInvoiceInfo(
                    connection,
                    invoice.getUserEmail(),
                    java.sql.Date.valueOf(invoice.getDate()),
                    invoice.getAmount(),
                    invoice.getCategory(),
                    invoice.getStatus(),
                    fileUrl,
                    invoice.getCreatedAt(),
                    invoice.getReimbursement(),
                    selectedFile
            );
            uploadedDates.add(selectedDate);
            showAlert("Invoice and file uploaded successfully.", Alert.AlertType.INFORMATION);
            resetForm();
        } catch (SQLException e) {
            showAlert("Error saving invoice to database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetForm() {
        datePicker.setValue(null);
        amountField.clear();
        categoryCombo.getSelectionModel().clearSelection();
        selectedFile = null;
        if (removeImageBtn != null) {
            removeImageBtn.setDisable(true);
        }
        if (uploadButton != null) {
            uploadButton.setDisable(false); // wieder aktivieren, falls du Upload-Button deaktivierst
        }
    }

    public void setSelectedFile(Object o) {

    }
}
