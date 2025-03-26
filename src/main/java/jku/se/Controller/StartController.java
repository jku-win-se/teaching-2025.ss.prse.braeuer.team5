package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.IOException;

public class StartController {

    private Label login1;
    private Label login2;
    @FXML
    private void handleAdminLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard2.fxml"));
        Scene dashboardScene = new Scene(loader.load());

        // Hole das aktuelle Fenster (Stage) von einem der UI-Elemente (Node)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(dashboardScene); // Setze die neue Szene
        stage.show(); // Zeige das Dashboard
    }

    @FXML
    private void handleUserLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard1.fxml"));
        Scene dashboardScene = new Scene(loader.load());

        // Hole das aktuelle Fenster (Stage) von einem der UI-Elemente (Node)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(dashboardScene); // Setze die neu Szene
        stage.show(); // Zeige das Dashboard
    }


}
