package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jku.se.Invoice;
import jku.se.Notification;
import jku.se.User;
import jku.se.UserSession;
import jku.se.repository.InvoiceRepository;
import jku.se.Utilities.NotificationManager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for displaying user-specific invoice notifications.
 * Shows all invoices with delete buttons.
 */
public class NotificationsUserController {

    @FXML
    private VBox notificationContainer;

    private static final Logger LOGGER = Logger.getLogger(NotificationsUserController.class.getName());

    /**
     * Initializes the notification list with all notifications
     * and adds delete buttons for each notification
     */
    @FXML
    public void initialize() {
        try {
            User currentUser = UserSession.getCurrentUser();
            String email = currentUser.getEmail();

            notificationContainer.getChildren().clear();

            List<Notification> userNotifications = NotificationManager.getInstance().getUserSpecificNotifications(email);

            addMissingInvoiceNotifications(email, userNotifications);

            List<Notification> finalNotifications = NotificationManager.getInstance().getUserSpecificNotifications(email);

            if (finalNotifications.isEmpty()) {
                addInfoMessage("No notifications found.");
            } else {
                for (Notification notification : finalNotifications) {
                    if (notification.getMessage().contains("approved") || notification.getMessage().contains("declined") && notification.getMessage().matches("Your Invoice from \\d{4}-\\d{2}-\\d{2} was (approved|declined)\\.")) {
                        addNotificationToContainer(notification, email);
                    }
                }
            }

            if (notificationContainer.getChildren().isEmpty()) {
                addInfoMessage("No notifications found.");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading notifications: ", e);
            addInfoMessage("Error loading notifications: " + e.getMessage());
        }
    }

    private void addMissingInvoiceNotifications(String email, List<Notification> existingNotifications) {
        List<Invoice> allInvoices = InvoiceRepository.getAllInvoicesUser(email);

        for (Invoice invoice : allInvoices) {
            if (invoice.getStatus() == jku.se.Status.APPROVED || invoice.getStatus() == jku.se.Status.DECLINED) {

                String statusWord = invoice.getStatus() == jku.se.Status.APPROVED ? "approved" : "declined";
                String dateIso = invoice.getDate().toString();
                String dateDot = invoice.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                boolean notificationExists = existingNotifications.stream().anyMatch(n ->
                        n.getMessage().equals("Your Invoice from " + dateIso + " was " + statusWord + ".") ||
                                n.getMessage().equals("Your invoice from " + dateDot + " was " + statusWord + ".")
                );

                if (!notificationExists) {
                    String message = "Your Invoice from " + dateIso + " was " + statusWord + ".";

                    if (!NotificationManager.getInstance().isNotificationDeleted(email, message)) {
                        Notification newNotification = new Notification(message);
                        NotificationManager.getInstance().addNotificationForUser(email, newNotification);
                    }
                }
            }
        }
    }

    /**
     * Adds a notification to the container with a delete button.
     * @param notification The notification to add.
     * @param userEmail The email of the current user.
     */
    private void addNotificationToContainer(Notification notification, String userEmail) {
        HBox notificationBox = new HBox(10);
        notificationBox.setStyle("-fx-padding: 8; -fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label messageLabel = new Label(notification.getUserMessage());
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(500);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

        // Create delete button with trash icon
        Button deleteButton = new Button("🗑");
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30; -fx-min-height: 30;");
        deleteButton.setOnAction(e -> deleteNotification(notification.getId(), notificationBox, userEmail));

        notificationBox.getChildren().addAll(messageLabel, deleteButton);
        notificationContainer.getChildren().add(notificationBox);
    }

    /**
     * Adds an info message to the container.
     * @param message The message to display.
     */
    private void addInfoMessage(String message) {
        Label infoLabel = new Label(message);
        infoLabel.setStyle("-fx-font-style: italic; -fx-text-fill: black; -fx-padding: 10;");
        notificationContainer.getChildren().add(infoLabel);
    }

    /**
     * Deletes a notification from the NotificationManager and removes it from the UI.
     * @param notificationId The ID of the notification to delete.
     * @param notificationBox The UI element to remove.
     * @param userEmail The email of the current user.
     */
    private void deleteNotification(String notificationId, HBox notificationBox, String userEmail) {
        boolean removed = NotificationManager.getInstance().removeNotificationForUser(userEmail, notificationId);
        if (removed) {
            notificationContainer.getChildren().remove(notificationBox);
            LOGGER.info("Notification deleted: " + notificationId);
        } else {
            LOGGER.warning("Failed to delete notification: " + notificationId);
        }
    }

    /**
     * Closes the notification window.
     */
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) notificationContainer.getScene().getWindow();
        stage.close();
    }
}

