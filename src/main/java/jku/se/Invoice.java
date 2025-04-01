package jku.se;

import java.io.File;
import java.time.LocalDate;

public class Invoice {
    public String id;
    public LocalDate date;
    public double amount;
    public Category category;
    public Status status;
    public File file;
    public User submittedBy;

    public Invoice(String id, LocalDate date, double amount, Category category, Status status, File file, User submittedBy) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.status = status;
        this.file = file;
        this.submittedBy = submittedBy;
    }
}
