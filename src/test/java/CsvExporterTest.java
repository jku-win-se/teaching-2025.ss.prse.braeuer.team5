import jku.se.export.CsvExporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CsvExporterTest {

    @Test
    void testConstructorWithDefaultDelimiter() {
        CsvExporter exporter = new CsvExporter();
        assertEquals(";", exporter.getDelimiter());
    }

    @Test
    void testEscapeCsv_WithSpecialCharacters() {
        CsvExporter exporter = new CsvExporter();
        assertEquals("\"text;with;delimiter\"", exporter.escapeCsv("text;with;delimiter"));
        assertEquals("\"text \"\"with\"\" quotes\"", exporter.escapeCsv("text \"with\" quotes"));
        assertEquals("\"text\nwith\nnewlines\"", exporter.escapeCsv("text\nwith\nnewlines"));
    }

    @Test
    void testExport_SingleRow() throws IOException {
        CsvExporter exporter = new CsvExporter();
        String baseFileName = "test";  // Wichtig: kein Pfad, nur Basisname

        Map<String, String> row = new HashMap<>();
        row.put("Name", "John");
        row.put("Age", "30");

        exporter.export(Collections.singletonList(row), baseFileName);

        // Verzeichnis Downloads
        Path downloadsDir = Path.of(System.getProperty("user.home"), "Downloads");

        // Suche die zuletzt erstellte Datei, die mit "test_" anfängt und ".csv" endet
        Path outputFile = Files.list(downloadsDir)
                .filter(p -> p.getFileName().toString().startsWith(baseFileName + "_") && p.getFileName().toString().endsWith(".csv"))
                .max((p1, p2) -> Long.compare(p1.toFile().lastModified(), p2.toFile().lastModified()))
                .orElseThrow(() -> new IOException("Exported file not found"));

        assertTrue(Files.exists(outputFile), "Exported file should exist");

        List<String> lines = Files.readAllLines(outputFile);

        // BOM entfernen, falls vorhanden
        if (lines.get(0).startsWith("\uFEFF")) {
            lines.set(0, lines.get(0).substring(1));
        }

        assertEquals(2, lines.size(), "CSV should have 2 lines (header + one row)");

        // Kopfzeile dynamisch prüfen
        Set<String> headerSet = Set.of(lines.get(0).split(exporter.getDelimiter()));
        assertEquals(Set.of("Name", "Age"), headerSet, "Header should match");

        // Wertezeile dynamisch prüfen
        Set<String> valueSet = Set.of(lines.get(1).split(exporter.getDelimiter()));
        assertEquals(Set.of("John", "30"), valueSet, "Values should match");

        // Optional: Datei nach Test löschen, damit Download-Ordner sauber bleibt
        Files.deleteIfExists(outputFile);
    }

    @Test
    void testExport_WithCustomDelimiter() throws IOException {
        CsvExporter exporter = new CsvExporter(",");
        String baseFileName = "custom";

        Map<String, String> row = new HashMap<>();
        row.put("ID", "1");
        row.put("Value", "Test");

        exporter.export(Collections.singletonList(row), baseFileName);

        Path downloadsDir = Path.of(System.getProperty("user.home"), "Downloads");

        // Finde Datei, die mit "custom_" anfängt
        Path outputFile = Files.list(downloadsDir)
                .filter(p -> p.getFileName().toString().startsWith(baseFileName + "_") && p.getFileName().toString().endsWith(".csv"))
                .max((p1, p2) -> Long.compare(p1.toFile().lastModified(), p2.toFile().lastModified()))
                .orElseThrow(() -> new IOException("Exported file not found"));

        assertTrue(Files.exists(outputFile), "Exported file should exist");

        List<String> lines = Files.readAllLines(outputFile);

        // BOM entfernen, falls vorhanden
        if (lines.get(0).startsWith("\uFEFF")) {
            lines.set(0, lines.get(0).substring(1));
        }

        assertEquals(2, lines.size(), "CSV should have 2 lines (header + one row)");

        // Kopfzeile dynamisch prüfen
        Set<String> headerSet = Set.of(lines.get(0).split(exporter.getDelimiter()));
        assertEquals(Set.of("ID", "Value"), headerSet, "Header should match");

        // Wertezeile dynamisch prüfen
        Set<String> valueSet = Set.of(lines.get(1).split(exporter.getDelimiter()));
        assertEquals(Set.of("1", "Test"), valueSet, "Values should match");

        // Datei nach Test löschen
        Files.deleteIfExists(outputFile);
    }

}