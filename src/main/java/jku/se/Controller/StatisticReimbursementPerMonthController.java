package jku.se.Controller;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import jku.se.Statistics;
import jku.se.export.CsvExporter;
import jku.se.export.JsonExporter;
import jku.se.export.PdfExporter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticReimbursementPerMonthController {
    @FXML private BarChart<String, Number> barChartReimbursementPerMonth;
    @FXML private ComboBox<String> saveFormatComboBox;
    @FXML private Text statusText;

    private final Statistics statistics = new Statistics();
    private PauseTransition statusTimer;

    @FXML
    public void initialize() {
        Map<String, Double> reimbursementPerMonth = statistics.getReimbursementPerMonth();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reimbursement per Month");

        for (Map.Entry<String, Double> entry : reimbursementPerMonth.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) newNode.setStyle("-fx-bar-fill: lightblue;");
            });
        }

        barChartReimbursementPerMonth.getData().clear();
        barChartReimbursementPerMonth.getData().add(series);

        saveFormatComboBox.getItems().addAll("JSON", "PDF", "CSV");
        saveFormatComboBox.getSelectionModel().selectFirst();

        statusTimer = new PauseTransition(Duration.seconds(3));
        statusTimer.setOnFinished(e -> statusText.setText(""));
    }

    @FXML
    private void handleExport() throws SQLException {
        String format = saveFormatComboBox.getValue();

        try {
            if ("PDF".equals(format)) {
                exportPdf();
            } else if ("CSV".equals(format)) {
                exportCsv();
            } else if ("JSON".equals(format)) {
                exportJson();
            } else {
                showStatus("Unsupported format: " + format, false);
            }
        } catch (IOException e) {
            showStatus("Export failed: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void exportPdf() throws IOException {
        Map<String, Double> reimbursementPerMonth = statistics.getReimbursementPerMonth();

        List<String> headers = List.of("Month", "Reimbursement (€)");
        List<List<String>> rows = new ArrayList<>();
        for (var entry : reimbursementPerMonth.entrySet()) {
            rows.add(List.of(entry.getKey(), String.format("%.2f", entry.getValue())));
        }

        PdfExporter exporter = new PdfExporter();
        exporter.startPage();
        exporter.addTitle("Reimbursement Per Month Report");
        exporter.addTable(headers, rows);
        exporter.end();
        exporter.saveToFile("reimbursement_per_month");
        showStatus("PDF export successful!", true);
    }

    private void exportCsv() throws IOException {
        Map<String, Double> reimbursementPerMonth = statistics.getReimbursementPerMonth();

        List<Map<String, String>> rows = new ArrayList<>();
        for (var entry : reimbursementPerMonth.entrySet()) {
            rows.add(Map.of("Month", entry.getKey(), "Reimbursement (€)", String.format("%.2f", entry.getValue())));
        }

        CsvExporter exporter = new CsvExporter();
        exporter.export(rows, "reimbursement_per_month");
        showStatus("CSV export successful!", true);
    }

    private void exportJson() throws IOException, SQLException {
        Map<String, Object> userDetails = statistics.getUserReimbursementDetails();

        JsonExporter exporter = new JsonExporter();
        exporter.export(userDetails, "reimbursement_details");
        showStatus("JSON export successful!", true);
    }

    private void showStatus(String message, boolean success) {
        statusText.setStyle(success ? "-fx-fill: green;" : "-fx-fill: red;");
        statusText.setText(message);
        statusTimer.playFromStart();
    }

    @FXML
    private void cancelReimbursement(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistics.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
