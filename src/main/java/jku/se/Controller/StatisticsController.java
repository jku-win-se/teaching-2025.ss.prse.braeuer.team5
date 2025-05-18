package jku.se.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jku.se.Category;
import jku.se.Statistics;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsController {

    @FXML private Label cancelButton;
    @FXML private Text textFieldTotalRefund;
    @FXML private Text textFieldAverageInvoices;
    @FXML private RadioButton distributionRadioButton;
    @FXML private RadioButton invoicesRadioButton;
    @FXML private RadioButton reimbursementRadioButton;
    @FXML private BarChart<String, Number> barChart;
    @FXML private ToggleGroup statisticsToggleGroup;

    private final Statistics statistics = new Statistics();

    @FXML
    public void initialize() {
        // Calculate and display total refund and average invoices per user per month
        double totalRefund = statistics.getReimbursementForAYear();
        textFieldTotalRefund.setText(String.format("%.2f €", totalRefund));
        double averageInvoicesPerson = statistics.getAverageOfInvoicesPerUserPerMonth();
        textFieldAverageInvoices.setText(String.format("%.2f", averageInvoicesPerson));
    }

    // Navigate to Supermarket vs Restaurant distribution chart
    @FXML
    private void statisticDistributionRestaurantSupermarket(ActionEvent event) throws IOException {
        loadPage("StatisticSupermarketRestaurant.fxml", event);
    }

    // Navigate to number of invoices per month chart
    @FXML
    private void statisticNumberInvoicesPerMonth(ActionEvent event) throws IOException{
        loadPage("StatisticNumberOfInvoices.fxml", event);
    }

    // Navigate to reimbursement per month chart
    @FXML
    private void statisticReimbursementPerMonth(ActionEvent event) throws IOException{
        loadPage("StatisticReimbursementPerMonth.fxml", event);
    }

    // Cancel and return to dashboard
    @FXML
    private void cancelStatistics(ActionEvent event) throws IOException {
        loadPage("dashboard2.fxml", event);
    }

    // Load specified FXML page
    @FXML
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }
}
