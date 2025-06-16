package jku.se.export;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Utility class for exporting tabular data to CSV format.
 */
public class CsvExporter {

    private final String delimiter;

    /**
     * Default constructor using semicolon as delimiter (common in AT/DE Excel).
     */
    public CsvExporter() {
        this.delimiter = ";"; // Standard Semicolon for AT/DE Excel
    }

    /**
     * Constructor allowing custom delimiter.
     *
     * @param delimiter Delimiter character used to separate values.
     */
    public CsvExporter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Exports the provided data rows to a CSV file.
     *
     * @param rows List of rows, where each row is a map of column name to value.
     * @param baseFileName The base name for the output file (without extension).
     * @throws IOException If an I/O error occurs.
     */
    public void export(List<Map<String, String>> rows, String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "csv");

        try (
                FileOutputStream fos = new FileOutputStream(new File(fileName));
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                BufferedWriter writer = new BufferedWriter(osw)
        ) {
            // UTF-8 BOM write for Excel
            fos.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});

            if (rows.isEmpty()) {
                return;
            }

            Map<String, String> firstRow = rows.get(0);
            String headerLine = String.join(delimiter, firstRow.keySet());
            writer.write(headerLine);
            writer.newLine();

            for (Map<String, String> row : rows) {
                StringBuilder line = new StringBuilder();
                for (String key : firstRow.keySet()) {
                    String value = row.getOrDefault(key, "");
                    line.append(escapeCsv(value)).append(delimiter);
                }
                if (line.length() > 0) {
                    line.deleteCharAt(line.length() - 1);
                }
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }

    /**
     * Escapes CSV values by quoting and handling embedded quotes or special characters.
     *
     * @param value The original cell value.
     * @return The escaped CSV-compatible value.
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        boolean mustQuote = value.contains(delimiter) || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        if (mustQuote) {
            return "\"" + escaped + "\"";
        } else {
            return escaped;
        }
    }

    /**
     * Generates a full file path in the Downloads folder with timestamp and given extension.
     *
     * @param baseName  Base filename (without timestamp or extension).
     * @param extension File extension (e.g., "csv").
     * @return Full file path as a string.
     */
    private String generateFileName(String baseName, String extension) {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return System.getProperty("user.home") + File.separator + "Downloads" + File.separator + baseName + "_" + timestamp + "." + extension;
    }
}
