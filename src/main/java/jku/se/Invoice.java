package jku.se;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;

public class Invoice {
    private String userEmail;
    private LocalDate date;
    private double amount;
    private Category category;
    private Status status;
    private String file_Url;
    private LocalDateTime createdAt;
    private double reimbursement;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB in Bytes

    public Invoice(String userEmail, LocalDate date, double amount, Category category, Status status, String file_Url, LocalDateTime createdAt, double reimbursement) {
        this.userEmail = userEmail;
        this.date = validateDate(date);
        this.amount = validateAmount(amount); // ZUERST Amount prüfen!
        this.category = category;
        this.status = status;
        this.file_Url = file_Url;
        this.createdAt = createdAt;
        this.reimbursement = reimbursement;
    }

    // --- Validierungsmethoden ---
    private LocalDate validateDate(LocalDate date) {
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

    private double validateReimbursement(double reimbursement, double amount) {
        double maxAllowed = (category == Category.RESTAURANT) ? RESTAURANT_REFUND : SUPERMARKET_REFUND;
        double actualRefund = Math.min(amount, maxAllowed); // Rückerstattung darf nie mehr als maxAllowed sein

        if (reimbursement < 0 || reimbursement > actualRefund) {
            throw new IllegalArgumentException("Rückerstattung entspricht nicht den Regeln");
        }
        return reimbursement;
    }

    public static void validateFile(File file) {
        // Existenzprüfung hinzufügen
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }

        // Formatvalidierung
        String fileName = file.getName().toLowerCase();
        if (!fileName.matches(".*\\.(jpg|jpeg|png|pdf)$")) {
            throw new IllegalArgumentException("Only JPG/PNG/PDF allowed");
        }

        // Größenvalidierung
        if (file.length() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(String.format(
                    "File too large (%.2f MB > 10 MB limit)",
                    file.length() / (1024.0 * 1024)
            ));
        }
    }

    public String getUserEmail() {return userEmail;}
    public double getAmount() {return amount;}
    public void setAmount(double amount) {this.amount = amount;}
    public Category getCategory() {return category;}
    public void setCategory(Category category) {this.category = category;}
    public void setFileUrl(String fileUrl) {file_Url = fileUrl;}
    public LocalDate getDate() {return date;}
    public String getFile_Url() {return file_Url;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public double getReimbursement() {return reimbursement;}
    public void setReimbursement(double reimbursement) {this.reimbursement = reimbursement;}

    // Berechnung des Rückerstattungsbetrags unter Verwendung der Kategorie und deren Standard-/benutzerdefinierten Rückerstattungsbetrag
    public double calculateRefund() {
        // Rückerstattungsbetrag basierend auf der Kategorie und deren gesetztem oder Standardwert
        if (amount < category.getRefundAmount()) return amount;
        return category.getRefundAmount();
    }

    // Getter für die Tabelle der Rechnungen im AdminDashboard
    public String getStatusString() {
        return status.name();
    }

    public String getCategoryString() {
        return category.name();
    }
}
