package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jku.se.Statistics;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

public class StatisticsController {

    @FXML private Label cancelButton;
    @FXML private Text TextFieldTotalRefund;
    @FXML private Text TextFieldAverageInvoices;
    @FXML private RadioButton distributionRadioButton;
    @FXML private RadioButton InvoicesRadioButton;
    @FXML private RadioButton reimbursementRadioButton;
    @FXML private BarChart<String, Number> barChart;
    @FXML
    private ToggleGroup statisticsToggleGroup; //

    private final Statistics statistics = new Statistics();

    @FXML
    public void initialize() {
        //calculate totalRefund and averageInvoices
        double totalRefund = statistics.getReimbursementForAYear();
        TextFieldTotalRefund.setText(String.format("%.2f €", totalRefund));
        double averageInvoicesPerson = statistics.getAverageOfInvoicesPerUserPerMonth();
        TextFieldAverageInvoices.setText(String.format("%.2f ", averageInvoicesPerson));
    }
    //ab hier alles überarbeiten
    @FXML
    private void statisticDistributionRestaurantSupermarket(ActionEvent event) throws IOException{
        Map<String, Integer> data = statistics.getInvoicesPerMonth();
        updateChart(data, "Rechnungen pro Monat");
    }

    @FXML
    private void statisticNumberInvoicesPerMonth(ActionEvent event) throws IOException{

    }

    @FXML
    private void statisticReimbursementPerMonth(ActionEvent event) throws IOException{
        Map<String, Double> data = statistics.getReimbursementPerMonth();
        updateChart(data, "Rückerstattung pro Monat");
    }

    private <T extends Number> void updateChart(Map<String, T> data, String title) {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(title);

        for (Map.Entry<String, T> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
    }

    @FXML
    private void cancelStatistics(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard2.fxml"));
        Scene scene = new Scene(loader.load());


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }
}
