package jku.se.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import jku.se.Statistics;

import java.io.IOException;

public class StatisticSupermarketRestaurantController {
    @FXML
    private PieChart pieChartDistribution;

    private final Statistics statistics = new Statistics();

    @FXML //ai
    public void initialize() {
        int supermarketCount = statistics.getInvoicesPerSupermaket();
        int restaurantCount = statistics.getInvoicesPerRestaurant();

        PieChart.Data supermarketData = new PieChart.Data("Supermarket (" + supermarketCount + ")", supermarketCount);
        PieChart.Data restaurantData = new PieChart.Data("Restaurant (" + restaurantCount + ")", restaurantCount);

        pieChartDistribution.setData(FXCollections.observableArrayList(
                supermarketData,
                restaurantData
        ));


        Platform.runLater(() -> {
            supermarketData.getNode().setStyle("-fx-pie-color: lightblue;");
            restaurantData.getNode().setStyle("-fx-pie-color: grey;");


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
    private void cancelDistribution(ActionEvent event)throws IOException {
        loadPage("Statistics.fxml", event);
    }

    @FXML
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }
}
