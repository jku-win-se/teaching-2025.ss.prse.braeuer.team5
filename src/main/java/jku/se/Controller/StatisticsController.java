package jku.se.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jku.se.Statistics;

import java.io.IOException;

/**
 * Controller for the Statistics overview screen.
 * Displays summary data (total yearly reimbursement and average invoices per user)
 * and provides navigation to detailed statistical views.
 */
public class StatisticsController {

    @FXML private Label cancelButton;
    @FXML private Text textFieldTotalRefund;
    @FXML private Text textFieldAverageInvoices;
    @FXML private RadioButton distributionRadioButton;
    @FXML private RadioButton invoicesRadioButton;
    @FXML private RadioButton reimbursementRadioButton;
    @FXML private BarChart<String, Number> barChart;
    @FXML private ToggleGroup statisticsToggleGroup;

    private final Statistics statistics = new Statistics();

    /**
     * Initializes the statistics view with summary values.
     * - Displays total yearly reimbursement
     * - Displays average invoices per user per month
     */
    @FXML
    public void initialize() {
        // Calculate and display total refund and average invoices per user per month
        double totalRefund = statistics.getReimbursementForAYear();
        textFieldTotalRefund.setText(String.format("%.2f €", totalRefund));
        double averageInvoicesPerson = statistics.getAverageOfInvoicesPerUserPerMonth();
        textFieldAverageInvoices.setText(String.format("%.2f", averageInvoicesPerson));
    }

    /**
     * Navigates to the "Restaurant vs Supermarket" distribution statistics view.
     *
     * @param event the event triggered by clicking the corresponding radio button or action
     * @throws IOException if FXML loading fails
     */
    @FXML
    private void statisticDistributionRestaurantSupermarket(ActionEvent event) throws IOException {
        loadPage("StatisticSupermarketRestaurant.fxml", event);
    }

    /**
     * Navigates to the "Number of Invoices per Month" statistics view.
     *
     * @param event the event triggered by clicking the corresponding radio button or action
     * @throws IOException if FXML loading fails
     */
    @FXML
    private void statisticNumberInvoicesPerMonth(ActionEvent event) throws IOException{
        loadPage("StatisticNumberOfInvoices.fxml", event);
    }

    /**
     * Navigates to the "Reimbursement per Month" statistics view.
     *
     * @param event the event triggered by clicking the corresponding radio button or action
     * @throws IOException if FXML loading fails
     */
    @FXML
    private void statisticReimbursementPerMonth(ActionEvent event) throws IOException{
        loadPage("StatisticReimbursementPerMonth.fxml", event);
    }

    /**
     * Cancels the statistics view and returns to the admin dashboard.
     *
     * @param event the triggered event
     * @throws IOException if FXML loading fails
     */
    @FXML
    private void cancelStatistics(ActionEvent event) throws IOException {
        loadPage("dashboard2.fxml", event);
    }

    /**
     * Helper method to load and switch to another FXML page.
     *
     * @param fxmlFile the filename of the FXML page to load
     * @param event    the triggered action event
     * @throws IOException if loading fails
     */
    @FXML
    private void loadPage(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }
}
