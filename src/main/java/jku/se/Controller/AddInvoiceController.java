package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AddInvoiceController {

    private Label cancelAdd;
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
