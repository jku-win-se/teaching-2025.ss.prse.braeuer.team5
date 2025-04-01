package jku.se;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Invoice {
    private String userEmail;
    private LocalDate date;
    private double amount;
    private String category;
    private String status;
    private String fileUrl;
    private LocalDateTime createdAt;
    private double reimbursement;

    public Invoice(String userEmail, LocalDate date, double amount, String category, String status, String fileUrl, LocalDateTime createdAt, double reimbursement) {
        this.userEmail = userEmail;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.status = status;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt; //brauchen wir um zu vergleichen, ob die Rechnung im selben Monat eingereicht wurde
        this.reimbursement = reimbursement;
    }
}
