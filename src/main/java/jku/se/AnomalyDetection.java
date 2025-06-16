package jku.se;

/**
 * Utility class for detecting mismatches between OCR-extracted data and manually entered invoice data.
 * Compares date, amount, and category fields to identify inconsistencies.
 */
public class AnomalyDetection {

    /**
     * Detects mismatches between OCR-extracted fields and actual invoice fields.
     * Compares date, amount, and category.
     *
     * @param invoice The invoice to check.
     * @return true if a mismatch is found, false otherwise.
     */
    public static boolean detectMismatch(Invoice invoice) {
        boolean mismatch = false;

        if (invoice.ocrDate != null && invoice.getDate() != null) {
            mismatch |= !invoice.getDate().toString().equals(normalizeDate(invoice.ocrDate));
        }

        if (invoice.ocrAmount != null) {
            mismatch |= Math.abs(invoice.getAmount() - parseAmount(invoice.ocrAmount)) > 0.01;
        }

        if (invoice.ocrCategory != null && invoice.getCategory() != null) {
            mismatch |= !invoice.getCategory().name().equalsIgnoreCase(invoice.ocrCategory);
        }

        return mismatch;
    }

    /**
     * Normalizes OCR date format by replacing dots and slashes with hyphens.
     *
     * @param ocrDate Date string from OCR.
     * @return Normalized date string.
     */
    private static String normalizeDate(String ocrDate) {
        return ocrDate.replace(".", "-").replace("/", "-");
    }

    /**
     * Parses the amount from an OCR string and converts it to a double.
     * Handles common European formats (e.g., "1.234,56 €").
     *
     * @param amount Amount string from OCR.
     * @return Parsed double value, or 0.0 if parsing fails.
     */
    private static double parseAmount(String amount) {
        try {
            return Double.parseDouble(amount.replace(",", ".").replace("€", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
