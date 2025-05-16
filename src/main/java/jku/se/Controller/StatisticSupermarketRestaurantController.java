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
import jku.se.Utilities.ExportUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StatisticSupermarketRestaurantController {
    @FXML private PieChart pieChartDistribution;
    @FXML private ComboBox<String> saveFormatComboBox;
    @FXML private Text statusText;

    private final Statistics statistics = new Statistics();
    private PauseTransition statusTimer;

    @FXML
    public void initialize() {
        // Chart initialisieren
        int supermarketCount = statistics.getInvoicesPerSupermaket();
        int restaurantCount = statistics.getInvoicesPerRestaurant();

        pieChartDistribution.setData(FXCollections.observableArrayList(
                new PieChart.Data("Supermarket (" + supermarketCount + ")", supermarketCount),
                new PieChart.Data("Restaurant (" + restaurantCount + ")", restaurantCount)
        ));

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
            Map<String, Integer> data = new HashMap<>();
            data.put("Supermarket", statistics.getInvoicesPerSupermaket());
            data.put("Restaurant", statistics.getInvoicesPerRestaurant());

            boolean success = false;
            switch (saveFormatComboBox.getValue()) {
                case "JSON":
                    success = ExportUtils.exportToJson(data, "supermarket_restaurant_distribution");
                    break;
                case "PDF":
                    success = ExportUtils.exportToPdf(data,
                            "Supermarket vs Restaurant",
                            "supermarket_restaurant_distribution");
                    break;
                case "CSV":
                    success = ExportUtils.exportToCsv(data, "supermarket_restaurant_distribution");
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
    private void cancelDistribution(ActionEvent event) throws IOException {
        loadPage("Statistics.fxml", event);
    }

    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}