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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jku.se.Category;
import jku.se.Statistics;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

//
public class StatisticsController {

    @FXML private Label cancelButton;
    @FXML private Text TextFieldTotalRefund;
    @FXML private Text TextFieldAverageInvoices;
    @FXML private RadioButton distributionRadioButton;
    @FXML private RadioButton InvoicesRadioButton;
    @FXML private RadioButton reimbursementRadioButton;
    @FXML private BarChart<String, Number> barChart;
    @FXML
    private ToggleGroup statisticsToggleGroup;

    private final Statistics statistics = new Statistics();

    @FXML
    public void initialize() {
        //calculate totalRefund and averageInvoices
        double totalRefund = statistics.getReimbursementForAYear();
        TextFieldTotalRefund.setText(String.format("%.2f €", totalRefund));
        double averageInvoicesPerson = statistics.getAverageOfInvoicesPerUserPerMonth();
        TextFieldAverageInvoices.setText(String.format("%.2f ", averageInvoicesPerson));

    }

    @FXML
    private void statisticDistributionRestaurantSupermarket(ActionEvent event) throws IOException {
        loadPage("StatisticSupermarketRestaurant.fxml", event);
    }


    @FXML
    private void statisticNumberInvoicesPerMonth(ActionEvent event) throws IOException{
        loadPage("StatisticNumberOfInvoices.fxml", event);
    }


    @FXML
    private void statisticReimbursementPerMonth(ActionEvent event) throws IOException{
        loadPage("StatisticReimbursementPerMonth.fxml", event);

    }



    @FXML
    private void cancelStatistics(ActionEvent event) throws IOException {
    loadPage("dashboard2.fxml", event);
}

        @FXML
        private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        }
}

