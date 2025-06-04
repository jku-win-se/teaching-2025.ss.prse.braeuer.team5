package jku.se;

import jku.se.Utilities.NotificationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Notification {

    private static final Logger LOGGER = Logger.getLogger(Notification.class.getName());

    public static final List<String> MESSAGES_SENT = new ArrayList<>();
    private String message;
    private LocalDateTime timestamp;
    private boolean isForAdmin;
    private String targetUserEmail;

    public Notification(String message, boolean isForAdmin, String targetUserEmail) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isForAdmin = isForAdmin;
        this.targetUserEmail = targetUserEmail;
    }

    public Notification() {
        this.message = "";
        this.timestamp = LocalDateTime.now();
        this.isForAdmin = false;
        this.targetUserEmail = null;
    }



    public void sendInApp(User user, String message){
        String formatted = "User " + user.getEmail() + ": " + message;
        NotificationManager.getInstance().addNotification(new Notification(formatted));
        MESSAGES_SENT.add(formatted);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("In-App-Notifications for " + user.getName() + ": " + message);
        }
    }

    public static void clearMessages() {
        MESSAGES_SENT.clear();
    }

    public Notification(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

}
