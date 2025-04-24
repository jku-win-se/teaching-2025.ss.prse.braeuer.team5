package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jku.se.Invoice;

import java.io.IOException;

public class EditInvoiceController {
    private Invoice invoice;

    @FXML
    private void cancelEdit(ActionEvent event) throws IOException {
        // Load the correct dashboard (adjust path if needed)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard1.fxml"));
        Scene dashboardScene = new Scene(loader.load());

        // Get the current stage and switch scenes
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(dashboardScene);
        stage.show();
    }

    // Optional: Set the invoice safely
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        // Add validation here if needed (e.g., check isEditable())
    }
}