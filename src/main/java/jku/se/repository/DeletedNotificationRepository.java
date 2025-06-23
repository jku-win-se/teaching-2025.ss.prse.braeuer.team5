package jku.se.repository;

import jku.se.DatabaseConnection;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository for managing deleted notifications in the database.
 * Provides methods to store and retrieve deleted notification messages.
 */
public class DeletedNotificationRepository {
    private static final Logger LOGGER = Logger.getLogger(DeletedNotificationRepository.class.getName());

    private static final String INSERT_DELETED_NOTIFICATION =
            "INSERT INTO deleted_notifications (message, user_email) VALUES (?, ?)";

    private static final String GET_DELETED_NOTIFICATIONS =
            "SELECT message FROM deleted_notifications WHERE user_email = ?";

    private static final String DELETE_ALL_FOR_USER =
            "DELETE FROM deleted_notifications WHERE user_email = ?";

    private static final String DELETE_ALL =
            "DELETE FROM deleted_notifications";


    /**
     * Adds a deleted notification message to the database.
     * @param message The notification message that was deleted.
     * @param userEmail The email of the user who deleted the notification.
     */
    public static void addDeletedNotification(String message, String userEmail) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(INSERT_DELETED_NOTIFICATION)) {
            stmt.setString(1, message);
            stmt.setString(2, userEmail);
            stmt.executeUpdate();
            LOGGER.info("Deleted notification saved to database: " + message);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving deleted notification: ", e);
        }
    }

    /**
     * Retrieves all deleted notification messages for a specific user.
     * @param userEmail The email of the user.
     * @return Set of deleted notification messages.
     */
    public static Set<String> getDeletedNotifications(String userEmail) {
        Set<String> deletedMessages = new HashSet<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(GET_DELETED_NOTIFICATIONS)) {
            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    deletedMessages.add(rs.getString("message"));
                }
            }
            LOGGER.info("Loaded " + deletedMessages.size() + " deleted notifications for user: " + userEmail);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading deleted notifications: ", e);
        }
        return deletedMessages;
    }

    /**
     * Clears all deleted notifications for a specific user.
     * @param userEmail The email of the user.
     */
    public static void clearDeletedNotificationsForUser(String userEmail) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(DELETE_ALL_FOR_USER)) {
            stmt.setString(1, userEmail);
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Cleared " + rowsAffected + " deleted notifications for user: " + userEmail);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing deleted notifications for user: ", e);
        }
    }

    /**
     * Clears all deleted notifications for all users.
     */
    public static void clearAllDeletedNotifications() {
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            int rowsAffected = stmt.executeUpdate(DELETE_ALL);
            LOGGER.info("Cleared all " + rowsAffected + " deleted notifications");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing all deleted notifications: ", e);
        }
    }
}
