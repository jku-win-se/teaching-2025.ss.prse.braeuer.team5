package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import jku.se.Invoice;
import jku.se.Notification;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;
import jku.se.Utilities.NotificationManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
  *Controller for displaying admin notifications with action buttons.
 * Shows notifications with delete buttons and invoice management options.
 */
public class NotificationsController {

    @FXML
    private VBox notificationContainer;

    private static final Logger LOGGER = Logger.getLogger(NotificationsController.class.getName());

    /**
     * Initializes the controller and loads all notifications with action buttons.
     */
    @FXML
    public void initialize() {
        notificationContainer.getChildren().clear();

        List<Notification> notifications = NotificationManager.getInstance().getNotifications();

        if (notifications.isEmpty()) {
            addInfoMessage("No notifications found.");
        } else {
            for (Notification notification : notifications) {
                addNotificationToContainer(notification);
            }
        }
    }

    /**
     * Adds a notification to the container with action buttons.
     * @param notification The notification to add.
     */
    private void addNotificationToContainer(Notification notification) {
        HBox notificationBox = new HBox(10);
        notificationBox.setStyle("-fx-padding: 8; -fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Create label for notification text with black color
        Label messageLabel = new Label(notification.getFullMessage());
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(400);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

        // Create action buttons
        HBox buttonBox = new HBox(5);

        // Check if this is an invoice upload notification
        if (isInvoiceUploadNotification(notification.getMessage())) {
            // Add Accept and Decline buttons for invoice notifications
            Button acceptButton = new Button("✓ Accept");
            acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 80; -fx-min-height: 30;");
            acceptButton.setOnAction(e -> handleAcceptInvoice(notification));

            Button declineButton = new Button("✗ Decline");
            declineButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 80; -fx-min-height: 30;");
            declineButton.setOnAction(e -> handleDeclineInvoice(notification));

            buttonBox.getChildren().addAll(acceptButton, declineButton);
        }

        Button deleteButton = new Button("🗑");
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30; -fx-min-height: 30;");
        deleteButton.setOnAction(e -> deleteNotification(notification.getId(), notificationBox));

        buttonBox.getChildren().add(deleteButton);

        // Make the message label grow to fill available space
        HBox.setHgrow(messageLabel, javafx.scene.layout.Priority.ALWAYS);

        notificationBox.getChildren().addAll(messageLabel, buttonBox);
        notificationContainer.getChildren().add(notificationBox);
    }

    /**
     * Checks if a notification is about an invoice upload.
     * @param message The notification message.
     * @return true if it's an invoice upload notification.
     */
    private boolean isInvoiceUploadNotification(String message) {
        return message.contains("uploaded a new invoice") ||
                message.contains("submitted an invoice") ||
                message.contains("Invoice submitted successfully") ||
                message.contains("uploaded a new invoice from");
    }

    /**
     * Handles accepting an invoice from a notification.
     * @param notification The notification containing invoice information.
     */
    private void handleAcceptInvoice(Notification notification) {
        try {
            // Extract user email and date from notification message
            String userEmail = extractUserEmail(notification.getMessage());
            LocalDate invoiceDate = extractInvoiceDate(notification.getMessage());

            if (userEmail != null && invoiceDate != null) {
                // Find the invoice in the database
                List<Invoice> userInvoices = InvoiceRepository.getAllInvoicesUser(userEmail);
                Invoice targetInvoice = null;

                for (Invoice invoice : userInvoices) {
                    if (invoice.getDate().equals(invoiceDate) && invoice.getStatus() == Status.PROCESSING) {
                        targetInvoice = invoice;
                        break;
                    }
                }

                if (targetInvoice != null) {
                    // Update invoice status to APPROVED
                    targetInvoice.setStatus(Status.APPROVED);
                    InvoiceRepository.updateInvoiceStatus(targetInvoice);

                    // Create notification for the user
                    String formattedDate = targetInvoice.getDate().toString();
                    Notification userNotification = new Notification("Your Invoice from " + formattedDate + " was approved.");
                    NotificationManager.getInstance().addNotificationForUser(userEmail, userNotification);

                    // Remove the admin notification without marking as deleted
                    boolean removed = NotificationManager.getInstance().removeNotification(notification.getId(), false);
                    if (removed) {
                        // Refresh the UI
                        initialize();
                    }

                    LOGGER.info("Invoice accepted for user: " + userEmail + " on date: " + invoiceDate);
                } else {
                    LOGGER.warning("Invoice not found for user: " + userEmail + " on date: " + invoiceDate);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error accepting invoice: ", e);
        }
    }

    /**
     * Handles declining an invoice from a notification.
     * @param notification The notification containing invoice information.
     */
    private void handleDeclineInvoice(Notification notification) {
        try {
            // Extract user email and date from notification message
            String userEmail = extractUserEmail(notification.getMessage());
            LocalDate invoiceDate = extractInvoiceDate(notification.getMessage());

            if (userEmail != null && invoiceDate != null) {
                // Find the invoice in the database
                List<Invoice> userInvoices = InvoiceRepository.getAllInvoicesUser(userEmail);
                Invoice targetInvoice = null;

                for (Invoice invoice : userInvoices) {
                    if (invoice.getDate().equals(invoiceDate) && invoice.getStatus() == Status.PROCESSING) {
                        targetInvoice = invoice;
                        break;
                    }
                }

                if (targetInvoice != null) {
                    // Update invoice status to DECLINED
                    targetInvoice.setStatus(Status.DECLINED);
                    InvoiceRepository.updateInvoiceStatus(targetInvoice);

                    // Create notification for the user
                    String formattedDate = targetInvoice.getDate().toString();
                    Notification userNotification = new Notification("Your Invoice from " + formattedDate + " was declined.");
                    NotificationManager.getInstance().addNotificationForUser(userEmail, userNotification);

                    // Remove the admin notification without marking as deleted
                    boolean removed = NotificationManager.getInstance().removeNotification(notification.getId(), false);
                    if (removed) {
                        initialize();
                    }

                    LOGGER.info("Invoice declined for user: " + userEmail + " on date: " + invoiceDate);
                } else {
                    LOGGER.warning("Invoice not found for user: " + userEmail + " on date: " + invoiceDate);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error declining invoice: ", e);
        }
    }

    /**
     * Extracts user email from notification message.
     * @param message The notification message.
     * @return The user email or null if not found.
     */
    private String extractUserEmail(String message) {
        if (message.contains("User ")) {
            int start = message.indexOf("User ") + 5;
            int end = message.indexOf(" ", start);
            if (end == -1) end = message.length();
            String email = message.substring(start, end);

            if (email.contains("@")) {
                return email;
            }

            if (end < message.length()) {
                int nextSpace = message.indexOf(" ", end + 1);
                if (nextSpace != -1) {
                    String potentialEmail = message.substring(end + 1, nextSpace);
                    if (potentialEmail.contains("@")) {
                        return potentialEmail;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Extracts invoice date from notification message.
     * @param message The notification message.
     * @return The invoice date or null if not found.
     */
    private LocalDate extractInvoiceDate(String message) {
        try {
            // Look for date patterns in the message
            String dateStr = null;

            // Pattern 1: "from [date]"
            if (message.contains("from ")) {
                int start = message.indexOf("from ") + 5;
                int end = message.indexOf(" ", start);
                if (end == -1) end = message.length();
                dateStr = message.substring(start, end);
            }

            // Pattern 2: "Invoice [date]"
            if (dateStr == null && message.contains("Invoice ")) {
                int start = message.indexOf("Invoice ") + 8;
                int end = message.indexOf(" ", start);
                if (end == -1) end = message.length();
                dateStr = message.substring(start, end);
            }

            if (dateStr != null) {
                // Try different date formats
                DateTimeFormatter[] formatters = {
                        DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                };

                for (DateTimeFormatter formatter : formatters) {
                    try {
                        return LocalDate.parse(dateStr, formatter);
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Could not extract date from message: " + message);
        }
        return null;
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
     * @param notificationBox The UI element to remove (can be null).
     */
    private void deleteNotification(String notificationId, HBox notificationBox) {
        boolean removed = NotificationManager.getInstance().removeNotification(notificationId, true);
        if (removed) {
            if (notificationBox != null) {
                notificationContainer.getChildren().remove(notificationBox);
            }
            LOGGER.info("Notification deleted: " + notificationId);
        } else {
            LOGGER.warning("Failed to delete notification: " + notificationId);
        }
    }

    /**
     * Closes the notification window.
     * @param event the action event that triggered the close
     */
    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}

