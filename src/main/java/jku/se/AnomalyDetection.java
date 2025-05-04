package jku.se;

public class AnomalyDetection {
    boolean detectDuplicateUpload(User user, Invoice invoice){
        return false;
    }

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

    private static String normalizeDate(String ocrDate) {
        return ocrDate.replace(".", "-").replace("/", "-");
    }

    private static double parseAmount(String amount) {
        try {
            return Double.parseDouble(amount.replace(",", ".").replace("€", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
