package jku.se;

import com.google.gson.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

/**
 * Service class for performing OCR (Optical Character Recognition) using Google Cloud Vision API.
 * Extracts structured data such as date, amount, and category from an image of a receipt or invoice.
 */
public class CloudOCRService {

    private static final Logger LOGGER = Logger.getLogger(CloudOCRService.class.getName());

    private static final String API_KEY = "AIzaSyBjoG6cn0pXDb9OlZliX4oDmwbMLnKrfUE";
    private static final String ENDPOINT = "https://vision.googleapis.com/v1/images:annotate?key=" + API_KEY;

    /**
     * Inner class to encapsulate OCR results.
     */
    public static class OCRResult {
        public String date;
        public String amount;
        public String category;

        public OCRResult(String date, String amount, String category) {
            this.date = date;
            this.amount = amount;
            this.category = category;
        }

        @Override
        public String toString() {
            return "Date: " + date + ", Amount: " + amount + ", Category: " + category;
        }
    }

    /**
     * Analyzes an image using Google Cloud Vision API and extracts structured information.
     *
     * @param imageFile The image file to analyze.
     * @return OCRResult containing date, amount, and category.
     * @throws IOException if the request fails.
     */
    public OCRResult analyzeImage(File imageFile) throws IOException {
        String base64Image = encodeImageToBase64(imageFile);
        String requestBody = buildRequestJson(base64Image);

        HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes());
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("OCR API request failed with response code: " + responseCode);
        }

        String response = new String(connection.getInputStream().readAllBytes());

        String extractedText = extractTextFromJson(response);


        return new OCRResult(
                extractDate(extractedText),
                extractAmount(extractedText),
                detectCategory(extractedText)
        );
    }

    /**
     * Encodes an image file to a Base64 string.
     *
     * @param imageFile The file to encode.
     * @return Base64-encoded image string.
     * @throws IOException if reading the file fails.
     */
    public String encodeImageToBase64(File imageFile) throws IOException {
        byte[] imageBytes = new FileInputStream(imageFile).readAllBytes();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Builds the JSON request body for the OCR API.
     *
     * @param base64Image Base64-encoded image string.
     * @return JSON request as a string.
     */
    public String buildRequestJson(String base64Image) {
        return "{\n" +
                "  \"requests\": [\n" +
                "    {\n" +
                "      \"image\": {\n" +
                "        \"content\": \"" + base64Image + "\"\n" +
                "      },\n" +
                "      \"features\": [\n" +
                "        {\n" +
                "          \"type\": \"TEXT_DETECTION\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    /**
     * Parses the OCR API response JSON and extracts the full detected text.
     *
     * @param json JSON response from the API.
     * @return Extracted plain text.
     */
    public String extractTextFromJson(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray responses = root.getAsJsonArray("responses");

            if (responses != null && responses.size() > 0) {
                JsonObject firstResponse = responses.get(0).getAsJsonObject();
                JsonArray textAnnotations = firstResponse.getAsJsonArray("textAnnotations");

                if (textAnnotations != null && textAnnotations.size() > 0) {
                    JsonObject firstAnnotation = textAnnotations.get(0).getAsJsonObject();
                    return firstAnnotation.get("description").getAsString();
                }
            }
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, () -> "Error parsing OCR response: " + e.getMessage());
        }
        return "";
    }

    /**
     * Extracts a date string from raw text using several regex formats.
     *
     * @param text Raw OCR text.
     * @return Extracted date string or "Not found".
     */
    public String extractDate(String text) {
        Pattern[] patterns = {
                Pattern.compile("\\b(\\d{2}[./-]\\d{2}[./-]\\d{4})\\b"),
                Pattern.compile("\\b(\\d{4}[./-]\\d{2}[./-]\\d{2})\\b"),
                Pattern.compile("\\b(\\d{1,2}\\s+[A-Za-z]{3,9}\\s+\\d{4})\\b"),
                Pattern.compile("\\b([A-Za-z]{3,9}\\s+\\d{1,2},\\s+\\d{4})\\b")
        };

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "Not found";
    }

    /**
     * Extracts the amount from OCR text, prioritizing labeled values (e.g., "Amount").
     *
     * @param text OCR-detected text.
     * @return Extracted amount string or "Not found".
     */
    public String extractAmount(String text) {
        Pattern strongPattern = Pattern.compile(
                "(Betrag|Amount|Total|Summe)[^0-9]{0,10}([0-9]+[.,][0-9]{2})",
                Pattern.CASE_INSENSITIVE
        );
        Matcher strongMatcher = strongPattern.matcher(text);

        if (strongMatcher.find()) {
            String value = strongMatcher.group(2).replace(",", ".").trim();
            return value;
        }

        double maxAmount = -1.0;
        Pattern fallbackPattern = Pattern.compile("\\b([0-9]+[.,][0-9]{2})\\b");
        Matcher fallbackMatcher = fallbackPattern.matcher(text);

        while (fallbackMatcher.find()) {
            String value = fallbackMatcher.group(1).replace(",", ".");
            try {
                double amount = Double.parseDouble(value);

                if (amount > 0.5 && amount <= 1000.0) {
                    if (amount > maxAmount) {
                        maxAmount = amount;
                    }
                }
            } catch (NumberFormatException e) {
            }
        }

        if (maxAmount != -1.0) {
            return String.format("%.2f", maxAmount);
        }
        return "Not found";
    }

    /**
     * Attempts to detect whether the receipt belongs to a restaurant or supermarket.
     *
     * @param text OCR-detected text.
     * @return Category label: "RESTAURANT", "SUPERMARKET", or "OTHER".
     */
    public String detectCategory(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("restaurant") || lower.contains("pizzeria") || lower.contains("café") || lower.contains("gasthaus") || lower.contains("mensa")) {
            return "RESTAURANT";
        } else if (lower.contains("supermarkt") || lower.contains("hofer") || lower.contains("spar") || lower.contains("billa")) {
            return "SUPERMARKET";
        }
        return "OTHER";
    }
}
