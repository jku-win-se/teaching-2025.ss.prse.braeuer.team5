package jku.se;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Notification {

    private static final Logger LOGGER = Logger.getLogger(Notification.class.getName());

    public static List<String> messagesSent = new ArrayList<>();

    public void sendInApp(User user, String message){
        String formatted = "[In-App] " + message;
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
}
