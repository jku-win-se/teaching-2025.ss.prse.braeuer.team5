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
import jku.se.UserInvoiceData;
import jku.se.export.CsvExporter;
import jku.se.export.PdfExporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class StatisticNumberOfInvoicesController {
    @FXML private BarChart<String, Number> barChartInvoicesPerMonth;
    @FXML private ComboBox<String> saveFormatComboBox;
    @FXML private Text statusText;

    private final Statistics statistics = new Statistics();
    private PauseTransition statusTimer;

    @FXML
    public void initialize() {
        Map<String, Integer> invoicesPerMonth = statistics.getInvoicesPerMonth();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Invoices per Month");

        for (Map.Entry<String, Integer> entry : invoicesPerMonth.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) newNode.setStyle("-fx-bar-fill: lightblue;");
            });
        }

        barChartInvoicesPerMonth.getData().clear();
        barChartInvoicesPerMonth.getData().add(series);

        saveFormatComboBox.getItems().addAll("PDF", "CSV");
        saveFormatComboBox.getSelectionModel().selectFirst();

        statusTimer = new PauseTransition(Duration.seconds(3));
        statusTimer.setOnFinished(e -> statusText.setText(""));
    }

    @FXML
    private void handleExport() {
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
        Map<String, Integer> invoicesPerMonth = statistics.getInvoicesPerMonth();
        Map<String, Map<String, UserInvoiceData>> userInvoicesPerMonth = statistics.getInvoicesPerUserAndMonth();

        PdfExporter exporter = new PdfExporter();
        exporter.startPage();
        exporter.addTitle("Invoices Per Month Report");

        // 1. Bild vom Chart erstellen und einfügen
        WritableImage snapshot = barChartInvoicesPerMonth.snapshot(null, null);
        File tempImageFile = File.createTempFile("chart_snapshot", ".png");
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
        ImageIO.write(bufferedImage, "png", tempImageFile);

        // Bild in PDF laden und zeichnen
        PDImageXObject pdImage = PDImageXObject.createFromFileByContent(tempImageFile, exporter.getDocument());
        PDPageContentStream contentStream = exporter.getContentStream();

        // Beispielposition und Skalierung (ggf. anpassen)
        int imgWidth = 300;
        int imgHeight = 225;
        int imgX = 50;
        int imgY = exporter.getYPosition() - imgHeight - 20;

        contentStream.drawImage(pdImage, imgX, imgY, imgWidth, imgHeight);
        exporter.setYPosition(imgY - 30);

        // Tabelle 1: Gesamte Rechnungen pro Monat
        List<String> headersMonth = List.of("Month", "Invoice Count");
        List<List<String>> rowsMonth = new ArrayList<>();
        for (var entry : invoicesPerMonth.entrySet()) {
            rowsMonth.add(List.of(entry.getKey(), String.valueOf(entry.getValue())));
        }
        exporter.addTable(headersMonth, rowsMonth);

        exporter.addParagraph(" ");

        // Tabelle 2: Detaillierte User-Statistik pro Monat
        for (var monthEntry : userInvoicesPerMonth.entrySet()) {
            String month = monthEntry.getKey();
            Map<String, UserInvoiceData> userMap = monthEntry.getValue();

            exporter.addParagraph("Details for Month: " + month);

            List<String> headersUser = List.of("User Name", "Email", "Invoice Count");
            List<List<String>> rowsUser = new ArrayList<>();

            for (UserInvoiceData userData : userMap.values()) {
                rowsUser.add(List.of(
                        userData.getName(),
                        userData.getEmail(),
                        String.valueOf(userData.getInvoiceCount())
                ));
            }
            exporter.addTable(headersUser, rowsUser);

            exporter.addParagraph(" ");
        }

        exporter.end();
        exporter.saveToFile("invoices_per_month_detailed");

        // Temporäre Bilddatei löschen
        tempImageFile.delete();

        showStatus("PDF export with chart successful!", true);
    }

    private void exportCsv() throws IOException {
        Map<String, Integer> invoicesPerMonth = statistics.getInvoicesPerMonth();

        List<Map<String, String>> rows = new ArrayList<>();
        for (var entry : invoicesPerMonth.entrySet()) {
            rows.add(Map.of("Month", entry.getKey(), "Invoice Count", String.valueOf(entry.getValue())));
        }

        CsvExporter exporter = new CsvExporter();
        exporter.export(rows, "invoices_per_month");
        showStatus("CSV export successful!", true);
    }

    private void showStatus(String message, boolean success) {
        statusText.setStyle(success ? "-fx-fill: green;" : "-fx-fill: red;");
        statusText.setText(message);
        statusTimer.playFromStart();
    }

    @FXML
    private void cancelNumberOfInvoices(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistics.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
