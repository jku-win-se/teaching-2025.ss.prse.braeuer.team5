package jku.se.export;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class CsvExporter {

    private final String delimiter;

    public CsvExporter() {
        this.delimiter = ";"; // Standard Semicolon for AT/DE Excel
    }

    public CsvExporter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return delimiter;
    }

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

    public String escapeCsv(String value) {
        if (value == null) return "";
        boolean mustQuote = value.contains(delimiter) || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        if (mustQuote) {
            return "\"" + escaped + "\"";
        } else {
            return escaped;
        }
    }

    public String generateFileName(String baseName, String extension) {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return System.getProperty("user.home") + File.separator + "Downloads" + File.separator + baseName + "_" + timestamp + "." + extension;
    }
}
