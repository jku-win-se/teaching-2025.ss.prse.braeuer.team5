package jku.se;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    public static String USER = "postgres.dljjtuynbgxgmhkcdypu";
    public static String PWD = "LunchifyTeam5!";
    private static final String URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";

    // Use this for queries
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PWD);
    }

    //Check database connection
    public static void main(String[] args) {
        try (Connection con = getConnection()) {
            System.out.println("Verbindung zu Supabase erfolgreich!");
        } catch (SQLException e) {
            System.err.println("Fehler bei der Verbindung: " + e.getMessage());
        }
    }
}
