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
    @FXML private DatePicker datePicker;
    @FXML private TextField amountField;
    @FXML private Label statusLabel;
    @FXML private Button removeImageBtn;
    @FXML ComboBox<String> categoryCombo;
    private Label cancelAdd;
    @FXML private Button uploadButton;
    private File selectedFile;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB in Bytes
    private Set<LocalDate> uploadedDates = new HashSet<>(); // Set, um bereits hochgeladene Tage zu speichern

    @FXML
    public void initialize() {
    }

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
                validateFile(selectedFile);
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

    private void validateFile(File file) {
        // Formatvalidierung
        String fileName = file.getName().toLowerCase();
        if (!fileName.matches(".*\\.(jpg|jpeg|png|pdf)$")) {
            throw new IllegalArgumentException("Only JPG/PNG/PDF allowed");
        }

        // Größenvalidierung (10MB)
        if (file.length() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(String.format(
                    "File too large (%.2f MB > 10 MB limit)",
                    file.length() / (1024.0 * 1024)
            ));
        }
    }

    @FXML
    private void handleUpload(ActionEvent event) {
        // Holen der Benutzerdaten (E-Mail des aktuellen Benutzers)
        String userEmail = UserDashboardController.getCurrentUserEmail();  // Holt die E-Mail des eingeloggten Benutzers

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

        // Überprüfen, ob bereits eine Rechnung für den gewählten Werktag hochgeladen wurde
        if (uploadedDates.contains(selectedDate) && userEmail.equals(selectedDate.toString())) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("You can only upload one invoice per weekday.");
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
            if (amount <= 0) { // Amount muss größer als 0 sein
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter a valid amount");
            return;
        }

        // Holen der Benutzerwahl für die Kategorie (von der ComboBox)
        Category selectedCategory = Category.valueOf(categoryCombo.getValue());
        if (selectedCategory == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please select a category");
            return;
        }

        // Setze den Status auf einen Standardwert
        Status selectedStatus = Status.PROCESSING;  // Standardwert für den Status - muss nachher überschrieben werden (admin)

        // Überprüfen, ob eine Datei ausgewählt wurde
        if (selectedFile == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please select a file to upload");
            return;
        }

        // Bild hochladen und die URL erhalten
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

        // Erstelle eine Instanz der Invoice-Klasse mit den Benutzereingaben
        Invoice invoice = new Invoice(userEmail, selectedDate, amount, selectedCategory, selectedStatus, "", LocalDateTime.now(), amount);

        // Berechne den Rückerstattungsbetrag
        double reimbursement = invoice.calculateRefund();

        // Der Betrag wird auf den maximalen Rückerstattungsbetrag angepasst, falls er größer ist
        if (amount > reimbursement) {
            amount = reimbursement; // Maximaler Rückerstattungsbetrag
            statusLabel.setStyle("-fx-text-fill: orange;"); // Hinweis, dass der Betrag angepasst wurde
            statusLabel.setText("Amount adjusted to the maximum reimbursement.");
        } else {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Amount is below or equal to reimbursement.");
        }

        // Aktualisiere das Invoice-Objekt mit der hochgeladenen Datei-URL
        invoice.setFileUrl(fileUrl);

        // Speichere die Rechnung in der Datenbank
        try (Connection connection = DatabaseConnection.getConnection()) {
            InvoiceRepository.saveInvoiceInfo(
                    connection,
                    userEmail,  // Dynamische E-Mail des Benutzers
                    java.sql.Date.valueOf(selectedDate),
                    amount,
                    selectedCategory,  // Benutzerdefinierte Kategorie
                    selectedStatus,  // Standardstatus
                    fileUrl,
                    LocalDateTime.now(),
                    reimbursement,
                    selectedFile  // Das hochgeladene Bild
            );
            uploadedDates.add(selectedDate);  // Füge das Datum der Liste hinzu, um Mehrfachuploads zu verhindern
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Invoice and file uploaded successfully.");
        } catch (SQLException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error saving invoice to database: " + e.getMessage());
        }
    }
}