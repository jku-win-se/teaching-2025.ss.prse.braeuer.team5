package jku.se.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class JsonExporter {

    public void export(Map<String, ?> data, String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "json");

        try (FileWriter writer = new FileWriter(new File(fileName))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        }
    }

    private String generateFileName(String baseName, String extension) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return System.getProperty("user.home") + File.separator + "Downloads" + File.separator + baseName + "_" + timestamp + "." + extension;
    }
}
