package jku.se.Controller;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import jku.se.Category;
import jku.se.Invoice;
import jku.se.Statistics;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    @FXML
    private PieChart PieChartDistribution;

    private final Statistics statistics = new Statistics();

    // Setter-Methode
    public  void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
        loadInvoices();
        loadPieChart();
    }
    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    //fill table with invoices
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

    private void loadInvoices() {
        if (invoiceTable == null) {
            System.err.println("invoiceTable is null! Check whether the fx:id is set correctly in the FXML file.");
            return;  // Prevent the method from being executed further if the TableView is null
        }
        // load invoices
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesUser(currentUserEmail);
        invoiceTable.getItems().setAll(invoices);
    }

    //load pie chart with distribution of invoices from restaurant or supermarket
    private void loadPieChart(){

        int supermarketCount = statistics.getInvoicesPerSupermaketUser(currentUserEmail);
        int restaurantCount = statistics.getInvoicesPerRestaurantUser(currentUserEmail);

        PieChart.Data supermarketData = new PieChart.Data("Supermarket (" + supermarketCount + ")", supermarketCount);
        PieChart.Data restaurantData = new PieChart.Data("Restaurant (" + restaurantCount + ")", restaurantCount);

        PieChartDistribution.setData(FXCollections.observableArrayList(
                supermarketData,
                restaurantData
        ));


        Platform.runLater(() -> {
            supermarketData.getNode().setStyle("-fx-pie-color: lightblue;");
            restaurantData.getNode().setStyle("-fx-pie-color: grey;");


            for (Node node : PieChartDistribution.lookupAll(".chart-legend-item")) {
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
