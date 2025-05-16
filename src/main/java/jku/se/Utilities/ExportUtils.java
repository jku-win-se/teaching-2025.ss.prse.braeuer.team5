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
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

public class ExportUtils {
    private static final Logger logger = Logger.getLogger(ExportUtils.class.getName());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static boolean exportToJson(Map<String, ?> data, String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(data);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            return true;
        }
    }

    public static boolean exportToCsv(Map<String, ?> data, String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "csv");
        StringBuilder csv = new StringBuilder("Key,Value\n");

        for (Map.Entry<String, ?> entry : data.entrySet()) {
            csv.append(escapeCsv(entry.getKey())).append(",")
                    .append(escapeCsv(entry.getValue().toString())).append("\n");
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(csv.toString());
            return true;
        }
    }

    public static boolean exportToPdf(Map<String, ?> data, String title, String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                // Header
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText(title);
                contentStream.endText();

                // Content
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);

                int yPosition = 700;
                for (Map.Entry<String, ?> entry : data.entrySet()) {
                    contentStream.showText(entry.getKey() + ": " + entry.getValue());
                    yPosition -= 20;
                    contentStream.newLineAtOffset(0, -20);
                }
                contentStream.endText();
            } finally {
                contentStream.close();
            }

            document.save(new File(fileName));
            return true;
        }
    }

    private static String generateFileName(String baseName, String extension) {
        String timestamp = DATE_FORMAT.format(new Date());
        return Paths.get(System.getProperty("user.home"), "Downloads",
                String.format("%s_%s.%s", baseName, timestamp, extension)).toString();
    }

    private static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}