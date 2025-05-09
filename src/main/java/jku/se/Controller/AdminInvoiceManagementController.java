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
import jku.se.*;
import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
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

        if (selectedInvoice == null) {
            statusLabel.setText("Please select an invoice to update.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        //save old and new date for changing date
        LocalDate oldDate = selectedInvoice.getDate();
        Category oldCategory = selectedInvoice.getCategory();

        //check if invoice is in current month
        LocalDate now = LocalDate.now();
        if(!selectedInvoice.isInCurrentMonth(oldDate, now)){
            statusLabel.setText("The invoice can no longer be changed - only possible in the current month!");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (selectedInvoice != null) {
            try {
                double newAmount = Double.parseDouble(amountField.getText());
                String newCategory = categoryCombobox.getValue();
                LocalDate newDate = invoiceDateField.getValue();

                if (!selectedInvoice.isValidAmount(newAmount)) {
                    statusLabel.setText("Amount must be greater than 0 and less than 1000!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }

                selectedInvoice.setAmount(newAmount);
                selectedInvoice.setCategory(Category.valueOf(newCategory));

                if(!selectedInvoice.isDateOnWeekday(newDate)){
                    statusLabel.setText("Invoice date must be a weekday!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                // Check if date changed - AI generated
                if (!newDate.equals(oldDate)) {
                    try (Connection con = DatabaseConnection.getConnection()) {
                        boolean exists = InvoiceRepository.invoiceExists(con, selectedInvoice.getUserEmail(), java.sql.Date.valueOf(newDate));
                        if (exists) {
                            statusLabel.setText("An invoice already exists for this user on the selected date.");
                            statusLabel.setStyle("-fx-text-fill: red;");
                            return;
                        }
                    } catch (SQLException e) {
                        statusLabel.setText("Database error: " + e.getMessage());
                        statusLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                }
                selectedInvoice.setDate(newDate);
                selectedInvoice.setStatus(Status.PROCESSING);  // after update invoice details set status to processing

                //after changing category or amount - change reimbursement
                double newReimbursement = selectedInvoice.calculateRefund();
                selectedInvoice.setReimbursement(newReimbursement);

                InvoiceRepository.updateInvoiceAmount(selectedInvoice);
                InvoiceRepository.updateInvoiceCategory(selectedInvoice);
                InvoiceRepository.updateInvoiceDate(selectedInvoice);
                InvoiceRepository.updateInvoiceStatus(selectedInvoice);
                InvoiceRepository.updateInvoiceReimbursement(selectedInvoice);
                statusLabel.setText("Invoice updated successfully.");
                statusLabel.setStyle("-fx-text-fill: green;");

                //Update data output in JAVAFX
                amountField.setText(String.valueOf(selectedInvoice.getAmount()));
                reimbursementField.setText(String.valueOf(selectedInvoice.getReimbursement()));
                invoiceDateField.setValue(selectedInvoice.getDate());
                categoryCombobox.setValue(selectedInvoice.getCategory().name());
            } catch (Exception e) {
                statusLabel.setText("Update failed: " + e.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        }

    }
    //accept invoice update db
    @FXML
    private void handleAcceptInvoice(ActionEvent event) throws SQLException {
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
    private void handleDeclinedInvoice(ActionEvent event) throws SQLException {
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
