package jku.se.Utilities;

import jku.se.Notification;
import jku.se.User;
import jku.se.repository.DeletedNotificationRepository;
import jku.se.repository.AdminNotificationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NotificationManager {
    private static final Logger LOGGER = Logger.getLogger(NotificationManager.class.getName());
    private static NotificationManager instance;

    private final List<Notification> globalNotifications;
    private final Map<String, List<Notification>> userNotifications;
    private final Map<String, Set<String>> deletedNotificationMessages;

    private NotificationManager() {
        globalNotifications = new ArrayList<>(AdminNotificationRepository.getAllAdminNotifications());
        userNotifications = new HashMap<>();
        deletedNotificationMessages = new HashMap<>();
    }

    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    /**
     * Loads deleted notifications from the database for a specific user.
     * @param userEmail
     */
    public void loadDeletedNotificationsForUser(String userEmail) {
        if (!deletedNotificationMessages.containsKey(userEmail)) {
            Set<String> deletedMessages = DeletedNotificationRepository.getDeletedNotifications(userEmail);
            deletedNotificationMessages.put(userEmail, deletedMessages);
            LOGGER.info("Loaded " + deletedMessages.size() + " deleted notifications for user: " + userEmail);
        }
    }

    public void addNotification(Notification notification) {
        globalNotifications.add(notification);
        AdminNotificationRepository.addAdminNotification(notification);
    }
    /**
     * Adds a notification for a specific user.
     * @param userEmail The email of the user.
     * @param notification The notification to add.
     */
    public void addNotificationForUser(String userEmail, Notification notification) {
        userNotifications.computeIfAbsent(userEmail, k -> new ArrayList<>()).add(notification);
        LOGGER.info("Notification added for user: " + userEmail);
    }

    public List<Notification> getNotifications() {
        return new ArrayList<>(globalNotifications); // Defensive copy
    }

    /**
     * Gets all notifications for a specific user (global + user-specific).
     * @param userEmail The email of the user.
     * @return List of notifications for the user.
     */
    public List<Notification> getNotificationsForUser(String userEmail) {
        List<Notification> allNotifications = new ArrayList<>(globalNotifications);
        List<Notification> userSpecificNotifications = userNotifications.get(userEmail);
        if (userSpecificNotifications != null) {
            allNotifications.addAll(userSpecificNotifications);
        }
        return allNotifications;
    }

    /**
     * Gets only user-specific notifications for a specific user (excludes global admin notifications).
     * @param userEmail The email of the user.
     * @return List of user-specific notifications for the user.
     */
    public List<Notification> getUserSpecificNotifications(String userEmail) {
        List<Notification> userSpecificNotifications = userNotifications.get(userEmail);
        if (userSpecificNotifications != null) {
            return new ArrayList<>(userSpecificNotifications); // Defensive copy
        }
        return new ArrayList<>();
    }

    /**
     * Checks if a notification message has been deleted by the user.
     * @param userEmail The email of the user.
     * @param message The message content to check.
     * @return true if the message has been deleted, false otherwise.
     */
    public boolean isNotificationDeleted(String userEmail, String message) {
        loadDeletedNotificationsForUser(userEmail);
        Set<String> userDeletedMessages = deletedNotificationMessages.get(userEmail);
        return userDeletedMessages != null && userDeletedMessages.contains(message);
    }

    /**
     * Marks a notification message as deleted.
     * @param userEmail The email of the user.
     * @param message The message content to mark as deleted as saves it to the database.
     */
    public void markNotificationAsDeleted(String userEmail, String message) {
        deletedNotificationMessages.computeIfAbsent(userEmail, k -> new HashSet<>()).add(message);

        // Save to database
        DeletedNotificationRepository.addDeletedNotification(message, userEmail);

        LOGGER.info("Notification message marked as deleted and saved to database: " + message);
    }

    /**
     * Removes a notification by its ID.
     * @param notificationId The ID of the notification to remove.
     * @return true if the notification was found and removed, false otherwise.
     */
    public boolean removeNotification(String notificationId) {
        return removeNotification(notificationId, true);
    }

    /**
     * Removes a notification by its ID from global notifications.
     * @param notificationId The ID of the notification to remove.
     * @param markAsDeleted Whether to mark the notification as deleted for users.
     * @return true if the notification was found and removed, false otherwise.
     */
    public boolean removeNotification(String notificationId, boolean markAsDeleted) {
        for (int i = 0; i < globalNotifications.size(); i++) {
            if (globalNotifications.get(i).getId().equals(notificationId)) {
                Notification removed = globalNotifications.remove(i);
                AdminNotificationRepository.deleteAdminNotification(notificationId);
                if (markAsDeleted) {
                    // Mark the message as deleted (for all users since it's global)
                    for (String userEmail : userNotifications.keySet()) {
                        markNotificationAsDeleted(userEmail, removed.getMessage());
                    }
                }
                LOGGER.info("Notification removed: " + notificationId);
                return true;
            }
        }
        LOGGER.warning("Notification not found for removal: " + notificationId);
        return false;
    }

    /**
     * Removes a notification by its ID from user-specific notifications.
     * @param userEmail The email of the user.
     * @param notificationId The ID of the notification to remove.
     * @return true if the notification was found and removed, false otherwise.
     */
    public boolean removeUserNotification(String userEmail, String notificationId) {
        List<Notification> userSpecificNotifications = userNotifications.get(userEmail);
        if (userSpecificNotifications != null) {
            for (int i = 0; i < userSpecificNotifications.size(); i++) {
                if (userSpecificNotifications.get(i).getId().equals(notificationId)) {
                    Notification removed = userSpecificNotifications.remove(i);
                    markNotificationAsDeleted(userEmail, removed.getMessage());
                    LOGGER.info("User notification removed: " + notificationId + " for user: " + userEmail);
                    return true;
                }
            }
        }
        LOGGER.warning("User notification not found for removal: " + notificationId + " for user: " + userEmail);
        return false;
    }

    /**
     * Removes a notification by its ID from either global or user-specific notifications.
     * @param userEmail The email of the user.
     * @param notificationId The ID of the notification to remove.
     * @return true if the notification was found and removed, false otherwise.
     */
    public boolean removeNotificationForUser(String userEmail, String notificationId) {
        // First try to remove from user-specific notifications
        if (removeUserNotification(userEmail, notificationId)) {
            return true;
        }
        // Then try to remove from global notifications
        return removeNotification(notificationId);
    }

    /**
     * Clears all notifications.
     */
    public void clearAllNotifications() {
        globalNotifications.clear();
        userNotifications.clear();
        deletedNotificationMessages.clear();
        DeletedNotificationRepository.clearAllDeletedNotifications();
        LOGGER.info("All notifications cleared");
    }

    /**
     * Clears all notifications for a specific user.
     * @param userEmail The email of the user.
     */
    public void clearUserNotifications(String userEmail) {
        userNotifications.remove(userEmail);
        deletedNotificationMessages.remove(userEmail);
        DeletedNotificationRepository.clearDeletedNotificationsForUser(userEmail);
        LOGGER.info("All notifications cleared for user: " + userEmail);
    }

    /**
     * Clears the deleted notifications list.
     * @param userEmail
     */
    public void clearDeletedNotificationsForUser(String userEmail) {
        deletedNotificationMessages.remove(userEmail);
        // Also clear from database
        DeletedNotificationRepository.clearDeletedNotificationsForUser(userEmail);
        LOGGER.info("Deleted notifications cleared for user: " + userEmail);
    }
    public void clearAllDeletedNotifications() {
        deletedNotificationMessages.clear();
        DeletedNotificationRepository.clearAllDeletedNotifications();
        LOGGER.info("All deleted notifications cleared");
    }
}
