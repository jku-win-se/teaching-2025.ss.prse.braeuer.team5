
import jku.se.CloudOCRService;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class CloudOCRServiceTest {

    private final CloudOCRService service = new CloudOCRService();

    @Test
    void testExtractDate_variousFormats() {
        String[] inputs = {
                "Invoice from 12.04.2023",
                "Date: 2023-04-12",
                "Date: 5 May 2023",
                "Purchase Date: April 5, 2023"
        };
        String[] expected = {
                "12.04.2023",
                "2023-04-12",
                "5 May 2023",
                "April 5, 2023"
        };

        for (int i = 0; i < inputs.length; i++) {
            String result = service.extractDate(inputs[i]);
            assertEquals(expected[i], result);
        }
    }

    @Test
    void testExtractDate_notFound() {
        String input = "No date present.";
        assertEquals("Not found", service.extractDate(input));
    }

    @Test
    void testExtractAmount_withKeywords() {
        String input = "Amount: 34,95 EUR\nTotal: 55.00\nSum: 12,30";
        String result = service.extractAmount(input);
        assertEquals("34.95", result);
    }

    @Test
    void testExtractAmount_fallbackToLargest() {
        String input = "1.00\n3.00\n999.99\n1001.00\n0.20";
        String result = service.extractAmount(input);
        assertEquals("999,99", result);
    }

    @Test
    void testExtractAmount_notFound() {
        String input = "No amounts here.";
        assertEquals("Not found", service.extractAmount(input));
    }

    @Test
    void testDetectCategory_restaurant() {
        String input = "Receipt from a pizzeria and a café.";
        assertEquals("RESTAURANT", service.detectCategory(input));
    }

    @Test
    void testDetectCategory_supermarket() {
        String input = "Shopping at Hofer and Spar.";
        assertEquals("SUPERMARKET", service.detectCategory(input));
    }

    @Test
    void testDetectCategory_other() {
        String input = "Receipt from a gas station.";
        assertEquals("OTHER", service.detectCategory(input));
    }

    @Test
    void testExtractTextFromJson_valid() {
        String json = """
                {
                  "responses": [
                    {
                      "textAnnotations": [
                        {
                          "description": "Sample OCR text\\nAnother line"
                        }
                      ]
                    }
                  ]
                }
                """;
        String extracted = service.extractTextFromJson(json);
        assertEquals("Sample OCR text\nAnother line", extracted);
    }

    @Test
    void testExtractTextFromJson_invalid() {
        String json = "{}";
        String extracted = service.extractTextFromJson(json);
        assertEquals("", extracted);
    }

    @Test
    void testBuildRequestJson_containsBase64ImageAndStructure() {
        String fakeBase64 = "dGVzdF9pbWFnZQ==";
        String json = service.buildRequestJson(fakeBase64);

        assertTrue(json.contains("\"content\": \"" + fakeBase64 + "\""), "Base64 content should be embedded");
        assertTrue(json.contains("\"type\": \"TEXT_DETECTION\""), "Feature type TEXT_DETECTION should be included");
        assertTrue(json.contains("\"requests\""), "JSON should include a requests field");
    }

    @Test
    void testEncodeImageToBase64_smokeTest() throws IOException {
        // Create a temporary file with some dummy bytes
        File tempImage = File.createTempFile("testImage", ".tmp");
        try (OutputStream os = new java.io.FileOutputStream(tempImage)) {
            os.write(new byte[] {1, 2, 3, 4});
        }

        String base64 = service.encodeImageToBase64(tempImage);
        assertNotNull(base64, "Base64 string should not be null");
        assertFalse(base64.isEmpty(), "Base64 string should not be empty");

        String expectedBase64 = Base64.getEncoder().encodeToString(new byte[]{1, 2, 3, 4});
        assertEquals(expectedBase64, base64);
    }
}