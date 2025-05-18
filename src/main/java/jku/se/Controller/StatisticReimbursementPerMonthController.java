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
import jku.se.repository.InvoiceRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class StatisticReimbursementPerMonthController extends BaseStatisticController{
    @FXML private BarChart<String, Number> barChartReimbursementPerMonth;
    @FXML private ComboBox<String> saveFormatComboBox;
    @FXML private Text statusText;

    private final Statistics statistics = new Statistics();
    private PauseTransition statusTimer;

    @FXML
    public void initialize() {
        // Initialize bar chart with reimbursement per month data
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

        // Setup export formats and status timer
        saveFormatComboBox.getItems().addAll("JSON", "PDF", "CSV");
        saveFormatComboBox.getSelectionModel().selectFirst();

        statusTimer = new PauseTransition(Duration.seconds(3));
        statusTimer.setOnFinished(e -> statusText.setText(""));
    }

    // Handle export based on selected format
    @FXML
    private void handleExport() throws SQLException {
        String selectedFormat = saveFormatComboBox.getValue();

        if (selectedFormat.equals("PDF") || selectedFormat.equals("CSV")) {
            exportSingleFormat(
                    statusText,
                    "reimbursement_per_month",
                    statistics.getReimbursementPerMonth(),
                    "Reimbursement per Month",
                    selectedFormat
            );
        } else if (selectedFormat.equals("JSON")) {
            Map<String, Object> userDetails = statistics.getUserReimbursementDetails();
            exportReimbursementJson(
                    statusText,
                    "reimbursement_details",
                    userDetails,
                    InvoiceRepository.getTotalReimbursementThisMonth()
            );
        }
    }

    // Cancel and return to statistics page
    @FXML
    private void cancelReimbursement(ActionEvent event) throws IOException {
        loadPage("Statistics.fxml", event);
    }

    // Load specified FXML page
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
