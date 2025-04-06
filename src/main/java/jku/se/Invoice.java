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
    private double reimbursement; // Der Rückerstattungsbetrag
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB in Bytes

    public Invoice(String userEmail, LocalDate date, double amount, Category category, Status status, String file_Url, LocalDateTime createdAt, double reimbursement) {
        this.userEmail = userEmail;
        this.date = date;
        this.amount = amount;
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

    // Diese Methode validiert das Dateiformat und die Größe der hochgeladenen Datei
    public static void validateFile(File file) {
        // Formatvalidierung
        String fileName = file.getName().toLowerCase();
        if (!fileName.matches(".*\\.(jpg|jpeg|png|pdf)$")) {
            throw new IllegalArgumentException("Nur JPG/PNG/PDF-Dateien sind erlaubt");
        }

        // Größenvalidierung (max. 10MB)
        if (file.length() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(String.format(
                    "Datei zu groß (%.2f MB > 10 MB Limit)",
                    file.length() / (1024.0 * 1024)
            ));
        }
    }

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
        this.file_Url = fileUrl;
    }
    public void setDate(LocalDate date){this.date = date;}
    public LocalDate getDate() {
        return date;
    }
    public String getFile_Url() {
        return file_Url;
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

    // Berechnung des Rückerstattungsbetrags anhand der Kategorie und des Betrags
    public double calculateRefund() {
        // Die Rückerstattung hängt vom Betrag und der Kategorie ab
        double maxRefund = category.getRefundAmount();  // Der Rückerstattungsbetrag je nach Kategorie

        if (amount < maxRefund) {
            return amount;  // Wenn der Rechnungsbetrag kleiner ist als der Rückerstattungsbetrag, gibt es nur den Betrag als Rückerstattung
        } else {
            return maxRefund;  // Ansonsten den maximalen Rückerstattungsbetrag
        }
    }

    // Diese Methode wird verwendet, um die Informationen zu einer Rechnung für die Anzeige in einer Tabelle zu formatieren
    public String getStatusString() {
        return status.name();
    }

    public String getCategoryString() {
        return category.name();
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
    public String toString() {
        return date.toString(); // oder z.B. date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

}


