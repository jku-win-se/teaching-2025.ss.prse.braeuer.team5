package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jku.se.User;
import jku.se.repository.UserRepository;
import javafx.scene.control.Label;
import java.io.IOException;

/**
 * Controller for adding new users or administrators in the Lunchify system.
 * Only administrators are allowed to access this screen.
 * Fields include name, email, password, and checkboxes to define role.
 */

public class AddAdminUserController { //#18 Magdalena

    @FXML private TextField txtEmail;
    @FXML private TextField txtName;
    @FXML private TextField txtPassword;
    @FXML private CheckBox chkAdmin;
    @FXML private CheckBox chkUser;
    @FXML private Label message;


    /**
     * Adds a new user or administrator based on the checkbox selection.
     * Shows an alert if required fields are missing or upon success.
     */
    @FXML
    private void addAdminUser(){
        String email = txtEmail.getText();
        String name = txtName.getText();
        String password = txtPassword.getText();
        boolean isAdmin = chkAdmin.isSelected();


        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields!");
            return;
        }

        User newUser = new User(name, email, password, isAdmin);
        UserRepository.addUser(newUser);  // add User in database
        showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");
        clearFields();
    }

    /**
     * Clears all input fields and resets the checkboxes.
     */
    //ai
    private void clearFields() {
        txtEmail.clear();
        txtName.clear();
        txtPassword.clear();
        chkAdmin.setSelected(false);
        chkUser.setSelected(false);
    }

    /**
     * Cancels the add-user process and navigates back to the admin dashboard.
     *
     * @param event The action event triggering the method.
     * @throws IOException If the FXML file cannot be loaded.
     */

    @FXML
    private void cancelAddAdminUser(ActionEvent event) throws IOException {
        loadPage("dashboard2.fxml", event);
    }

    /**
     * Loads the specified FXML page into the current scene.
     *
     * @param fxmlFile The name of the FXML file to load.
     * @param event The event that triggered the load.
     * @throws IOException If the file cannot be found or loaded.
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
     * Displays an alert dialog with a given type, title, and message.
     *
     * @param type The type of alert (e.g., ERROR, INFORMATION).
     * @param title The title shown on the alert window.
     * @param message The message body of the alert.
     */

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
