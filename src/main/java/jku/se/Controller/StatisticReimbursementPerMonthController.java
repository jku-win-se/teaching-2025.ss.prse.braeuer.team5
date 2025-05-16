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
import jku.se.Utilities.ExportUtils;

import java.io.IOException;
import java.util.Map;

public class StatisticReimbursementPerMonthController {
    @FXML private BarChart<String, Number> BarChartReimbursementPerMonth;
    @FXML private ComboBox<String> saveFormatComboBox;
    @FXML private Text statusText;

    private final Statistics statistics = new Statistics();
    private PauseTransition statusTimer;

    @FXML
    public void initialize() {
        // Chart initialisieren
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

        BarChartReimbursementPerMonth.getData().clear();
        BarChartReimbursementPerMonth.getData().add(series);

        // Export-Einstellungen
        saveFormatComboBox.getItems().addAll("JSON", "PDF", "CSV");
        saveFormatComboBox.getSelectionModel().selectFirst();

        // Status-Timer
        statusTimer = new PauseTransition(Duration.seconds(3));
        statusTimer.setOnFinished(e -> statusText.setText(""));
    }

    @FXML
    private void handleExport(ActionEvent event) {
        try {
            boolean success = false;
            switch (saveFormatComboBox.getValue()) {
                case "JSON":
                    success = ExportUtils.exportToJson(
                            statistics.getReimbursementPerMonth(),
                            "reimbursement_per_month"
                    );
                    break;
                case "PDF":
                    success = ExportUtils.exportToPdf(
                            statistics.getReimbursementPerMonth(),
                            "Reimbursement per Month",
                            "reimbursement_per_month"
                    );
                    break;
                case "CSV":
                    success = ExportUtils.exportToCsv(
                            statistics.getReimbursementPerMonth(),
                            "reimbursement_per_month"
                    );
                    break;
            }

            showStatus(success ? "Export erfolgreich!" : "Export fehlgeschlagen", success);
        } catch (IOException e) {
            showStatus("Fehler: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void showStatus(String message, boolean isSuccess) {
        statusText.setStyle("-fx-fill: " + (isSuccess ? "green" : "red") + "; -fx-font-size: 14;");
        statusText.setText(message);
        statusTimer.stop();
        statusTimer.play();
    }

    @FXML
    private void cancelReimbursement(ActionEvent event) throws IOException {
        loadPage("Statistics.fxml", event);
    }

    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}