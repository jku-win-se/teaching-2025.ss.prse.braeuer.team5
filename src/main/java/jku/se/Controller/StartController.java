package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.IOException;

import jku.se.repository.UserRepository;
import jku.se.User;

public class StartController {

    @FXML private TextField emailFieldUser;
    @FXML private TextField passwordFieldUser;
    @FXML private TextField emailFieldAdmin;
    @FXML private TextField passwordFieldAdmin;
    @FXML private Label errorLabel, errorLabel1;

    @FXML
    private void handleAdminLogin(ActionEvent event) throws IOException {
        String email = emailFieldAdmin.getText();
        String password = passwordFieldAdmin.getText();

        User user = UserRepository.findByEmailAndPassword(email, password);

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Email and password cannot be empty!");
            errorLabel.setVisible(true);
            return;
        }

        if (user != null && user.isAdministrator()) {
            user.login();



            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard2.fxml"));
            Scene dashboardScene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.show();
        } else {
            errorLabel1.setText("Invalid admin credentials!");
            errorLabel1.setVisible(true);
        }
    }

    @FXML
    private void handleUserLogin(ActionEvent event) throws IOException {
        String email = emailFieldUser.getText();
        String password = passwordFieldUser.getText();

        User user = UserRepository.findByEmailAndPassword(email, password);

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Email and password cannot be empty!");
            errorLabel.setVisible(true);
            return;
        }

        if (user != null && !user.isAdministrator()) {
            user.login();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard1.fxml"));
            Scene dashboardScene = new Scene(loader.load());

            //save current user
            UserDashboardController controller = loader.getController();
            controller.setCurrentUserEmail(email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.show();
        } else {
            errorLabel.setText("Invalid login credentials!");
            errorLabel.setVisible(true);
        }

    }



}
