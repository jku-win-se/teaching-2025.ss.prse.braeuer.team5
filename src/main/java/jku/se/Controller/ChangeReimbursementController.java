package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jku.se.Category;

import java.io.IOException;

public class ChangeReimbursementController {

    @FXML
    private TextField restaurantField;

    @FXML
    private TextField supermarketField;

    @FXML
    private Button saveButton;

    @FXML
    private Button exitButton;

    // Initialisierung der Textfelder mit den aktuellen Rückerstattungswerten
    @FXML
    public void initialize() {
        // Aktuelle Rückerstattungsbeträge in die Textfelder laden
        restaurantField.setText(String.valueOf(Category.RESTAURANT.getRefundAmount()));
        supermarketField.setText(String.valueOf(Category.SUPERMARKET.getRefundAmount()));
    }

    // Methode zum Speichern der Änderungen
    @FXML
    public void handleSave() {
        try {
            // Abrufen und Überprüfen der Eingabewerte
            double restaurantAmount = validateInput(restaurantField.getText());
            double supermarketAmount = validateInput(supermarketField.getText());

            // Setzen der neuen benutzerdefinierten Rückerstattungsbeträge für jede Kategorie
            Category.setCustomRefundAmount(Category.RESTAURANT, restaurantAmount);
            Category.setCustomRefundAmount(Category.SUPERMARKET, supermarketAmount);

            // Erfolgreiche Speicherung anzeigen
            showAlert(Alert.AlertType.INFORMATION, "Successful", "The Reimbursement has been successfully changed.");
        } catch (NumberFormatException e) {
            // Fehlerhafte Eingabe
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid number.");
        }
    }

    // Methode zum Beenden der Anwendung
    @FXML
    private void handleExit(ActionEvent event) throws IOException {
        loadPage("dashboard2.fxml", event);
    }

    @FXML
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // Validierung der Eingabe
    private double validateInput(String input) throws NumberFormatException {
        // Versuchen, den Text in eine Zahl zu konvertieren
        double amount = Double.parseDouble(input.trim());

        // Überprüfen, ob der Betrag eine gültige Zahl ist
        if (amount <= 0) {
            throw new NumberFormatException("Error: Needs to be positive");
        }

        return amount;
    }

    // Methode zur Anzeige von Fehlermeldungen
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
