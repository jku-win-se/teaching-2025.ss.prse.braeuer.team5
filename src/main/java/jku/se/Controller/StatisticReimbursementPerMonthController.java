package jku.se.Controller;

import javafx.animation.PauseTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import jku.se.Statistics;
import jku.se.repository.InvoiceRepository;
import jku.se.export.CsvExporter;
import jku.se.export.JsonExporter;
import jku.se.export.PdfExporter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StatisticReimbursementPerMonthController {
    @FXML private BarChart<String, Number> barChartReimbursementPerMonth;
    @FXML private ComboBox<String> saveFormatComboBox;
    @FXML
    public Text statusText;

    public Statistics statistics = new Statistics();
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
    private void handleExport() throws SQLException, IOException {
        String selectedFormat = saveFormatComboBox.getValue();

        switch (selectedFormat) {
            case "PDF":
                exportPdf();
                break;
            case "CSV":
                exportCsv();
                break;
            case "JSON":
                exportJson();
                break;
            default:
                showStatus("Unsupported format: " + selectedFormat, false);
        }
    }

    public void exportPdf() throws IOException, SQLException {
        Map<String, Double> reimbursementPerMonth = statistics.getReimbursementPerMonth();
        Map<String, Object> userDetails = statistics.getUserReimbursementDetails();

        PdfExporter exporter = new PdfExporter();
        exporter.startPage();
        exporter.addTitle("Reimbursement Per Month Report");

        // Grafik als Bild proportional einfügen
        WritableImage fxImage = barChartReimbursementPerMonth.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

        int imgWidth = bufferedImage.getWidth();
        int imgHeight = bufferedImage.getHeight();
        float aspectRatio = (float) imgHeight / imgWidth;
        float desiredWidth = 300f;
        float desiredHeight = desiredWidth * aspectRatio;

        exporter.addImageAndMovePosition(bufferedImage, 50, desiredWidth, desiredHeight, 20);

        // Tabelle mit Monatsdaten
        exporter.addParagraph("Monthly Reimbursements:");

        // Erstelle Tabellenzeilen
        var monthRows = reimbursementPerMonth.entrySet().stream()
                .map(e -> java.util.List.of(e.getKey(), String.format("%.2f", e.getValue())))
                .toList();

        exporter.addTable(
                java.util.List.of("Month", "Amount (€)"),
                monthRows
        );

        // Gesamtsumme berechnen
        double totalSum = reimbursementPerMonth.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Gesamtsumme als eigene Zeile hinzufügen
        exporter.addParagraph(String.format("Total Reimbursement: %.2f €", totalSum));
        exporter.addParagraph(" ");

        // Tabelle mit Userdetails
        exporter.addParagraph("User Details:");
        exporter.addTable(
                java.util.List.of("User Email", "Invoice Count", "Total Reimbursement (€)"),
                userDetails.entrySet().stream()
                        .filter(e -> !e.getKey().equals("month") && !e.getKey().equals("total_all_users"))
                        .map(e -> {
                            Map<?, ?> map = (Map<?, ?>) e.getValue();
                            return java.util.List.of(
                                    e.getKey().toString(),
                                    String.valueOf(map.get("invoice_count")),
                                    String.format("%.2f", (Double) map.get("total_reimbursement"))
                            );
                        }).toList()
        );
        exporter.addParagraph(" ");

        exporter.end();
        exporter.saveToFile("reimbursement_per_month_detailed");

        showStatus("PDF export with detailed user info successful!", true);
    }

    public void exportCsv() throws IOException, SQLException {
        Map<String, Map<String, Map<String, Object>>> userReimbursementDetails = statistics.getUserReimbursementDetailsPerMonth();

        List<Map<String, String>> rows = new ArrayList<>();

        for (var monthEntry : userReimbursementDetails.entrySet()) {
            String month = monthEntry.getKey();

            if (!rows.isEmpty()) {
                Map<String, String> emptyRow = new LinkedHashMap<>();
                emptyRow.put("Month", "");
                emptyRow.put("User Name", "");
                emptyRow.put("Email", "");
                emptyRow.put("Total Reimbursement (€)", "");
                rows.add(emptyRow);
            }

            Map<String, String> monthRow = new LinkedHashMap<>();
            monthRow.put("Month", month);
            monthRow.put("User Name", "");
            monthRow.put("Email", "");
            monthRow.put("Total Reimbursement (€)", "");
            rows.add(monthRow);

            for (var userEntry : monthEntry.getValue().entrySet()) {
                Map<String, Object> userData = userEntry.getValue();
                Map<String, String> row = new LinkedHashMap<>();
                row.put("Month", "");
                row.put("User Name", (String)userData.getOrDefault("name", ""));
                row.put("Email", (String)userData.getOrDefault("email", ""));
                row.put("Total Reimbursement (€)", String.format("%.2f", userData.getOrDefault("total_reimbursement", 0.0)));
                rows.add(row);
            }
        }

        CsvExporter exporter = new CsvExporter(";");
        exporter.export(rows, "reimbursement_per_month_detailed");
        showStatus("CSV export with detailed user info successful!", true);
    }

    public void exportJson() throws SQLException, IOException {
        Map<String, Object> jsonData = statistics.getUserReimbursementDetails();
        JsonExporter exporter = new JsonExporter();
        exporter.export(jsonData, "reimbursement_details");
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
