package jku.se.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;


public class AddInvoiceController {

    @FXML ComboBox<String> categoryCombo;
    private Label cancelAdd;

    @FXML
    public void initialize() {
        // Safe initialization
        if (categoryCombo != null) {
            categoryCombo.getItems().addAll("RESTAURANT", "SUPERMARKET");
        } else {
            System.err.println("FATAL: categoryCombo not injected!");
        }
    }

    @FXML
    private void cancelAdd(ActionEvent event) throws IOException {
        // Wenn der Admin auf "Logout" klickt, lade die Startseite
        loadPage("dashboard1.fxml", event);
    }

    @FXML
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());

        // Hole das aktuelle Fenster (Stage) von einem der UI-Elemente (z. B. einem Node)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Setze die neue Szene und zeige sie
        stage.setScene(scene);
        stage.show();
    }
}
