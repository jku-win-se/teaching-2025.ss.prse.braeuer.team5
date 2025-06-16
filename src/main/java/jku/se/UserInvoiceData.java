package jku.se;

/**
 * The {@code UserInvoiceData} class stores basic information about a user's invoices.
 * It is used primarily in statistics or reporting features to display user names, emails,
 * and the number of invoices submitted.
 */
public class UserInvoiceData {
    private String name;
    private String email;
    private int invoiceCount;

    /**
     * Constructs a new {@code UserInvoiceData} object with the given details.
     *
     * @param name the user's name
     * @param email the user's email
     * @param invoiceCount the number of invoices submitted by the user
     */
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
