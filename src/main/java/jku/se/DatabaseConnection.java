package jku.se;

import jku.se.repository.InvoiceRepository;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String USER = "postgres.dljjtuynbgxgmhkcdypu";
    private static final String PWD = "LunchifyTeam5!";
    private static final String URL_JDBC = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    public static final String BUCKET = "invoicefiles";
    public static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRsamp0dXluYmd4Z21oa2NkeXB1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDI3OTg4MjcsImV4cCI6MjA1ODM3NDgyN30.OTZ-XN4JYNbzgfYfHWN_ZbyrRcnW1uIzJIFK1MJLXrI";
    public static final String URL_SUPABASE = "https://dljjtuynbgxgmhkcdypu.supabase.co";

    // Establish database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL_JDBC, USER, PWD);
    }

    // Verbindung zu Supabase und Upload
    public static String uploadFileToBucket(File imageFile) throws IOException {

        try {
            String uniqueFileName = System.currentTimeMillis() + "_" + imageFile.getName();
            String uploadUrl = DatabaseConnection.URL_SUPABASE + "/storage/v1/object/" + DatabaseConnection.BUCKET + "/" + uniqueFileName;
            String contentType = Files.probeContentType(imageFile.toPath());
            if (contentType == null) contentType = "application/octet-stream";

            HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Bearer " + DatabaseConnection.API_KEY);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream();
                 FileInputStream fis = new FileInputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                return getPublicFileUrl(uniqueFileName);
            } else {
                System.out.println("Upload fehlgeschlagen: HTTP " + responseCode);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Fehlerdetails: " + response);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPublicFileUrl(String filePath){
        return URL_SUPABASE + "/storage/v1/object/" + DatabaseConnection.BUCKET + "/" + filePath;
    }


    // Check database connection (test connection)
    public static void main(String[] args) {
        try (Connection con = getConnection()) {
            System.out.println("Verbindung zu Supabase erfolgreich!");
        } catch (SQLException e) {
            System.err.println("Fehler bei der Verbindung: " + e.getMessage());
        }
    }
}
