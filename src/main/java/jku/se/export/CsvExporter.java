package jku.se.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class CsvExporter {

    private static final String EXPORT_FOLDER = System.getProperty("user.home") + File.separator + "Downloads";

    /**
     * Exportiert eine Liste von Datenzeilen (Karten von Spaltenname zu Wert) als CSV-Datei.
     * Die Kopfzeile wird aus dem ersten Eintrag generiert.
     */
    public void export(List<Map<String, String>> rows, String baseFileName) throws IOException {
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("Keine Daten zum Exportieren");
        }

        String fileName = generateFileName(baseFileName, "csv");

        try (FileWriter writer = new FileWriter(fileName)) {
            // Kopfzeile
            Map<String, String> firstRow = rows.get(0);
            String header = String.join(",", firstRow.keySet());
            writer.write(header + "\n");

            // Datenzeilen
            for (Map<String, String> row : rows) {
                StringBuilder line = new StringBuilder();
                for (String key : firstRow.keySet()) {
                    String value = row.getOrDefault(key, "");
                    line.append(escapeCsv(value)).append(",");
                }
                line.deleteCharAt(line.length() - 1); // letztes Komma entfernen
                writer.write(line.toString() + "\n");
            }
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String generateFileName(String baseName, String extension) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path path = Paths.get(EXPORT_FOLDER, baseName + "_" + timestamp + "." + extension);
        return path.toString();
    }
}
