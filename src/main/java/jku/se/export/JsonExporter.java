package jku.se.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Utility class for exporting data to a JSON file.
 * Uses Google's Gson library to format the JSON with pretty printing.
 */
public class JsonExporter {

    /**
     * Exports the provided data map to a JSON file.
     * The file is saved in the user's Downloads directory with a timestamped name.
     *
     * @param data Map containing the data to export.
     * @param baseFileName Base name for the output file (without extension or timestamp).
     * @throws IOException if writing to file fails.
     */
    public void export(Map<String, ?> data, String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "json");

        try (FileWriter writer = new FileWriter(new File(fileName))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        }
    }

    /**
     * Generates a filename with a timestamp and the specified extension.
     * The file will be placed in the user's Downloads directory.
     *
     * @param baseName Base name of the file.
     * @param extension File extension (e.g., "json").
     * @return Full path to the generated file.
     */
    private String generateFileName(String baseName, String extension) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return System.getProperty("user.home") + File.separator + "Downloads" + File.separator + baseName + "_" + timestamp + "." + extension;
    }
}
