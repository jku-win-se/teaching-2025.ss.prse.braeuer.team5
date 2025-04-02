package jku.se;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.Objects;

public class Invoice {
    private String userEmail;
    private LocalDate date;
    private double amount;
    private Category category;
    private Status status;
    private String file_Url;
    private LocalDateTime createdAt;
    private double reimbursement;
    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB in Bytes

    private static final double RESTAURANT_REFUND = 3.0;              // maximaler Rückerstattungsbetrag, kann später vom admin geändert werden
    private static final double SUPERMARKET_REFUND = 2.5;


    public Invoice(String userEmail, LocalDate date, double amount, Category category, Status status, String file_Url, LocalDateTime createdAt, double reimbursement) {
        this.userEmail = userEmail;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.status = status;
        this.file_Url = file_Url;
        this.createdAt = createdAt; //brauchen wir um zu vergleichen, ob die Rechnung im selben Monat eingereicht wurde
        this.reimbursement = reimbursement;
    }

    // --- Validierungsmethoden ---
    private LocalDate validateDate(LocalDate date) {
        validateDate(this.date);
        validateAmount(this.amount);
        validateReimbursement(this.reimbursement);


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

    private double validateReimbursement(double reimbursement) {
        double expected = calculateRefund();
        if (Math.abs(reimbursement - expected) > 0.01) {
            throw new IllegalArgumentException("Rückerstattung entspricht nicht den Regeln");
        }
        return reimbursement;
    }

    public static void validateFileFormat(File file) throws IllegalArgumentException {
        if (file == null) {
            throw new IllegalArgumentException("No file selected");
        }

        String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".jpg") &&
                !fileName.endsWith(".jpeg") &&
                !fileName.endsWith(".png") &&
                !fileName.endsWith(".pdf")) {
            throw new IllegalArgumentException(
                    "Invalid file format. Only JPG, PNG, or PDF allowed."
            );
        }
    }


    public String getUserEmail() {return userEmail;}

    public void setUserEmail(String userEmail) {this.userEmail = userEmail;}

    public double getAmount() {return amount;}

    public void setAmount(double amount) {this.amount = amount;}

    public Category getCategory() {return category;}

    public void setCategory(Category category) {this.category = category;}

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getFileUrl() {
        return file_Url;
    }

    public void setFileUrl(String fileUrl) {
        this.file_Url = fileUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getReimbursement() {
        return calculateRefund();
    }

    public void setReimbursement(double reimbursement) {
        this.reimbursement = reimbursement;
    }

    public LocalDate getDate() {return date;}

    public double calculateRefund(){
        double maxRefund = 0;

        if (category == Category.RESTAURANT){
            maxRefund = RESTAURANT_REFUND;
        }else if(category == Category.SUPERMARKET){
            maxRefund = SUPERMARKET_REFUND;
        }

        // Wenn der Rechnungsbetrag kleiner ist als der maximal mögliche Rückerstattungsbetrag,
        // gibt es nur so viel Rückerstattung wie der Rechnungsbetrag
        if (amount < maxRefund) {
            return amount;
        } else {
            return maxRefund;
        }
    }

    //getter for table invoice AdminDashboard
    public String getStatusString() {
        return status.name();
    }
    public String getCategoryString() {
        return category.name();
    }


}
