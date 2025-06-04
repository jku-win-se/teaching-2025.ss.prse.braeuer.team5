package jku.se.Controller;

import javafx.scene.text.Text;
import jku.se.Utilities.ExportUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseStatisticController {

    // Export data to PDF or CSV formats, update statusText with success/error message
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

    // Export reimbursement data to JSON format with month and total reimbursement
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

    // Export data to JSON, PDF, or CSV based on format parameter
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

    // Show success message in green text
    protected void showSuccess(Text statusText, String message) {
        statusText.setStyle("-fx-fill: green;");
        statusText.setText(message);
    }

    // Show error message in red text
    protected void showError(Text statusText, String message) {
        statusText.setStyle("-fx-fill: red;");
        statusText.setText(message);
    }
}
