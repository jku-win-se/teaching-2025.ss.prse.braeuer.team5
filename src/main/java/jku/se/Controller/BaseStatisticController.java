package jku.se.Controller;

import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import jku.se.Utilities.ExportUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseStatisticController {

    // Für PDF/CSV-Exporte
    protected void exportSingleFormat(Text statusText, String fileName,
                                      Map<String, ?> data, String title,
                                      String format) {
        try {
            switch (format) {
                case "PDF":
                    ExportUtils.exportToPdf(data, title, fileName);
                    break;
                case "CSV":
                    ExportUtils.exportToCsv(data, fileName);
                    break;
            }
            showSuccess(statusText, fileName + "." + format.toLowerCase() + " exportiert!");
        } catch (Exception e) {
            showError(statusText, "Export fehlgeschlagen: " + e.getMessage());
        }
    }

    // Nur für JSON-Export (Reimbursement)
    protected void exportReimbursementJson(Text statusText, String fileName,
                                           Map<String, Object> userDetails, double total) {
        try {
            Map<String, Object> exportData = new LinkedHashMap<>();
            exportData.put("month", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
            exportData.put("users", userDetails);
            exportData.put("total_reimbursement", total);

            ExportUtils.exportToJson(exportData, fileName);
            showSuccess(statusText, fileName + ".json exportiert!");
        } catch (Exception e) {
            showError(statusText, "JSON-Export fehlgeschlagen: " + e.getMessage());
        }
    }

    protected void exportData(Map<String, ?> data, String fileNamePrefix, String format, Text statusText) {
        try {
            String fileName = fileNamePrefix + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

            switch (format) {
                case "JSON":
                    ExportUtils.exportToJson(data, fileName);
                    break;
                case "PDF":
                    ExportUtils.exportToPdf(data, fileNamePrefix + " Report", fileName);
                    break;
                case "CSV":
                    ExportUtils.exportToCsv(data, fileName);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }

            showSuccess(statusText, fileName + "." + format.toLowerCase() + " erfolgreich exportiert!");
        } catch (Exception e) {
            showError(statusText, "Export fehlgeschlagen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hilfsmethoden
    protected void showSuccess(Text statusText, String message) {
        statusText.setStyle("-fx-fill: green;");
        statusText.setText(message);
    }

    protected void showError(Text statusText, String message) {
        statusText.setStyle("-fx-fill: red;");
        statusText.setText(message);
    }
}