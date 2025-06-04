package jku.se;

public enum Category {
    RESTAURANT("Restaurant"),
    SUPERMARKET("Supermarket");

    private final String displayName;
    public double customRefundAmount = -1; // -1 means that no custom value has been set.

    // Constructor that sets the display name of the category
    Category(String displayName) {
        this.displayName = displayName;
    }

    // Method for calculating the reimbursement amount
    public double getRefundAmount() {
        // If the custom refund amount has been set, use this.
        // Otherwise, return the default value.
        return customRefundAmount >= 0 ? customRefundAmount : getDefaultRefund();
    }

    // Ensure that the custom refund amount is positive
    public static void setCustomRefundAmount(Category category, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive");
        }
        category.customRefundAmount = amount;
    }

    // Returns the standard refund amount depending on the category.
    private double getDefaultRefund() {
        switch (this) {
            case RESTAURANT:
                return 3.00;
            case SUPERMARKET:
                return 2.50;
            default:
                throw new IllegalArgumentException("Unknown Category");
        }
    }
}
