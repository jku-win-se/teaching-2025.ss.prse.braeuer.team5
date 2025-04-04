package jku.se.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;

import java.io.IOException;
import java.util.List;

public class UserDashboardController {

    @FXML
    private TableView<Invoice> invoiceTable;

    @FXML
    private TableColumn<Invoice, String> submissionDateColumn;

    @FXML
    private TableColumn<Invoice, Double> amountColumn;

    @FXML
    private TableColumn<Invoice, Category> categoryColumn;

    @FXML
    private TableColumn<Invoice, Status> statusColumn;

    @FXML
    private TableColumn<Invoice, Double> reimbursementColumn;

    private static String currentUserEmail;

    // Setter-Methode
    public void setCurrentUserEmail(String email) {
        currentUserEmail = email;
        loadInvoices();  // Invoices laden, sobald User gesetzt ist
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    //fill table with invoices
    @FXML
    private void initialize() { //#15- Magdalena
        // Connect columns with the invoice attributes
        submissionDateColumn.setCellValueFactory(new PropertyValueFactory<>("date")); // Achte auf den genauen Methodennamen in der Invoice-Klasse
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryString"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusString"));
        reimbursementColumn.setCellValueFactory(new PropertyValueFactory<>("reimbursement"));

        loadInvoices();
    }

    private void loadInvoices() { //#15-Magdalena
        if (invoiceTable == null) {
            System.err.println("invoiceTable ist null! Überprüfe, ob das fx:id in der FXML-Datei korrekt gesetzt ist.");
            return;  // Verhindere, dass die Methode weiter ausgeführt wird, wenn die TableView null ist
        }
        // Lade die Rechnungen aus der Datenbank
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesUser(currentUserEmail);
        invoiceTable.getItems().setAll(invoices);
    }
    @FXML
    private void handleEditInvoiceUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditInvoice.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleUploadInvoice(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddInvoice.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleLogoutUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }



}
