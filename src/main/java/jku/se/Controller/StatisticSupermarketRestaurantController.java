package jku.se.Controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import jku.se.Statistics;
import jku.se.export.CsvExporter;
import jku.se.export.PdfExporter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class StatisticSupermarketRestaurantController {
    @FXML private PieChart pieChartDistribution;
    @FXML private ComboBox<String> saveFormatComboBox;


    private final Statistics statistics = new Statistics();


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


        //design
        Platform.runLater(() -> {
            for (PieChart.Data data : pieChartDistribution.getData()) {
                String name = data.getName();
                if (name.contains("Supermarket")) {
                    data.getNode().setStyle("-fx-pie-color: lightblue;");
                } else if (name.contains("Restaurant")) {
                    data.getNode().setStyle("-fx-pie-color: grey;");
                }
            }

            for (Node node : pieChartDistribution.lookupAll(".chart-legend-item")) {
                if (node instanceof Label label) {
                    String text = label.getText();
                    if (text.contains("Supermarket")) {
                        label.getGraphic().setStyle("-fx-background-color: lightblue;");
                    } else if (text.contains("Restaurant")) {
                        label.getGraphic().setStyle("-fx-background-color: grey;");
                    }
                }
            }
        });
    }

    @FXML
    private void handleExport(ActionEvent event) {
        String format = saveFormatComboBox.getValue();
        try {
            switch (format) {
                case "PDF":
                    exportPdf();
                    break;
                case "CSV":
                    exportCsv();
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Export Error", "Unsupported export format: " + format);

            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Export Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    private void exportPdf() throws IOException {
        int supermarketCount = statistics.getInvoicesPerSupermaket();
        int restaurantCount = statistics.getInvoicesPerRestaurant();

        PdfExporter exporter = new PdfExporter();
        exporter.startPage();
        exporter.addTitle("Supermarket vs Restaurant Distribution");

        // Grafik als Bild proportional einfügen
        WritableImage fxImage = pieChartDistribution.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

        int imgWidth = bufferedImage.getWidth();
        int imgHeight = bufferedImage.getHeight();
        float aspectRatio = (float) imgHeight / imgWidth;
        float desiredWidth = 300f;
        float desiredHeight = desiredWidth * aspectRatio;

        exporter.addImageAndMovePosition(bufferedImage, 50, desiredWidth, desiredHeight, 20);

        // Tabelle mit den Zählungen
        exporter.addParagraph("Invoice Counts:");
        exporter.addTable(
                List.of("Category", "Count"),
                List.of(
                        List.of("Supermarket", String.valueOf(supermarketCount)),
                        List.of("Restaurant", String.valueOf(restaurantCount))
                )
        );

        exporter.end();
        exporter.saveToFile("supermarket_restaurant_distribution");

        showAlert(Alert.AlertType.INFORMATION,"Export Success","PDF export successful!");
    }

    private void exportCsv() throws IOException {
        int supermarketCount = statistics.getInvoicesPerSupermaket();
        int restaurantCount = statistics.getInvoicesPerRestaurant();

        List<Map<String, String>> rows = new ArrayList<>();

        Map<String, String> rowSupermarket = new LinkedHashMap<>();
        rowSupermarket.put("Category", "Supermarket");
        rowSupermarket.put("Invoice Count", String.valueOf(supermarketCount));
        rows.add(rowSupermarket);

        Map<String, String> rowRestaurant = new LinkedHashMap<>();
        rowRestaurant.put("Category", "Restaurant");
        rowRestaurant.put("Invoice Count", String.valueOf(restaurantCount));
        rows.add(rowRestaurant);

        CsvExporter exporter = new CsvExporter(";");
        exporter.export(rows, "supermarket_vs_restaurant_distribution");

        showAlert(Alert.AlertType.INFORMATION,"Export Success", "CSV export successful!");
    }

    //add information in an alert
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
