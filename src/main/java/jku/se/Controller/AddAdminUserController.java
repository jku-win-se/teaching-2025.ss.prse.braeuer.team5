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

public class AddAdminUserController { //#18 Magdalena

    @FXML private TextField txtEmail;
    @FXML private TextField txtName;
    @FXML private TextField txtPassword;
    @FXML private CheckBox chkAdmin;
    @FXML private CheckBox chkUser;
    @FXML private Label message;


    //only admins can create users or other admins
    //add user - if the admin checkbox is ticked, an admin is created
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

    //delete the fields after insert  - ai
    private void clearFields() {
        txtEmail.clear();
        txtName.clear();
        txtPassword.clear();
        chkAdmin.setSelected(false);
        chkUser.setSelected(false);
    }
    @FXML //exit
    private void cancelAddAdminUser(ActionEvent event) throws IOException {
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

    //add alert for information
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
