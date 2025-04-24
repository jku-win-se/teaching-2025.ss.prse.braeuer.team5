package jku.se.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import jku.se.Statistics;

import java.io.IOException;
import java.util.Map;

public class StatisticReimbursementPerMonthController {
    @FXML
    private BarChart<String, Number> BarChartReimbursementPerMonth;

    private final Statistics statistics = new Statistics();
    @FXML
    public void initialize() {
        Map<String, Double> reimbursementPerMonth = statistics.getReimbursementPerMonth();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reimbursement per Month");

        for (Map.Entry<String, Double> entry : reimbursementPerMonth.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);

            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: lightblue;"); // Einheitliche Farbe für alle Balken
                }
            });
        }

        BarChartReimbursementPerMonth.getData().clear();
        BarChartReimbursementPerMonth.getData().add(series);

        Platform.runLater(() -> {
            BarChartReimbursementPerMonth.lookupAll(".chart-legend-item-symbol").forEach(node ->
                    node.setStyle("-fx-background-color: lightblue;")
            );
        });
    }
    @FXML
    private void cancelReimbursement(ActionEvent event) throws IOException{
        loadPage("Statistics.fxml", event);
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