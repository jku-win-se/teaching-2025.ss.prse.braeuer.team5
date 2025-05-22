package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jku.se.*;
import jku.se.Utilities.NotificationManager;
import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class AddInvoiceController {
    private static final Logger LOGGER = Logger.getLogger(AddInvoiceController.class.getName());

    @FXML public DatePicker datePicker;
    @FXML public TextField amountField;
    @FXML public Label statusLabel;
    @FXML public ComboBox<String> categoryCombo;
    @FXML public Button uploadButton;
    public File selectedFile;
    private String ocrAmount;
    private String ocrDate;
    private String ocrCategory;


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

                CloudOCRService ocrService = new CloudOCRService();
                CloudOCRService.OCRResult ocrResult = ocrService.analyzeImage(selectedFile);
                ocrAmount = ocrResult.amount;
                ocrDate = ocrResult.date;
                ocrCategory = ocrResult.category;

                if (!"Not found".equals(ocrResult.date)) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd.MM.yyyy][yyyy-MM-dd][dd/MM/yyyy]");
                        LocalDate parsedDate = LocalDate.parse(ocrResult.date, formatter);
                        datePicker.setValue(parsedDate);
                    } catch (Exception ex) {
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.info("Could not parse date: " + ocrResult.date);
                        }
                    }
                }

                if (!"Not found".equals(ocrResult.amount)) {
                    amountField.setText(ocrResult.amount.replace(",", ".").replace("€", "").trim());
                }

                if (!"OTHER".equals(ocrResult.category)) {
                    categoryCombo.setValue(ocrResult.category);
                }

                showAlert(Alert.AlertType.INFORMATION, "OCR Success", "OCR successfully completed! Please check.");


            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "File Error", "Errors during processing: " + e.getMessage());

            }
        }
    }



    @FXML
    public void handleUpload() throws IOException {
        // Get user data (e-mail of the current user)
        String userEmail = UserDashboardController.getCurrentUserEmail();
        UserRepository.getByEmail(userEmail);


        if (userEmail == null || userEmail.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "User is not logged in or email is missing.");
            return;
        }

        // Validate all fields
        if (datePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Date Error", "Please select an invoice date");
            return;
        }

        LocalDate selectedDate = datePicker.getValue();

        // Check whether the selected date is in the future
        LocalDate currentDate = LocalDate.now(); // Current date
        if (selectedDate.isAfter(currentDate)) {
            showAlert(Alert.AlertType.ERROR, "Date Error", "You cannot use a future date.");
            return;  // Prevent the upload if the date is in the future
        }

        // Check whether an invoice already exists for the same user and day
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (InvoiceRepository.invoiceExists(connection, userEmail, java.sql.Date.valueOf(selectedDate))) {
                showAlert(Alert.AlertType.INFORMATION, "Upload Limit", " One Invoice per day");
                return;  // Prevent the upload if an invoice already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error saving invoice to database: " + e.getMessage());
            return;
        }

        // Amount Validierung: Betrag muss größer als 0 sein
        if (amountField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR,"Amount Error", "Please enter an amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) { // Amount must be greater than 0
                showAlert(Alert.AlertType.ERROR,"Amount Error", "Amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR,"Amount Error", "Please enter a valid amount");
            return;
        }

        // Get the user selection for the category (from the ComboBox)
        String selectedCategoryString = categoryCombo.getValue();
        if (selectedCategoryString == null || selectedCategoryString.isEmpty()) {
            showAlert(Alert.AlertType.ERROR,"Category Error", "Please select a category");
            return;
        }

        Category selectedCategory = Category.valueOf(selectedCategoryString);

        double reimbursement = selectedCategory.getRefundAmount();
        if (amount < reimbursement) {
            reimbursement = amount;  // If the amount is less than the refund amount, set the amount as refund
        }

        // Set the status to a default value
        Status selectedStatus = Status.PROCESSING;  // Default value for the status - must be overwritten later (admin)

        // Check whether a file has been selected
        if (selectedFile == null) {
            showAlert(Alert.AlertType.ERROR,"File Error", "Please select a file to upload");
            return;
        }

        // Upload image and get the URL
        String fileUrl = null;
        try {
            fileUrl = DatabaseConnection.uploadFileToBucket(selectedFile);
            if (fileUrl == null) {
                showAlert(Alert.AlertType.ERROR,"File Error","File upload failed");
                return;
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,"File Error","Error uploading file: " + e.getMessage());
            return;
        }
        String displayDate = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        if (ocrAmount != null && !ocrAmount.equals(amountField.getText().trim())) {
            NotificationManager.getInstance().addNotification(new Notification("User " + userEmail + " changed field Amount of OCR recognition for Invoice " + displayDate + "."));
        }

        if (ocrDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd.MM.yyyy][yyyy-MM-dd][dd/MM/yyyy]");
                LocalDate parsedOcrDate = LocalDate.parse(ocrDate, formatter);
                if (!parsedOcrDate.equals(selectedDate)) {
                    NotificationManager.getInstance().addNotification(new Notification("User " + userEmail + " changed field Date of OCR recognition for Invoice " + displayDate + "."));
                }
            } catch (Exception ignored) {}
        }


        if (ocrCategory != null && !ocrCategory.equalsIgnoreCase(categoryCombo.getValue())) {
            NotificationManager.getInstance().addNotification(new Notification("User " + userEmail + " changed field Category of OCR recognition for Invoice " + displayDate + "."));
        }

        Invoice invoice = new Invoice(userEmail, selectedDate, amount, selectedCategory, selectedStatus, "", LocalDateTime.now(), reimbursement);

        invoice.setFileUrl(fileUrl);

        try (Connection connection = DatabaseConnection.getConnection()) {
            InvoiceRepository.saveInvoiceInfo(
                    connection,
                    userEmail,
                    java.sql.Date.valueOf(selectedDate),
                    amount,
                    selectedCategory,
                    selectedStatus,
                    fileUrl,
                    LocalDateTime.now(),
                    reimbursement,
                    selectedFile
            );
            uploadedDates.add(selectedDate);
            showAlert(Alert.AlertType.INFORMATION,"Invoice Success","Invoice and file uploaded successfully.");

            resetForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR,"Invoice Error","Error saving invoice to database: " + e.getMessage());
        }
        boolean anomaly = AnomalyDetection.detectMismatch(invoice);
        invoice.setAnomalyDetected(anomaly);
        new Notification("Invoice submitted successfully.");


    }

    // Zurücksetzen des Formulars
    private void resetForm() {
        datePicker.setValue(null);  // Reset the date
        amountField.clear();        // delete the image
        categoryCombo.getSelectionModel().clearSelection();  // Reset the category
        selectedFile = null;        // reset the image
    }

    //add alert for information
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
