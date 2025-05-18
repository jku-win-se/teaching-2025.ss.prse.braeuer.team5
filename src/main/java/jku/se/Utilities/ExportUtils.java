package jku.se.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportUtils {
    private static final Logger LOGGER = Logger.getLogger(ExportUtils.class.getName());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final int PDF_FONT_SIZE = 12;
    private static final int PDF_TITLE_SIZE = 16;
    private static final int PDF_MARGIN = 50;
    private static final int PDF_LINE_SPACING = 20;

    // Generate file path in user's Downloads folder with given basename and extension
    private static String generatePath(String baseName, String extension) {
        return Paths.get(System.getProperty("user.home"), "Downloads", baseName + "." + extension).toString();
    }

    /**
     * Export data map to a JSON file with pretty printing
     */
    public static boolean exportToJson(Map<String, ?> data, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(generatePath(fileName, "json"))) {
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
            return true;
        }
    }

    /**
     * Export data map to a CSV file with escaped values
     */
    public static boolean exportToCsv(Map<String, ?> data, String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "csv");
        StringBuilder csv = new StringBuilder("Key,Value\n");

        for (Map.Entry<String, ?> entry : data.entrySet()) {
            csv.append(escapeCsv(entry.getKey()))
                    .append(",")
                    .append(escapeCsv(String.valueOf(entry.getValue())))
                    .append("\n");
        }
        return writeToFile(fileName, csv.toString());
    }

    /**
     * Export data map to a formatted PDF file with title and multiple pages if needed
     */
    public static boolean exportToPdf(Map<String, ?> data, String title, String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                // Draw header title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, PDF_TITLE_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(PDF_MARGIN, 800);
                contentStream.showText(title);
                contentStream.endText();

                // Draw content lines
                contentStream.setFont(PDType1Font.HELVETICA, PDF_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(PDF_MARGIN, 750);

                int yPosition = 750;
                for (Map.Entry<String, ?> entry : data.entrySet()) {
                    if (yPosition < PDF_MARGIN) {
                        // Start a new page when margin is reached
                        contentStream.endText();
                        contentStream.close();

                        PDPage newPage = new PDPage(PDRectangle.A4);
                        document.addPage(newPage);
                        contentStream = new PDPageContentStream(document, newPage);

                        contentStream.setFont(PDType1Font.HELVETICA, PDF_FONT_SIZE);
                        contentStream.beginText();
                        yPosition = 750;
                        contentStream.newLineAtOffset(PDF_MARGIN, yPosition);
                    }

                    String line = String.format("%-30s: %s", entry.getKey(), entry.getValue());
                    contentStream.showText(line);
                    yPosition -= PDF_LINE_SPACING;
                    contentStream.newLineAtOffset(0, -PDF_LINE_SPACING);
                }
                contentStream.endText();
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            document.save(new File(fileName));
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "PDF export failed", e);
            throw e;
        }
    }

    // Generate filename with timestamp suffix
    private static String generateFileName(String baseName, String extension) {
        String timestamp = DATE_FORMAT.format(new Date());
        Path path = Paths.get(System.getProperty("user.home"), "Downloads",
                String.format("%s_%s.%s", baseName, timestamp, extension));
        return path.toString();
    }

    // Write given content string into file with given filename
    private static boolean writeToFile(String fileName, String content) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
            return true;
        }
    }

    // Escape CSV values that contain commas, quotes or newlines
    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
