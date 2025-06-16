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

/**
 * Controller class for managing the reimbursement rates of invoice categories (e.g., restaurant, supermarket).
 * Allows an admin to view and update the refund amounts for each category.
 */
public class ChangeReimbursementController {

    @FXML
    private TextField restaurantField;

    @FXML
    private TextField supermarketField;


    /**
     * Initializes the controller and pre-fills the text fields with current refund values.
     */
    @FXML
    public void initialize() {
        // Load current refund amounts into the text fields
        restaurantField.setText(String.valueOf(Category.RESTAURANT.getRefundAmount()));
        supermarketField.setText(String.valueOf(Category.SUPERMARKET.getRefundAmount()));
    }

    /**
     * Handles the logic to save changes made to the refund values.
     * Validates the input and sets the custom refund amounts for both categories.
     */
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

    /**
     * Handles navigation back to the admin dashboard.
     *
     * @param event the action event triggered by clicking the "Exit" button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void handleExit(ActionEvent event) throws IOException {
        loadPage("dashboard2.fxml", event);
    }

    /**
     * Helper method to load a different scene.
     *
     * @param fxmlFile the name of the FXML file to load
     * @param event the action event used to retrieve the current window
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Validates user input for the reimbursement amount.
     *
     * @param input the raw string input from the text field
     * @return the validated and parsed double value
     * @throws NumberFormatException if the input is not a positive number
     */
    private double validateInput(String input) throws NumberFormatException {
        // Try to convert the text into a number
        double amount = Double.parseDouble(input.trim());

        // Check if the amount is a valid number
        if (amount <= 0) {
            throw new NumberFormatException("Error: Needs to be positive");
        }

        return amount;
    }

    /**
     * Shows a modal alert with the given type, title, and message.
     *
     * @param alertType the type of the alert (e.g., ERROR, INFORMATION)
     * @param title the title of the alert dialog
     * @param message the content message shown to the user
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
