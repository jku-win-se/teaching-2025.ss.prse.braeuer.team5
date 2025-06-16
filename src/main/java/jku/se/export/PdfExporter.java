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

/**
 * Utility class for generating PDF documents using Apache PDFBox.
 * Supports adding titles, paragraphs, tables, and images, and saving the result as a file.
 */
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

    /**
     * Initializes a new PDF document and sets the starting Y-position.
     */
    public PdfExporter() {
        this.document = new PDDocument();
        this.yPosition = PAGE_HEIGHT - MARGIN;
    }

    /**
     * Returns the internal PDF document.
     * @return the PDDocument instance.
     */
    public PDDocument getDocument() {
        return document;
    }

    /**
     * Returns the current content stream used for writing to the PDF.
     * @return the PDPageContentStream instance.
     */
    public PDPageContentStream getContentStream() {
        return contentStream;
    }

    /**
     * Returns the current vertical writing position on the page.
     * @return the Y-position.
     */
    public int getYPosition() {
        return yPosition;
    }

    /**
     * Sets the current vertical writing position.
     * @param yPosition the Y-position to set.
     */
    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    /**
     * Starts a new page in the document and initializes a new content stream.
     * @throws IOException if the page or stream cannot be created.
     */
    public void startPage() throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        if (contentStream != null) {
            contentStream.close();
        }
        contentStream = new PDPageContentStream(document, page);
        yPosition = PAGE_HEIGHT - MARGIN;
    }

    /**
     * Finalizes the current content stream (e.g., before saving or starting a new page).
     * @throws IOException if closing the stream fails.
     */
    public void end() throws IOException {
        if (contentStream != null) {
            contentStream.close();
        }
    }

    /**
     * Adds a title to the PDF using a bold font and larger font size.
     * @param title the text to display as the title.
     * @throws IOException if writing fails.
     */
    public void addTitle(String title) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
        writeText(title);
        yPosition -= 2 * ROW_HEIGHT;
    }

    /**
     * Adds a simple paragraph of text to the PDF using a regular font.
     * @param text the paragraph content.
     * @throws IOException if writing fails.
     */
    public void addParagraph(String text) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, TEXT_FONT_SIZE);
        writeText(text);
        yPosition -= ROW_HEIGHT;
    }

    /**
     * Adds a table with headers and rows to the PDF.
     * Handles page breaks automatically if the table is too long for one page.
     * @param headers List of column headers.
     * @param rows List of rows, each being a list of cell values.
     * @throws IOException if writing fails.
     */
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
     * Insert an image into the PDF.
     * @param image  The BufferedImage, which should be inserted
     * @param x      X-position (from bottom left)
     * @param y      Y-position (from bottom left)
     * @param width  Width of the image in PDF points
     * @param height Height of the image in PDF points
     * @throws IOException if insertion fails
     */
    public void addImage(BufferedImage image, float x, float y, float width, float height) throws IOException {
        if (contentStream == null || document == null) {
            throw new IllegalStateException("ContentStream or document are not initialized. Call startPage() first.");
        }
        PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
        contentStream.drawImage(pdImage, x, y, width, height);
    }

    /**
     * Helper method: Insert image and shift yPosition down so that the following text is underneath.
     * @param image  BufferedImage
     * @param x      x-position
     * @param width  Width of the image
     * @param height Height of the image
     * @param gap    Distance down to the next element
     * @throws IOException
     */
    public void addImageAndMovePosition(BufferedImage image, float x, float width, float height, float gap) throws IOException {
        float y = yPosition - height;
        addImage(image, x, y, width, height);
        yPosition = (int)(y - gap);
    }

    /**
     * Saves the current PDF document to a file with a timestamped name in the Downloads folder.
     * @param baseFileName Base name of the file (without extension or timestamp).
     * @throws IOException if saving fails.
     */
    public void saveToFile(String baseFileName) throws IOException {
        String fileName = generateFileName(baseFileName, "pdf");
        document.save(new File(fileName));
        document.close();
    }

    /**
     * Writes text to the current content stream at the current y-position.
     * @param text the text to write.
     * @throws IOException if writing fails.
     */
    private void writeText(String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(text);
        contentStream.endText();
    }

    /**
     * Generates a full file path in the Downloads folder with timestamp.
     * @param baseName base name of the file.
     * @param extension file extension (e.g., "pdf").
     * @return the full path to the file.
     */
    private String generateFileName(String baseName, String extension) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return System.getProperty("user.home") + File.separator + "Downloads" + File.separator + baseName + "_" + timestamp + "." + extension;
    }
}
