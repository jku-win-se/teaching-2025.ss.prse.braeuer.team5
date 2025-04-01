package jku.se.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        // Wenn der Admin auf "Logout" klickt, lade die Startseite
        loadPage("dashboard1.fxml", event);
    }

    @FXML
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());

        // Hole das aktuelle Fenster (Stage) von einem der UI-Elemente (z. B. einem Node)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Setze die neue Szene und zeige sie
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

    // Methode für Entfernen-Button
    @FXML
    private void handleRemoveFile(ActionEvent event) {
        selectedFile = null;
        statusLabel.setText("No file selected");
        statusLabel.setStyle("-fx-text-fill: black;");
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
        // Validate all fields
        if (datePicker.getValue() == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please select an invoice date");
            return;
        }

        LocalDate selectedDate = datePicker.getValue();

        // Überprüfen, ob bereits eine Rechnung für den gewählten Werktag hochgeladen wurde
        if (uploadedDates.contains(selectedDate)) {
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

        // Erstelle eine Instanz der Invoice-Klasse, um die Rückerstattung zu berechnen  //nochmal anschaun -- example löschen?
        Invoice invoice = new Invoice("user@example.com", selectedDate, amount, Category.RESTAURANT, Status.PROCESSING, "", LocalDateTime.now(), amount);

        // Reimbursement Validierung: Betrag muss größer als Reimbursement sein
        double reimbursement = invoice.calculateRefund();
        if (amount <= reimbursement) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Amount must be greater than reimbursement");
            return;
        }

        if (categoryCombo.getValue() == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please select a category");
            return;
        }

        if (selectedFile == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please select a file to upload");
            return;
        }

        // Validierung erfolgreich, weiter mit dem Hochladen
        try {
            // Speichern der Datei
            Path uploadsDir = Paths.get("uploads");
            if (!Files.exists(uploadsDir)) {
                Files.createDirectories(uploadsDir);
            }

            Path destination = uploadsDir.resolve(selectedFile.getName());
            Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            // Nach erfolgreichem Upload: Füge das Datum der Liste hinzu
            uploadedDates.add(selectedDate);

            // Update der Statusmeldung
            statusLabel.setText("Upload successful: " + destination.toString());
            statusLabel.setStyle("-fx-text-fill: green;");

            // Formular nach dem erfolgreichen Upload zurücksetzen
            datePicker.setValue(null);
            amountField.clear();
            categoryCombo.setValue(null);
            selectedFile = null;
            statusLabel.setText("Ready for new upload");

        } catch (IOException e) {
            statusLabel.setText("Upload failed: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}
