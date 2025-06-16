package jku.se.Controller;

import javafx.scene.text.Text;
import jku.se.Utilities.ExportUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract base controller class for exporting statistical data in various formats (PDF, CSV, JSON).
 * Provides common export functionality and standardized feedback messages via JavaFX {@code Text}.
 */
public abstract class BaseStatisticController {

    /**
     * Exports a single data map to either PDF or CSV format.
     *
     * @param statusText the Text element used to display success or error messages
     * @param fileName the base name of the exported file
     * @param data the data to export (e.g., key-value pairs)
     * @param title the title to use in the export file (used in PDF)
     * @param format the export format ("PDF" or "CSV")
     */
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
            showSuccess(statusText, fileName + "." + format.toLowerCase() + " exported!");
        } catch (Exception e) {
            showError(statusText, "Export failed: " + e.getMessage());
        }
    }

    /**
     * Exports user reimbursement data to JSON format with additional metadata (month, total).
     *
     * @param statusText the Text element used to display messages
     * @param fileName the name of the exported file (without extension)
     * @param userDetails the reimbursement data per user
     * @param total the total reimbursement value
     */
    protected void exportReimbursementJson(Text statusText, String fileName,
                                           Map<String, Object> userDetails, double total) {
        try {
            Map<String, Object> exportData = new LinkedHashMap<>();
            exportData.put("month", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
            exportData.put("users", userDetails);
            exportData.put("total_reimbursement", total);

            ExportUtils.exportToJson(exportData, fileName);
            showSuccess(statusText, fileName + ".json exported!");
        } catch (Exception e) {
            showError(statusText, "JSON export failed: " + e.getMessage());
        }
    }

    /**
     * Exports a generic data map to the specified format (JSON, PDF, or CSV).
     *
     * @param data the data to export
     * @param fileNamePrefix the prefix for the exported file name
     * @param format the export format ("JSON", "PDF", or "CSV")
     * @param statusText the Text element to display result messages
     */
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

            showSuccess(statusText, fileName + "." + format.toLowerCase() + " successfully exported!");
        } catch (Exception e) {
            showError(statusText, "Export failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates the {@code statusText} with a success message in green.
     *
     * @param statusText the Text element to update
     * @param message the message to display
     */
    protected void showSuccess(Text statusText, String message) {
        statusText.setStyle("-fx-fill: green;");
        statusText.setText(message);
    }

    /**
     * Updates the {@code statusText} with an error message in red.
     *
     * @param statusText the Text element to update
     * @param message the message to display
     */
    protected void showError(Text statusText, String message) {
        statusText.setStyle("-fx-fill: red;");
        statusText.setText(message);
    }
}
