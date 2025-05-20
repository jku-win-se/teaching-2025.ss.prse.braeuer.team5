package jku.se.export;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfExporter {

    private static final int MARGIN = 50;
    private static final int PAGE_WIDTH = 595;  // A4 width in points
    private static final int PAGE_HEIGHT = 842; // A4 height in points
    private static final int ROW_HEIGHT = 20;
    private static final int HEADER_FONT_SIZE = 16;
    private static final int TEXT_FONT_SIZE = 12;

    private final PDDocument document;
    private PDPageContentStream contentStream;
    private int yPosition;

    public PdfExporter() {
        this.document = new PDDocument();
        this.yPosition = PAGE_HEIGHT - MARGIN;
    }

    public PDDocument getDocument() {
        return document;
    }

    public PDPageContentStream getContentStream() {
        return contentStream;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public void startPage() throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        if (contentStream != null) {
            contentStream.close();
        }
        contentStream = new PDPageContentStream(document, page);
        yPosition = PAGE_HEIGHT - MARGIN;
    }

    public void end() throws IOException {
        if (contentStream != null) {
            contentStream.close();
        }
    }

    public void addTitle(String title) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
        writeText(title);
        yPosition -= 2 * ROW_HEIGHT;
    }

    public void addParagraph(String text) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, TEXT_FONT_SIZE);
        writeText(text);
        yPosition -= ROW_HEIGHT;
    }

    public void addTable(List<String> headers, List<List<String>> rows) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, TEXT_FONT_SIZE);

        float colWidth = (PAGE_WIDTH - 2 * MARGIN) / headers.size();
        float startX = MARGIN;

        if (yPosition < MARGIN + ROW_HEIGHT) {
            startPage();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, TEXT_FONT_SIZE);
        }
        float startY = yPosition;

        for (int i = 0; i < headers.size(); i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(startX + i * colWidth + 5, startY);
            contentStream.showText(headers.get(i));
            contentStream.endText();
        }

        yPosition -= ROW_HEIGHT;

        contentStream.setFont(PDType1Font.HELVETICA, TEXT_FONT_SIZE);
        for (List<String> row : rows) {
            if (yPosition < MARGIN + ROW_HEIGHT) {
                contentStream.close();

                startPage();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, TEXT_FONT_SIZE);
                yPosition -= ROW_HEIGHT;
                for (int i = 0; i < headers.size(); i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(startX + i * colWidth + 5, yPosition + ROW_HEIGHT);
                    contentStream.showText(headers.get(i));
                    contentStream.endText();
                }

                contentStream.setFont(PDType1Font.HELVETICA, TEXT_FONT_SIZE);
            }

            for (int i = 0; i < row.size(); i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(startX + i * colWidth + 5, yPosition);
                contentStream.showText(row.get(i));
                contentStream.endText();
            }
            yPosition -= ROW_HEIGHT;
        }
        yPosition -= ROW_HEIGHT / 2;
    }

    /**
     * Fügt ein Bild in das PDF ein.
     * @param image  Das BufferedImage, das eingefügt werden soll
     * @param x      X-Position (von links unten)
     * @param y      Y-Position (von links unten)
     * @param width  Breite des Bildes in PDF-Punkten
     * @param height Höhe des Bildes in PDF-Punkten
     * @throws IOException falls Einfügen fehlschlägt
     */
    public void addImage(BufferedImage image, float x, float y, float width, float height) throws IOException {
        if (contentStream == null || document == null) {
            throw new IllegalStateException("ContentStream oder Dokument sind nicht initialisiert. Rufe startPage() zuerst auf.");
        }
        PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
        contentStream.drawImage(pdImage, x, y, width, height);
    }

    /**
     * Hilfsmethode: Bild einfügen und yPosition nach unten verschieben, sodass der folgende Text darunter ist.
     * @param image  BufferedImage
     * @param x      x-Position
     * @param width  Breite des Bildes
     * @param height Höhe des Bildes
     * @param gap    Abstand nach unten zum nächsten Element
     * @throws IOException
     */
    public void addImageAndMovePosition(BufferedImage image, float x, float width, float height, float gap) throws IOException {
        float y = yPosition - height;
        addImage(image, x, y, width, height);
        yPosition = (int)(y - gap);
    }

    public void saveToFile(String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "pdf");
        document.save(new File(fileName));
        document.close();
    }

    private void writeText(String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(text);
        contentStream.endText();
    }

    private String generateFileName(String baseName, String extension) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return System.getProperty("user.home") + File.separator + "Downloads" + File.separator + baseName + "_" + timestamp + "." + extension;
    }
}
