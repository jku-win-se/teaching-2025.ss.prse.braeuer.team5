package jku.se.Controller;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import jku.se.Statistics;
import jku.se.UserInvoiceData;
import jku.se.export.CsvExporter;
import jku.se.export.JsonExporter;
import jku.se.export.PdfExporter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class StatisticNumberOfInvoicesController {
    @FXML private BarChart<String, Number> barChartInvoicesPerMonth;
    @FXML private ComboBox<String> saveFormatComboBox;


    public Statistics statistics = new Statistics();


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

        //chart legend in blue
        Platform.runLater(() -> {
            Set<Node> legendItems = barChartInvoicesPerMonth.lookupAll(".chart-legend-item-symbol");
            for (Node node : legendItems) {
                node.setStyle("-fx-background-color: lightblue;");
            }
        });

        saveFormatComboBox.getItems().addAll("PDF", "CSV", "JSON");
        saveFormatComboBox.getSelectionModel().selectFirst();

    }

    @FXML
    private void handleExport() {
        String format = saveFormatComboBox.getValue();
        try {
            switch (format) {
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
                    showAlert(Alert.AlertType.ERROR, "Export Failed","Unsupported format: " + format);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Export Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    public void exportPdf() throws IOException {
        Map<String, Integer> invoicesPerMonth = statistics.getInvoicesPerMonth();
        Map<String, Map<String, UserInvoiceData>> userInvoicesPerMonth = statistics.getInvoicesPerUserAndMonth();

        PdfExporter exporter = new PdfExporter();
        exporter.startPage();

        // 1. Add title (at the top)
        exporter.addTitle("Invoices Per Month Report");

        // 2. Insert the chart as an image into the PDF and adjust the yPosition (x=50, width=300, height=150, spacing 20)
        WritableImage fxImage = barChartInvoicesPerMonth.snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);
        exporter.addImageAndMovePosition(bufferedImage, 50, 300, 180, 20);

        // 3. Table total number per month (comes below the image)
        List<String> headersMonth = List.of("Month", "Invoice Count");
        List<List<String>> rowsMonth = new ArrayList<>();
        for (var entry : invoicesPerMonth.entrySet()) {
            rowsMonth.add(List.of(entry.getKey(), String.valueOf(entry.getValue())));
        }
        exporter.addTable(headersMonth, rowsMonth);
        exporter.addParagraph(" ");

        // 4. Table Detail User per Month (under the first table)
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

        showAlert(Alert.AlertType.INFORMATION, "Export Success","PDF export with detailed user info successful!");
    }



    public void exportCsv() throws IOException {
        Map<String, Map<String, UserInvoiceData>> invoicesPerUserAndMonth = statistics.getInvoicesPerUserAndMonth();

        List<Map<String, String>> rows = new ArrayList<>();

        for (var monthEntry : invoicesPerUserAndMonth.entrySet()) {
            String month = monthEntry.getKey();

            if (!rows.isEmpty()) {
                Map<String, String> emptyRow = new LinkedHashMap<>();
                emptyRow.put("Month", "");
                emptyRow.put("User Name", "");
                emptyRow.put("Email", "");
                emptyRow.put("Invoice Count", "");
                rows.add(emptyRow);
            }

            Map<String, String> monthRow = new LinkedHashMap<>();
            monthRow.put("Month", month);
            monthRow.put("User Name", "");
            monthRow.put("Email", "");
            monthRow.put("Invoice Count", "");
            rows.add(monthRow);

            for (UserInvoiceData userData : monthEntry.getValue().values()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("Month", "");
                row.put("User Name", userData.getName());
                row.put("Email", userData.getEmail());
                row.put("Invoice Count", String.valueOf(userData.getInvoiceCount()));
                rows.add(row);
            }
        }

        CsvExporter exporter = new CsvExporter(";");
        exporter.export(rows, "invoices_per_month_detailed");
        showAlert(Alert.AlertType.INFORMATION, "Export Success","CSV export with detailed user info successful!");
    }


    public void exportJson() throws IOException {
        Map<String, Integer> invoicesPerMonth = statistics.getInvoicesPerMonth();
        Map<String, Map<String, UserInvoiceData>> userInvoicesPerMonth = statistics.getInvoicesPerUserAndMonth();

        Map<String, Object> jsonData = new LinkedHashMap<>();
        jsonData.put("invoicesPerMonth", invoicesPerMonth);

        Map<String, Object> userDetailsJson = new LinkedHashMap<>();
        for (var monthEntry : userInvoicesPerMonth.entrySet()) {
            List<Map<String, Object>> usersList = new ArrayList<>();
            for (UserInvoiceData userData : monthEntry.getValue().values()) {
                Map<String, Object> userMap = new LinkedHashMap<>();
                userMap.put("name", userData.getName());
                userMap.put("email", userData.getEmail());
                userMap.put("invoiceCount", userData.getInvoiceCount());
                usersList.add(userMap);
            }
            userDetailsJson.put(monthEntry.getKey(), usersList);
        }
        jsonData.put("userInvoicesPerMonth", userDetailsJson);

        JsonExporter exporter = new JsonExporter();
        exporter.export(jsonData, "invoices_per_month_detailed");
        showAlert(Alert.AlertType.INFORMATION, "Export Success","JSON export successful!");
    }
    //add alert for information
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
