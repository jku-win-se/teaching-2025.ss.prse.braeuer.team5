package jku.se.Controller;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import jku.se.*;
import jku.se.repository.InvoiceRepository;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for the User Dashboard.
 * Displays a table of user invoices and a pie chart showing the distribution
 * of invoice categories (Supermarket vs Restaurant).
 * Allows navigation to invoice upload, edit, and notification views.
 */
public class UserDashboardController {

    private static final Logger LOGGER = Logger.getLogger(UserDashboardController.class.getName());


    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, String> submissionDateColumn;
    @FXML private TableColumn<Invoice, Double> amountColumn;
    @FXML private TableColumn<Invoice, Category> categoryColumn;
    @FXML private TableColumn<Invoice, Status> statusColumn;
    @FXML private TableColumn<Invoice, Double> reimbursementColumn;
    private static String currentUserEmail;
    @FXML private PieChart pieChartDistribution;
    private final Statistics statistics = new Statistics();

    /**
     * Sets the current user's email and initializes the dashboard data.
     *
     * @param email the email of the current user
     */
    public  void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
        loadInvoices();
        loadPieChart();
    }

    /**
     * Gets the current user's email address.
     *
     * @return the current user's email
     */
    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    /**
     * Initializes the invoice table and pie chart when the view is loaded.
     */
    @FXML
    private void initialize() {
        // Connect columns with the invoice attributes
        submissionDateColumn.setCellValueFactory(new PropertyValueFactory<>("CreatedAtString"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryString"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusString"));
        reimbursementColumn.setCellValueFactory(new PropertyValueFactory<>("reimbursement"));

        loadInvoices();
        loadPieChart();
    }
    /**
     * Loads the user's invoices into the table view.
     */
    private void loadInvoices() {
        if (invoiceTable == null) {
            LOGGER.log(java.util.logging.Level.SEVERE, () -> "invoiceTable is null! Check whether the fx:id is set correctly in the FXML file.");
            return;  // Prevent the method from being executed further if the TableView is null
        }
        // load invoices
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesUser(currentUserEmail);
        invoiceTable.getItems().setAll(invoices);
    }

    /**
     * Loads and displays a pie chart showing invoice category distribution for the current user.
     */
    private void loadPieChart(){

        int supermarketCount = statistics.getInvoicesPerSupermarketUser(currentUserEmail);
        int restaurantCount = statistics.getInvoicesPerRestaurantUser(currentUserEmail);

        PieChart.Data supermarketData = new PieChart.Data("Supermarket (" + supermarketCount + ")", supermarketCount);
        PieChart.Data restaurantData = new PieChart.Data("Restaurant (" + restaurantCount + ")", restaurantCount);

        pieChartDistribution.setData(FXCollections.observableArrayList(
                supermarketData,
                restaurantData
        ));


        Platform.runLater(() -> {
            supermarketData.getNode().setStyle("-fx-pie-color: lightblue;");
            restaurantData.getNode().setStyle("-fx-pie-color: grey;");


            for (Node node : pieChartDistribution.lookupAll(".chart-legend-item")) {
                if (node instanceof Label label) {
                    String text = label.getText();
                    if (text.contains("Supermarket")) {
                        label.getGraphic().setStyle("-fx-background-color: lightblue;");
                    } else if (text.contains("Restaurant")) {
                        label.getGraphic().setStyle("-fx-background-color: grey;");
                    }
                }
            }
        });
    }

    /**
     * Handles navigation to the invoice editing screen.
     */
    @FXML
    private void handleEditInvoiceUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditInvoice.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles navigation to the invoice upload screen.
     */
    @FXML
    private void handleUploadInvoice(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddInvoice.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Logs out the user and navigates back to the start screen.
     */
    @FXML
    private void handleLogoutUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens the user notifications window in a new stage.
     */
    @FXML
    private void openNotifications(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notificationsUser.fxml"));
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
