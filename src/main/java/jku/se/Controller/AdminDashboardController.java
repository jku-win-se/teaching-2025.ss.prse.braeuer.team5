package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private void handleEditInvoice(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminInvoiceManagement.fxml"));
        Scene scene = new Scene(loader.load());


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleStatistics(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistics.fxml"));
        Scene scene = new Scene(loader.load());


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    @FXML //#18 Magda
    private void handleAddAdminUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddAdminUser.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    @FXML //#18 Magda
    private void handleDeleteAdminUser(ActionEvent event)throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DeleteAdminUser.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleLogoutAdmin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
        Scene scene = new Scene(loader.load());


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();;
    }


}
