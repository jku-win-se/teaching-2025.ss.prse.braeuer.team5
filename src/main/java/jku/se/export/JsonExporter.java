package jku.se.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class JsonExporter {

    private static final String EXPORT_FOLDER = System.getProperty("user.home") + File.separator + "Downloads";

    public void export(Map<String, ?> data, String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "json");

        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        }
    }

    private String generateFileName(String baseName, String extension) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path path = Paths.get(EXPORT_FOLDER, baseName + "_" + timestamp + "." + extension);
        return path.toString();
    }
}
