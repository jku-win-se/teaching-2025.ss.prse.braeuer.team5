package jku.se.repository;

import jku.se.Notification;
import jku.se.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminNotificationRepository {

    public static void addAdminNotification(Notification notification) {
        String sql = "INSERT INTO admin_notifications (id, message, timestamp) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(notification.getId()));
            stmt.setString(2, notification.getMessage());
            stmt.setTimestamp(3, Timestamp.valueOf(notification.getTimestamp()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static List<Notification> getAllAdminNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM admin_notifications ORDER BY timestamp DESC";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Notification n = new Notification(
                        rs.getString("message")
                );
                // Set ID and timestamp manually
                n.setId(rs.getObject("id").toString());
                n.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                notifications.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public static void deleteAdminNotification(String id) {
        String sql = "DELETE FROM admin_notifications WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
