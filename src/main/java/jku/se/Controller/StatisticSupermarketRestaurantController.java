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

public class StatisticSupermarketRestaurantController extends BaseStatisticController {
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

        // Export-Einstellungen (nur PDF/CSV, da JSON nicht benötigt)
        saveFormatComboBox.getItems().addAll("PDF", "CSV");
        saveFormatComboBox.getSelectionModel().selectFirst();

        // Status-Timer
        statusTimer = new PauseTransition(Duration.seconds(3));
        statusTimer.setOnFinished(e -> statusText.setText(""));
    }

    @FXML
    private void handleExport(ActionEvent event) {
        Map<String, Integer> data = new HashMap<>();
        data.put("Supermarket", statistics.getInvoicesPerSupermaket());
        data.put("Restaurant", statistics.getInvoicesPerRestaurant());

        // Nutzung der Basis-Controller-Methode
        exportSingleFormat(
                statusText,
                "supermarket_restaurant_distribution",
                data,
                "Supermarket vs Restaurant",
                saveFormatComboBox.getValue()
        );
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