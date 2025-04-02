package jku.se.repository;

import jku.se.User;
import jku.se.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {//#18 Magda

    //new user insert
    private static final String INSERT_USER_SQL = "INSERT INTO userlunchify (email, name, password, isadministrator) VALUES (?, ?, ?, ?)";

    //add a new user in database
    public static void addUser(User user) {
        // insert user to userlunchify
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(INSERT_USER_SQL)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getPassword());
            stmt.setBoolean(4, user.isAdministrator());

            stmt.executeUpdate();
            System.out.println("User successfully added: " + user.getEmail());
        } catch (SQLException e) {
            System.err.println("Error when saving the user: " + e.getMessage());
        }
    }

    //filter for User/Admin - delete user
    private static final String GET_ALL_ADMINS_SQL = "SELECT email FROM userlunchify WHERE isadministrator = true";
    private static final String GET_ALL_USERS_SQL = "SELECT email FROM userlunchify WHERE isadministrator = false";


    //choicebox delete user email - difference between user and admin
    public static List<String> getAllAdminEmails() {
        return getEmails(GET_ALL_ADMINS_SQL);
    }

    public static List<String> getAllUserEmails() {
        return getEmails(GET_ALL_USERS_SQL);
    }

    private static List<String> getEmails(String query) {
        List<String> emails = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                emails.add(rs.getString("email"));
            }
        } catch (Exception e) {
            System.err.println("Error when retrieving emails: " + e.getMessage());
        }
        return emails;
    }

    //delete from database
    private static final String DELETE_USER_SQL = "DELETE FROM userlunchify WHERE email = ?";

    public static boolean deleteUser(String email) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(DELETE_USER_SQL)) {

            stmt.setString(1, email);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0; //delete one row so successfull
        } catch (SQLException e) {
            System.err.println("Error when deleting the user: " + e.getMessage());
            return false;
        }
    }

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
            System.err.println("Login error: " + e.getMessage());
        }
        return null;
    }
}
