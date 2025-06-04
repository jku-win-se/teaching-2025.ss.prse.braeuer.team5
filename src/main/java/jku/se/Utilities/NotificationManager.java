package jku.se.Utilities;

import jku.se.Notification;
import jku.se.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NotificationManager {
    private static final Logger LOGGER = Logger.getLogger(NotificationManager.class.getName());
    private static NotificationManager instance;

    private final List<Notification> notifications;

    private NotificationManager() {
        notifications = new ArrayList<>();
    }

    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications); // Defensive copy
    }


}
