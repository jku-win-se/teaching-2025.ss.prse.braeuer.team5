package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import jku.se.Invoice;
import jku.se.User;
import jku.se.UserSession;
import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationsUserController {

    @FXML
    private ListView<String> notificationList;

    private static final Logger LOGGER = Logger.getLogger(NotificationsUserController.class.getName());

    @FXML
    public void initialize() {
        try {
            User currentUser = UserSession.getCurrentUser();

            String email = currentUser.getEmail();
            LOGGER.info("Loading Notifications for User: " + email);

            // Abgelehnte Rechnungen holen
            List<Invoice> declinedInvoices = InvoiceRepository.getDeclinedInvoicesCurrentMonth(email);

            if (declinedInvoices.isEmpty()) {
                notificationList.getItems().add("No declined Invoices for this month.");
            } else {
                for (Invoice invoice : declinedInvoices) {
                    notificationList.getItems().add(
                            "Your Invoice from " + invoice.getDate() + " was declined.");
                }
            }

            List<Invoice> acceptedInvoices = InvoiceRepository.getAcceptedInvoicesCurrentMonth(email);
            if (acceptedInvoices.isEmpty()) {
                notificationList.getItems().add("No accepted Invoices this month.");
            } else {
                for (Invoice invoice : acceptedInvoices) {
                    notificationList.getItems().add("Invoice from " + invoice.getDate() + " was accepted.");
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Benachrichtigungen: ", e);
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) notificationList.getScene().getWindow();
        stage.close();
    }
}

