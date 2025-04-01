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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getReimbursement() {
        return reimbursement;
    }

    public void setReimbursement(double reimbursement) {
        this.reimbursement = reimbursement;
    }

    public double calculateRefund(){
        if (category == Category.RESTAURANT){
            return amount;
        }else if(category == Category.SUPERMARKET){
            return amount *0.8;
        }
        return 0.0;
    }
}
