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
import jku.se.DatabaseConnection; // Importiere die DatabaseConnection-Klasse
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
    @FXML public Label statusLabel;
    @FXML public Button removeImageBtn;
    @FXML
    public ComboBox<String> categoryCombo;
    private Label cancelAdd;
    @FXML public Button uploadButton;
    public File selectedFile;
    private double reimbursement;

    private Set<LocalDate> uploadedDates = new HashSet<>(); // Set, um bereits hochgeladene Tage zu speichern

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

    //Dateiauswahl-Validierung: hat die Datei das richtige Format (pdf, jpeg, png)
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
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setText(String.format(
                        "✓ Selected: %s (%.2f MB)",
                        selectedFile.getName(),
                        selectedFile.length() / (1024.0 * 1024)
                ));
            } catch (IllegalArgumentException e) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText(e.getMessage());
                selectedFile = null;
            }
        }
    }

    @FXML
    public void handleUpload(ActionEvent event) {
        // Get user data (e-mail of the current user)
        String userEmail = UserDashboardController.getCurrentUserEmail();  // Fetches the e-mail of the logged-in user

        if (userEmail == null || userEmail.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("User is not logged in or email is missing.");
            return;
        }

        // Validate all fields
        if (datePicker.getValue() == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please select an invoice date");
            return;
        }

        LocalDate selectedDate = datePicker.getValue();

        // Check whether the selected date is in the future
        LocalDate currentDate = LocalDate.now(); // Current date
        if (selectedDate.isAfter(currentDate)) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("You cannot use a future date.");
            return;  // Prevent the upload if the date is in the future
        }

        // Check whether an invoice already exists for the same user and day
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (InvoiceRepository.invoiceExists(connection, userEmail, java.sql.Date.valueOf(selectedDate))) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Upload Limit: One Invoice per day");
                return;  // Prevent the upload if an invoice already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Database error: " + e.getMessage());
            return;
        }

        // Amount Validierung: Betrag muss größer als 0 sein
        if (amountField.getText().isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter an amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) { // Amount must be greater than 0
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter a valid amount");
            return;
        }

        // Get the user selection for the category (from the ComboBox)
        Category selectedCategory = Category.valueOf(categoryCombo.getValue());
        if (selectedCategory == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please select a category");
            return;
        }

        // Calculate the refund amount here before the Invoice object is created
        double reimbursement = selectedCategory.getRefundAmount();
        if (amount < reimbursement) {
            reimbursement = amount;  // If the amount is less than the refund amount, set the amount as refund
        }

        // Set the status to a default value
        Status selectedStatus = Status.PROCESSING;  // Default value for the status - must be overwritten later (admin)

        // Check whether a file has been selected
        if (selectedFile == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please select a file to upload");
            return;
        }

        // Upload image and get the URL
        String fileUrl = null;
        try {
            fileUrl = DatabaseConnection.uploadFileToBucket(selectedFile);
            if (fileUrl == null) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("File upload failed");
                return;
            }
        } catch (IOException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error uploading file: " + e.getMessage());
            return;
        }

        // Create an instance of the Invoice class with the user input
        Invoice invoice = new Invoice(userEmail, selectedDate, amount, selectedCategory, selectedStatus, "", LocalDateTime.now(), reimbursement);

        // Update the Invoice object with the uploaded file URL
        invoice.setFileUrl(fileUrl);

        // Save the invoice in the database
        try (Connection connection = DatabaseConnection.getConnection()) {
            InvoiceRepository.saveInvoiceInfo(
                    connection,
                    userEmail,  // Dynamic e-mail of the user
                    java.sql.Date.valueOf(selectedDate),
                    amount,
                    selectedCategory,  // User-defined category
                    selectedStatus,  // default status
                    fileUrl,
                    LocalDateTime.now(),
                    reimbursement,
                    selectedFile  // uploaded image
            );
            uploadedDates.add(selectedDate);  // Add the date to the list to prevent multiple uploads
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Invoice and file uploaded successfully.");

            resetForm();
        } catch (SQLException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error saving invoice to database: " + e.getMessage());
        }
    }

    // Zurücksetzen des Formulars
    private void resetForm() {
        datePicker.setValue(null);  // Reset the date
        amountField.clear();        // delete the image
        categoryCombo.getSelectionModel().clearSelection();  // Reset the category
        selectedFile = null;        // reset the image
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

    public void setAmountField(TextField amountField) {
        this.amountField = amountField;
    }

    public void setCategoryCombo(ComboBox<String> categoryCombo) {
        this.categoryCombo = categoryCombo;
    }

    public void setSelectedFile(File file) {
        this.selectedFile = file;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(Label label) {
        this.statusLabel = label;
    }
}
