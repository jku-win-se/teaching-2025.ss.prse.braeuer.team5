package jku.se;

import jku.se.Utilities.NotificationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents an in-app notification that can be sent to users or administrators.
 * Supports message tracking and timestamping.
 */
public class Notification {

    private static final Logger LOGGER = Logger.getLogger(Notification.class.getName());

    public static final List<String> MESSAGES_SENT = new ArrayList<>();
    private String id;
    private String message;
    private LocalDateTime timestamp;
    private boolean isForAdmin;
    private String targetUserEmail;

    /**
     * Constructs a new Notification with full attributes.
     *
     * @param message          The notification message.
     * @param isForAdmin       Whether the notification is intended for an admin.
     * @param targetUserEmail  The email of the target user (can be null).
     */
    public Notification(String message, boolean isForAdmin, String targetUserEmail) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isForAdmin = isForAdmin;
        this.targetUserEmail = targetUserEmail;
    }

    /**
     * Default constructor. Creates an empty message with current timestamp and no target.
     */
    public Notification() {
        this.id = UUID.randomUUID().toString();
        this.message = "";
        this.timestamp = LocalDateTime.now();
        this.isForAdmin = false;
        this.targetUserEmail = null;
    }

    /**
     * Sends an in-app notification for a given user.
     *
     * @param user    The target user.
     * @param message The message to send.
     */
    public void sendInApp(User user, String message){
        String formatted = "User " + user.getEmail() + ": " + message;
        NotificationManager.getInstance().addNotification(new Notification(formatted));
        MESSAGES_SENT.add(formatted);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("In-App-Notifications for " + user.getName() + ": " + message);
        }
    }

    /**
     * Clears the list of all sent messages (mainly for tests/debugging).
     */
    public static void clearMessages() {
        MESSAGES_SENT.clear();
    }

    /**
     * Constructs a simple Notification with a given message and current timestamp.
     *
     * @param message The notification message.
     */
    public Notification(String message) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }



    /**
     * Returns the message content of the notification.
     *
     * @return The notification message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the unique ID of the notification.
     *
     * @return The notification ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the timestamp when the notification was created.
     *
     * @return The timestamp.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the formatted timestamp as a string.
     *
     * @return Formatted timestamp string.
     */
    public String getFormattedTimestamp() {
        return timestamp.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    /**
    * Returns the full notification text including timestamp.
    *
    * @return Full notification text.
    */
    public String getFullMessage() {
        return getFormattedTimestamp() + " - " + message;
    }

    /**
     * Returns the notification message without timestamp (for user notifications).
     *
     * @return Notification message without timestamp.
     */
    public String getUserMessage() {
        return message;
    }
}
