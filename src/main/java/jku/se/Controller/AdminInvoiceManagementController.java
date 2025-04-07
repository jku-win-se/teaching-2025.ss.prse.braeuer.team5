package jku.se.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import jku.se.Administrator;
import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminInvoiceManagementController {

    @FXML private ComboBox<Invoice> selectInvoice;
    @FXML private ComboBox<String> selectUser;
    @FXML private TextField amountField;
    @FXML private TextField reimbursementField;
    @FXML private DatePicker invoiceDateField;
    @FXML private ComboBox<String> categoryCombobox;
    @FXML private Button changeButton;
    @FXML private Button invoiceAcceptButton;
    @FXML private Button declinedButton;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        List<String> emails = UserRepository.getAllUserEmails();
        selectUser.setItems(FXCollections.observableArrayList(emails));
        selectUser.setOnAction(e -> {
            searchInvoice(new ActionEvent());
        });
        //generated by ai
        //load details of invoice
        selectInvoice.setOnAction(e -> {
            Invoice selectedInvoice = selectInvoice.getValue();
            if (selectedInvoice != null) {
                amountField.setText(String.valueOf(selectedInvoice.getAmount()));
                reimbursementField.setText(String.valueOf(selectedInvoice.getReimbursement()));
                invoiceDateField.setValue(selectedInvoice.getDate());
                categoryCombobox.setValue(selectedInvoice.getCategory().name());
            }
            categoryCombobox.setItems(FXCollections.observableArrayList(
                    "SUPERMARKET", "RESTAURANT"
            ));
        });
    }

    //choice user - current user are not in
    @FXML
    private void searchUser(ActionEvent event) throws IOException {
        List<String> emails = UserRepository.getAllUsersWithoutLoggedAdmin(UserDashboardController.getCurrentUserEmail());

        selectUser.setItems(FXCollections.observableArrayList(emails));
    }

    @FXML
    //choice invoice from user
    private void searchInvoice(ActionEvent event) {
        String selectedUserEmail = selectUser.getValue();
        System.out.println("Selected user email: " + selectedUserEmail); // Debug

        if (selectedUserEmail == null || selectedUserEmail.isEmpty()) {
            statusLabel.setText("Please select an user first.");
            return;
        }

        List<Invoice> invoices = InvoiceRepository.getAllInvoicesUser(selectedUserEmail);

        selectInvoice.setItems(FXCollections.observableArrayList(invoices));
    }

    //is changed when you press the change button
    @FXML
    private void changeInvoiceDetails(ActionEvent event) {
        Invoice selectedInvoice = selectInvoice.getValue();

        if (selectedInvoice != null) {
            try {
                double newAmount = Double.parseDouble(amountField.getText());
                double newReimbursement = Double.parseDouble(reimbursementField.getText());
                String newCategory = categoryCombobox.getValue();
                var newDate = invoiceDateField.getValue();

                selectedInvoice.setAmount(newAmount);
                selectedInvoice.setReimbursement(newReimbursement);
                selectedInvoice.setCategory(Category.valueOf(newCategory));
                selectedInvoice.setDate(newDate);
                selectedInvoice.setStatus(Status.PROCESSING);  // after update invoice details set status to processing

                InvoiceRepository.updateInvoiceAmount(selectedInvoice);
                InvoiceRepository.updateInvoiceReimbursement(selectedInvoice);
                InvoiceRepository.updateInvoiceCategory(selectedInvoice);
                InvoiceRepository.updateInvoiceDate(selectedInvoice);
                InvoiceRepository.updateInvoiceStatus(selectedInvoice);

                statusLabel.setText("Invoice updated successfully.");
                statusLabel.setStyle("-fx-text-fill: green;");
            } catch (Exception e) {
                statusLabel.setText("Update failed: " + e.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }
    //accept invoice update db
    @FXML
    private void handleAcceptInvoice(ActionEvent event) {
        Invoice selectedInvoice = selectInvoice.getValue();

        if (selectedInvoice != null) {
            selectedInvoice.setStatus(Status.APPROVED);
            InvoiceRepository.updateInvoiceStatus(selectedInvoice);
            statusLabel.setText("Invoice accepted successfully.");
            statusLabel.setStyle("-fx-text-fill: green;");
        }
    }

    //declined invoice update db
    @FXML
    private void handleDeclinedInvoice(ActionEvent event) {
        Invoice selectedInvoice = selectInvoice.getValue();

        if (selectedInvoice != null) {
            selectedInvoice.setStatus(Status.DECLINED);
            InvoiceRepository.updateInvoiceStatus(selectedInvoice);
            statusLabel.setText("Invoice declined.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }


    @FXML
    private void cancelEditAdmin(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard2.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        }
    }
