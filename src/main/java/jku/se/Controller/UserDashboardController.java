package jku.se.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UserDashboardController {
    @FXML
    private TableView<Invoice> invoiceTable;

    @FXML
    private Button toggleTableSize;

    @FXML
    private TableColumn<Invoice, String> submissionDateColumn;

    @FXML
    private TableColumn<Invoice, Double> amountColumn;

    @FXML
    private TableColumn<Invoice, Category> categoryColumn;

    @FXML
    private TableColumn<Invoice, Status> statusColumn;

    @FXML
    private TableColumn<Invoice, Double> reimbursementColumn;

    private static String currentUserEmail;
    private boolean isTableExpanded = false;
    private final double COLLAPSED_HEIGHT = 138.0;
    private final double EXPANDED_HEIGHT = 400.0;

    // Setter-Methode
    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
        loadInvoices();
    }
    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    //fill table with invoices
    @FXML
    private void initialize() {
        // Connect columns with the invoice attributes
        submissionDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryString"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusString"));
        reimbursementColumn.setCellValueFactory(new PropertyValueFactory<>("reimbursement"));

        loadInvoices();
    }

    private void loadInvoices() {
        if (invoiceTable == null) {
            System.err.println("invoiceTable is null! Check whether the fx:id is set correctly in the FXML file.");
            return;  // Prevent the method from being executed further if the TableView is null
        }
        // load invoices
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesUser(currentUserEmail);
        invoiceTable.getItems().setAll(invoices);
    }


    @FXML
    private void toggleTableSize(ActionEvent event) {
        try {
            isTableExpanded = !isTableExpanded;
            String arrow = isTableExpanded ? "▼" : "▲";

            // Lösung 1: Direkter Zugriff
            toggleTableSize.setText(arrow);

            // ODER Lösung 2: Mit Null-Check
            if (toggleTableSize != null) {
                toggleTableSize.setText(arrow);
            }

            // Höhenanpassung wie zuvor
            invoiceTable.setPrefHeight(isTableExpanded ? EXPANDED_HEIGHT : COLLAPSED_HEIGHT);

        } catch (Exception e) {
            System.err.println("Fehler beim Button-Update: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditInvoiceUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditInvoice.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleUploadInvoice(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddInvoice.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleLogoutUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }
}
