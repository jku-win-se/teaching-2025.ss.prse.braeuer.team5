package jku.se.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jku.se.Statistics;


import java.io.IOException;
import java.util.Map;


public class StatisticNumberOfInvoicesController {
    @FXML
    private BarChart<String, Number> barChartInvoicesPerMonth;

    private final Statistics statistics = new Statistics();

    //ai
    @FXML
    public void initialize() {
        Map<String, Integer> invoicesPerMonth = statistics.getInvoicesPerMonth();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Invoices per Month");

        for (Map.Entry<String, Integer> entry : invoicesPerMonth.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);

            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: lightblue;"); // Einheitliche Farbe für alle Balken
                }
            });
        }

        barChartInvoicesPerMonth.getData().clear();
        barChartInvoicesPerMonth.getData().add(series);

        Platform.runLater(() -> {
            barChartInvoicesPerMonth.lookupAll(".chart-legend-item-symbol").forEach(node ->
                    node.setStyle("-fx-background-color: lightblue;")
            );
        });
    }
    @FXML
    private void cancelNumberOfInvoices(ActionEvent event) throws IOException {
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
