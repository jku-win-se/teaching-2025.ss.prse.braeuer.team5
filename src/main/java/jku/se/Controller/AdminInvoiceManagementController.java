package jku.se.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminInvoiceManagementController {

    @FXML
    private void cancelEditAdmin(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard2.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        }
    }
