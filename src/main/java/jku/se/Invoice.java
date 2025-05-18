package jku.se;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

public class Invoice {
    private String userEmail;
    private LocalDate date;
    private double amount;
    private Category category;
    private Status status;
    private String fileUrl;
    private LocalDateTime createdAt;
    private double reimbursement; // The refund amount
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB in Bytes
    String ocrDate;
    String ocrAmount;
    String ocrCategory;
    private boolean anomalyDetected;

    //Constructor
    public Invoice(String userEmail, LocalDate date, double amount, Category category, Status status, String file_Url, LocalDateTime createdAt, double reimbursement) {
        if (category == null) {
            throw new NullPointerException("Category cannot be null");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount has to be positive");
        }

        if (reimbursement < 0 || reimbursement > category.getRefundAmount()) {
            throw new IllegalArgumentException("Refund does not comply with the rules");
        }

        if (amount > 1000.0) {
            throw new IllegalArgumentException("Amount cannot be greater than 1000 €");
        }

        this.userEmail = userEmail;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.status = status;
        this.fileUrl = file_Url;
        this.createdAt = createdAt;
        this.reimbursement = reimbursement;
    }

    // --- Validierungsmethoden ---
    public LocalDate validateDate(LocalDate date) {
        validateAmount(this.amount);

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Rechnungen nur an Werktagen erlaubt");
        }
        return date;
    }

    private double validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Betrag muss positiv sein");
        }
        return amount;
    }

    // Diese Methode validiert das Dateiformat und die Größe der hochgeladenen Datei
    public static void validateFile(File file) {
        // Existenzprüfung
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Datei existiert nicht");
        }

        // Formatprüfung
        String fileName = file.getName().toLowerCase();
        if (!fileName.matches(".*\\.(jpg|jpeg|png|pdf)$")) {
            throw new IllegalArgumentException("Nur JPG/PNG/PDF-Dateien sind erlaubt");
        }

        // Größenprüfung (max 10MB)
        if (file.length() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(String.format(
                    "Datei zu groß (%.2f MB > 10 MB Limit)", file.length() / (1024.0 * 1024)
            ));
        }
    }
    //getter setter
    public String getUserEmail() {
        return userEmail;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public void setDate(LocalDate date){this.date = date;}
    public LocalDate getDate() {
        return date;
    }
    public String getFile_Url() {
        return fileUrl;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
        public double getReimbursement() {
        return reimbursement;
    }
    public void setReimbursement(double reimbursement) {
        this.reimbursement = reimbursement;
    }
    public void setStatus(Status status){this.status = status;}
    public Status getStatus() {
        return status;
    }

    // Calculation of the refund amount based on the category and amount
    public double calculateRefund() {
        // The refund depends on the amount and the category
        double maxRefund = category.getRefundAmount();  // The refund amount depending on the category

        if (amount < maxRefund) {
            return amount;  //  If the invoice amount is less than the refund amount, only the amount is refunded
        } else {
            return maxRefund;  //Otherwise the maximum refund amount
        }
    }

    // This method is used to format the information on an invoice for display in a table
    public String getStatusString() {
        return status.name();
    }

    public String getCategoryString() {
        return category.name();
    }

    //need a string for user dashboard table
    public String getCreatedAtString() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    //using these methods, the admins can approve the individual invoices, etc.
    public void approve() {
        this.status = Status.APPROVED;
    }

    public void declined() {
        this.status = Status.DECLINED;
    }

    public void correct(double newAmount, Category newCategory, LocalDate newDate) {
        this.amount = newAmount;
        this.category = newCategory;
        this.date = newDate;
        this.status = Status.PROCESSING; //if the invoice is corrected so it is again in the process
    }

    public boolean isEditable() {
        if (createdAt == null) {
            return false;
        }

        LocalDate invoiceDate = createdAt.toLocalDate();
        LocalDate now = LocalDate.now();

        // Die Rechnung ist nur im selben Monat und Jahr editierbar
        return invoiceDate.getMonth() == now.getMonth() && invoiceDate.getYear() == now.getYear();
    }

    //is required in AdminInvoiceManagementController to check the data
    public boolean isDateOnWeekday(LocalDate date) {
        return !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
    }

    public boolean isInCurrentMonth(LocalDate date, LocalDate now) {
        return date.getMonth().equals(now.getMonth()) && date.getYear() == now.getYear();
    }

    public boolean isValidAmount(double amount) {
        return amount >= 0 && amount <= 1000.0;
    }

    public void setOcrData(String date, String amount, String category) {
        this.ocrDate = date;
        this.ocrAmount = amount;
        this.ocrCategory = category;
    }

    public boolean isAnomalyDetected() {
        return anomalyDetected;
    }

    public void setAnomalyDetected(boolean anomalyDetected) {
        this.anomalyDetected = anomalyDetected;
    }

    //for AdminInvoiceManagementController to get all invoices per date
    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " | " + String.format("%.2f", amount) + "€" + " | " + getStatusString();
    }
}


