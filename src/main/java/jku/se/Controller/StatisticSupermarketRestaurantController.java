package jku.se.Controller;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import jku.se.Statistics;
import jku.se.export.CsvExporter;
import jku.se.export.PdfExporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticSupermarketRestaurantController {
    @FXML private PieChart pieChartDistribution;
    @FXML private ComboBox<String> saveFormatComboBox;
    @FXML private Text statusText;

    private final Statistics statistics = new Statistics();
    private PauseTransition statusTimer;

    @FXML
    public void initialize() {
        int supermarketCount = statistics.getInvoicesPerSupermaket();
        int restaurantCount = statistics.getInvoicesPerRestaurant();

        pieChartDistribution.setData(FXCollections.observableArrayList(
                new PieChart.Data("Supermarket (" + supermarketCount + ")", supermarketCount),
                new PieChart.Data("Restaurant (" + restaurantCount + ")", restaurantCount)
        ));

        saveFormatComboBox.getItems().addAll("PDF", "CSV");
        saveFormatComboBox.getSelectionModel().selectFirst();

        statusTimer = new PauseTransition(Duration.seconds(3));
        statusTimer.setOnFinished(e -> statusText.setText(""));
    }

    @FXML
    private void handleExport(ActionEvent event) {
        String format = saveFormatComboBox.getValue();

        try {
            if ("PDF".equals(format)) {
                exportPdf();
            } else if ("CSV".equals(format)) {
                exportCsv();
            } else {
                showStatus("Unsupported format: " + format, false);
            }
        } catch (IOException e) {
            showStatus("Export failed: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void exportPdf() throws IOException {
        Map<String, Integer> data = new HashMap<>();
        data.put("Supermarket", statistics.getInvoicesPerSupermaket());
        data.put("Restaurant", statistics.getInvoicesPerRestaurant());

        List<String> headers = List.of("Category", "Invoice Count");
        List<List<String>> rows = new ArrayList<>();
        for (var entry : data.entrySet()) {
            rows.add(List.of(entry.getKey(), String.valueOf(entry.getValue())));
        }

        PdfExporter exporter = new PdfExporter();
        exporter.startPage();
        exporter.addTitle("Supermarket vs Restaurant Invoices");
        exporter.addTable(headers, rows);
        exporter.end();
        exporter.saveToFile("supermarket_restaurant_distribution");
        showStatus("PDF export successful!", true);
    }

    private void exportCsv() throws IOException {
        Map<String, Integer> data = new HashMap<>();
        data.put("Supermarket", statistics.getInvoicesPerSupermaket());
        data.put("Restaurant", statistics.getInvoicesPerRestaurant());

        List<Map<String, String>> rows = new ArrayList<>();
        for (var entry : data.entrySet()) {
            rows.add(Map.of("Category", entry.getKey(), "Invoice Count", String.valueOf(entry.getValue())));
        }

        CsvExporter exporter = new CsvExporter();
        exporter.export(rows, "supermarket_restaurant_distribution");
        showStatus("CSV export successful!", true);
    }

    private void showStatus(String message, boolean success) {
        statusText.setStyle(success ? "-fx-fill: green;" : "-fx-fill: red;");
        statusText.setText(message);
        statusTimer.playFromStart();
    }

    @FXML
    private void cancelDistribution(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistics.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
