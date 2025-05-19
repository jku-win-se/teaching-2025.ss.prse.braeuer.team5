package jku.se;

import jku.se.Utilities.NotificationManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Notification {

    private static final Logger LOGGER = Logger.getLogger(Notification.class.getName());

    public static List<String> messagesSent = new ArrayList<>();
    private String message;
    private LocalDateTime timestamp;

    public Notification() {
        this.message = "";
        this.timestamp = LocalDateTime.now();
    }


    public void sendInApp(User user, String message){
        String formatted = "User " + user.getEmail() + ": " + message;
        NotificationManager.getInstance().addNotification(new Notification(formatted));
        messagesSent.add(formatted);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("In-App-Benachrichtigung für " + user.getName() + ": " + message);
        }
    }

    public void sendEmail(User user, String message){
        String formatted = "[Email] " + message;
        messagesSent.add(formatted);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("E-Mail an " + user.getEmail() + ": " + message);
        }
    }

    public static void clearMessages() {
        messagesSent.clear();
    }

    public Notification(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public String getTimestampFormatted() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
