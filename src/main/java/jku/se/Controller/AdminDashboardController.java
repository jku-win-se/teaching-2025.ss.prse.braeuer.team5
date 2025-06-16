package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;
import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the admin dashboard.
 * Allows admins to view invoices and navigate to other admin functionalities.
 */
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
    private TableColumn<Invoice, Category> categoryColumn;

    @FXML
    private TableColumn<Invoice, Status> statusColumn;

    @FXML
    private TableColumn<Invoice, Double> reimbursementColumn;


    /**
     * Initializes the dashboard by setting up table columns and loading invoice data.
     */
    //ai
    @FXML
    private void initialize() { //#15- Magdalena
        // Connect columns with the invoice attributes
        userColumn.setCellValueFactory(new PropertyValueFactory<>("UserEmail"));
        submissionDateColumn.setCellValueFactory(new PropertyValueFactory<>("CreatedAtString"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryString"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusString"));
        reimbursementColumn.setCellValueFactory(new PropertyValueFactory<>("Reimbursement"));

        loadInvoices();
    }

    /**
     * Loads all invoices available to the admin and displays them in the table.
     */
    private void loadInvoices() { //#15-Magdalena
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        invoiceTable.getItems().setAll(invoices);
    }

    /**
     * Navigates to the admin invoice management view.
     *
     * @param event the action event that triggered the method.
     * @throws IOException if loading the FXML fails.
     */
    @FXML
    private void handleEditInvoice(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminInvoiceManagement.fxml"));
        Scene scene = new Scene(loader.load());


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Navigates to the statistics view.
     *
     * @param event the action event that triggered the method.
     * @throws IOException if loading the FXML fails.
     */
    @FXML
    private void handleStatistics(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistics.fxml"));
        Scene scene = new Scene(loader.load());


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Navigates to the form to add a new admin user.
     *
     * @param event the action event that triggered the method.
     * @throws IOException if loading the FXML fails.
     */
    @FXML //#18 Magda
    private void handleAddAdminUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddAdminUser.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Navigates to the form to delete an admin user.
     *
     * @param event the action event that triggered the method.
     * @throws IOException if loading the FXML fails.
     */
    @FXML //#18 Magda
    private void handleDeleteAdminUser(ActionEvent event)throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DeleteAdminUser.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Logs the admin out and returns to the start view.
     *
     * @param event the action event that triggered the method.
     * @throws IOException if loading the FXML fails.
     */
    @FXML
    private void handleLogoutAdmin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
        Scene scene = new Scene(loader.load());


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens the view to change reimbursement settings.
     *
     * @param event the action event that triggered the method.
     * @throws IOException if loading the FXML fails.
     */
    @FXML
    private void changeReimbursement(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChangeReimbursement.fxml"));
        Scene scene = new Scene(loader.load());


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens a new window displaying system notifications.
     *
     * @param event the action event that triggered the method.
     */
    @FXML
    private void openNotifications(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notifications.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Notifications");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
