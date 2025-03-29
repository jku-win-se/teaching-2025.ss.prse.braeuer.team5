package jku.se;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    public static String USER = "postgres.dljjtuynbgxgmhkcdypu";
    public static String PWD = "LunchifyTeam5!";
    private static final String URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";

    public static void execute() {
        try (Connection con = DriverManager.getConnection(URL, USER, PWD)) {
            System.out.println("Verbindung zu Supabase erfolgreich!");
        } catch (SQLException e) {
            System.err.println("Fehler bei der Verbindung: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Fehlercode: " + e.getErrorCode());
        }
    }

    public static void main(String[] args) {
        execute(); // Testet die Verbindung
    }
}
