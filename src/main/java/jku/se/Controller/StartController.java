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

import jku.se.UserSession;
import jku.se.repository.UserRepository;
import jku.se.User;

/**
 * Controller for the start (login) screen of the Lunchify application.
 * Handles login actions for both admin and regular users.
 */
public class StartController {

    @FXML private TextField emailFieldUser;
    @FXML private TextField passwordFieldUser;
    @FXML private TextField emailFieldAdmin;
    @FXML private TextField passwordFieldAdmin;
    @FXML private Label errorLabel, errorLabel1;

    /**
     * Handles login for admin users.
     * If the credentials are valid and the user is an admin, loads the admin dashboard.
     *
     * @param event the action event triggered by the login button
     * @throws IOException if loading the FXML fails
     */
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

    /**
     * Handles login for regular users.
     * If the credentials are valid and the user is not an admin, loads the user dashboard.
     *
     * @param event the action event triggered by the login button
     * @throws IOException if loading the FXML fails
     */
    @FXML
    private void handleUserLogin(ActionEvent event) throws IOException {
        String email = emailFieldUser.getText();
        String password = passwordFieldUser.getText();

        User user = UserRepository.findByEmailAndPassword(email, password);
        if (user != null) {
            UserSession.setCurrentUser(user);
        }

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