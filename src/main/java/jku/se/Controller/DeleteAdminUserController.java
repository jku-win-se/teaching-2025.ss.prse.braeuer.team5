package jku.se.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
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

public class DeleteAdminUserController { //#18 Magdalena

    @FXML private ChoiceBox type;
    @FXML private ChoiceBox email;
    @FXML private Label message;


    //filled choicebox type - Admin/User
    @FXML
    public void initialize() {
        type.getItems().addAll("Admin", "User"); //
        type.setOnAction(event -> updateEmailList());
    }
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

    //delete user
    @FXML
    private void deleteAdminUser(ActionEvent event) throws IOException{
        String selectedEmail = (String) email.getValue();

        if (selectedEmail == null || selectedEmail.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Missing", "Please select an e-mail.");
            return;
        }

        boolean success = UserRepository.deleteUser(selectedEmail);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User has been successfully deleted.");
            email.getItems().remove(selectedEmail); // delete user from database
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "User could not be deleted.");
        }
    }

    //exit
    @FXML
    private void cancelDeleteAdminUser(ActionEvent event) throws IOException {
        loadPage("dashboard2.fxml", event);
    }

    //switch to dashboard
    @FXML
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    //add alert with information
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
