package jku.se.Controller;

import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.util.Duration;
import jku.se.Utilities.ExportUtils;
import javafx.animation.PauseTransition;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public abstract class BaseStatisticController {
    protected PauseTransition statusTimer;

    // Gemeinsame Export-Methode
    protected void exportData(Map<String, ?> data, String fileNamePrefix, String format, Text statusText) {
        try {
            String fileName = fileNamePrefix + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            switch (format) {
                case "JSON" -> ExportUtils.exportToJson(data, fileName);
                case "PDF" -> ExportUtils.exportToPdf(data, fileNamePrefix + " Report", fileName);
                case "CSV" -> ExportUtils.exportToCsv(data, fileName);
            }
            showStatus(statusText, "Export erfolgreich: " + fileName, true);
        } catch (Exception e) {
            showStatus(statusText, "Export fehlgeschlagen: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    // Status-Anzeige
    protected void showStatus(Text statusText, String message, boolean isSuccess) {
        if (statusText != null) {
            statusText.setStyle("-fx-fill: " + (isSuccess ? "green" : "red") + ";");
            statusText.setText(message);
            if (statusTimer == null) {
                statusTimer = new PauseTransition(Duration.seconds(3));
                statusTimer.setOnFinished(e -> statusText.setText(""));
            }
            statusTimer.playFromStart();
        }
    }

    // Alert-Hilfsmethode
    protected void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}