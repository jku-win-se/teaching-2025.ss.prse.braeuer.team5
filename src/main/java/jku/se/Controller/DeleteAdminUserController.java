package jku.se.Controller;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import jku.se.repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;

/**
 * Controller class for handling the deletion of users (Admin or User) from the system.
 * Allows an admin to choose a user type, select an email, and delete the selected user.
 */
public class DeleteAdminUserController { //#18 Magdalena

    @FXML private ChoiceBox type;
    @FXML private ChoiceBox email;
    @FXML private Label message;


    /**
     * Initializes the controller by populating the user type choice box.
     * Adds an event listener to update the email list based on selection.
     */
    @FXML
    public void initialize() {
        type.getItems().addAll("Admin", "User"); //
        type.setOnAction(event -> updateEmailList());
    }

    /**
     * Updates the email list based on the selected user type (Admin or User).
     * Fetches emails from the corresponding repository method.
     */
    @FXML
    private void updateEmailList() {
        String selectedType = (String) type.getValue();
        List<String> emails;

        if ("Admin".equals(selectedType)) {
            emails = UserRepository.getAllAdminEmails();
        } else {
            emails = UserRepository.getAllUserEmails();
        }

        email.setItems(FXCollections.observableArrayList(emails));
    }

    /**
     * Deletes the selected user based on the email chosen from the dropdown.
     * Displays a success or failure message accordingly.
     *
     * @param event the action event triggered by clicking the delete button
     * @throws IOException if any scene loading fails
     */
    @FXML
    private void deleteAdminUser(ActionEvent event) throws IOException{
        String selectedEmail = (String) email.getValue();

        if (selectedEmail == null || selectedEmail.isEmpty()) {
            message.setText("Please select an e-mail!");
            return;
        }

        boolean success = UserRepository.deleteUser(selectedEmail);
        if (success) {
            message.setText("User has been successfully deleted.");
            email.getItems().remove(selectedEmail); // delete user from database
        } else {
            message.setText("User could not be deleted.");
        }
    }

    /**
     * Cancels the deletion process and navigates back to the admin dashboard.
     *
     * @param event the action event triggered by clicking the cancel button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void cancelDeleteAdminUser(ActionEvent event) throws IOException {
        loadPage("dashboard2.fxml", event);
    }

    /**
     * Loads a new page based on the provided FXML file.
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
}
