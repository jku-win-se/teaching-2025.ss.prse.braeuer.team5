package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;
import jku.se.Invoice;
import jku.se.repository.InvoiceRepository;

import java.io.IOException;
import java.util.List;
public class AdminDashboardController {

    @FXML
    private TableView<Invoice> invoiceTable;

    @FXML
    private TableColumn<Invoice, String> userColumn;

    @FXML
    private TableColumn<Invoice, String> submissionDateColumn;

    @FXML
    private TableColumn<Invoice, Double> amountColumn;

    @FXML
    private TableColumn<Invoice, String> categoryColumn;

    @FXML
    private TableColumn<Invoice, String> statusColumn;

    @FXML
    private TableColumn<Invoice, Double> reimbursementColumn;

    //fill table with invoices
    @FXML
    private void initialize() { //#15- Magdalena
        // Connect columns with the invoice attributes
        userColumn.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        submissionDateColumn.setCellValueFactory(new PropertyValueFactory<>("submissionDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reimbursementColumn.setCellValueFactory(new PropertyValueFactory<>("reimbursement"));

        loadInvoices();
    }

    private void loadInvoices() { //#15-Magdalena
        List<Invoice> invoices = InvoiceRepository.getAllInvoices();
        invoiceTable.getItems().setAll(invoices);
    }

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
