package jku.se.repository;

import jku.se.User;
import jku.se.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Repository class for managing user-related database operations.
 * Provides methods to add, delete, and retrieve users and user data.
 */
public class UserRepository {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    //new user insert
    private static final String INSERT_USER_SQL = "INSERT INTO userlunchify (email, name, password, isadministrator) VALUES (?, ?, ?, ?)";

    /**
     * Adds a new user to the database.
     * @param user The user to be added.
     */
    public static void addUser(User user) {
        // insert user to userlunchify
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(INSERT_USER_SQL)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getPassword());
            stmt.setBoolean(4, user.isAdministrator());

            stmt.executeUpdate();
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("User successfully added: " + user.getEmail());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when saving the user: ", e);
        }
    }

    //filter for User/Admin - delete user
    private static final String GET_ALL_ADMINS_SQL = "SELECT email FROM userlunchify WHERE isadministrator = true";
    private static final String GET_ALL_USERS_SQL = "SELECT email FROM userlunchify WHERE isadministrator = false";
    private static final String GET_ALL_USERS_WITHOUT_LOGGED_ADMIN = "SELECT email FROM userlunchify WHERE NOT IN user_email =?";

    /**
     * Retrieves email addresses of all admin users.
     * @return List of admin email addresses.
     */
    public static List<String> getAllAdminEmails() {
        return getEmails(GET_ALL_ADMINS_SQL);
    }

    /**
     * Retrieves email addresses of all non-admin users.
     * @return List of user email addresses.
     */
    public static List<String> getAllUserEmails() {
        return getEmails(GET_ALL_USERS_SQL);
    }

    /**
     * Retrieves email addresses of all users except the currently logged-in admin.
     * @param eMail Email of the logged-in admin.
     * @return List of user email addresses.
     */
    public static List<String> getAllUsersWithoutLoggedAdmin(String eMail) {
        return getEmails(GET_ALL_USERS_WITHOUT_LOGGED_ADMIN);
    }

    /**
     * Executes a query to retrieve email addresses from the database.
     * @param query SQL query to execute.
     * @return List of email addresses.
     */
    private static List<String> getEmails(String query) {
        List<String> emails = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                emails.add(rs.getString("email"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when retrieving emails: ", e);
        }
        return emails;
    }

    //delete from database
    private static final String DELETE_USER_SQL = "DELETE FROM userlunchify WHERE email = ?";

    /**
     * Deletes a user by their email address.
     * @param email Email address of the user to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public static boolean deleteUser(String email) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(DELETE_USER_SQL)) {

            stmt.setString(1, email);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0; //delete one row so successfull
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when deleting the user: ", e);
            return false;
        }
    }

    /**
     * Finds a user by email and password for login verification.
     * @param email Email address.
     * @param password Password.
     * @return The matching User object, or null if not found.
     */
    public static User findByEmailAndPassword(String email, String password) {
        String query = "SELECT * FROM userlunchify WHERE email = ? AND password = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("isadministrator")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Login error: ", e);
        }
        return null;
    }

    /**
     * Retrieves a user by their email address.
     * @param email Email address.
     * @return The matching User object, or null if not found.
     */
    public static User getByEmail(String email) {
        String sql = "SELECT * FROM userlunchify WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isadministrator");

                User user = new User(name, email, password, isAdmin);
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
