package jku.se;

public class UserInvoiceData {
    private String name;
    private String email;
    private int invoiceCount;

    public UserInvoiceData(String name, String email, int invoiceCount) {
        this.name = name;
        this.email = email;
        this.invoiceCount = invoiceCount;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getInvoiceCount() { return invoiceCount; }
    public void setInvoiceCount(int invoiceCount) { this.invoiceCount = invoiceCount; }
}
