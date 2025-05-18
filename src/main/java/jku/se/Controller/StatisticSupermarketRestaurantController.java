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
        // Initialize pie chart with counts of invoices per supermarket and restaurant
        int supermarketCount = statistics.getInvoicesPerSupermaket();
        int restaurantCount = statistics.getInvoicesPerRestaurant();

        pieChartDistribution.setData(FXCollections.observableArrayList(
                new PieChart.Data("Supermarket (" + supermarketCount + ")", supermarketCount),
                new PieChart.Data("Restaurant (" + restaurantCount + ")", restaurantCount)
        ));

        // Setup export format options (PDF and CSV only)
        saveFormatComboBox.getItems().addAll("PDF", "CSV");
        saveFormatComboBox.getSelectionModel().selectFirst();

        // Setup status message timer to clear messages after 3 seconds
        statusTimer = new PauseTransition(Duration.seconds(3));
        statusTimer.setOnFinished(e -> statusText.setText(""));
    }

    // Handle export of distribution data in selected format
    @FXML
    private void handleExport(ActionEvent event) {
        Map<String, Integer> data = new HashMap<>();
        data.put("Supermarket", statistics.getInvoicesPerSupermaket());
        data.put("Restaurant", statistics.getInvoicesPerRestaurant());

        exportSingleFormat(
                statusText,
                "supermarket_restaurant_distribution",
                data,
                "Supermarket vs Restaurant",
                saveFormatComboBox.getValue()
        );
    }

    // Cancel and return to statistics page
    @FXML
    private void cancelDistribution(ActionEvent event) throws IOException {
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
