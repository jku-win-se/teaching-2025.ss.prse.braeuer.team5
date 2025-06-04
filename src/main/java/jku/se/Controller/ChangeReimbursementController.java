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


    // Initialization of the text fields with the current refund values
    @FXML
    public void initialize() {
        // Load current refund amounts into the text fields
        restaurantField.setText(String.valueOf(Category.RESTAURANT.getRefundAmount()));
        supermarketField.setText(String.valueOf(Category.SUPERMARKET.getRefundAmount()));
    }

    // Method for saving changes
    @FXML
    public void handleSave() {
        try {
            // Retrieving and verifying the input values
            double restaurantAmount = validateInput(restaurantField.getText());
            double supermarketAmount = validateInput(supermarketField.getText());

            // Setting the new custom refund amounts for each category
            Category.setCustomRefundAmount(Category.RESTAURANT, restaurantAmount);
            Category.setCustomRefundAmount(Category.SUPERMARKET, supermarketAmount);

            // Show successful save
            showAlert(Alert.AlertType.INFORMATION, "Successful", "The Reimbursement has been successfully changed.");
        } catch (NumberFormatException e) {
            // Incorrect input
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid number.");
        }
    }

    // Method to close the application
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

    // Validation of the input
    private double validateInput(String input) throws NumberFormatException {
        // Try to convert the text into a number
        double amount = Double.parseDouble(input.trim());

        // Check if the amount is a valid number
        if (amount <= 0) {
            throw new NumberFormatException("Error: Needs to be positive");
        }

        return amount;
    }

    // Method for displaying error messages
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
