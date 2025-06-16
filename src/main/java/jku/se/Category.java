package jku.se;

/**
 * Enum representing the type of invoice category.
 * Each category has a display name and a configurable custom refund amount.
 * If no custom refund amount is set, a default value is used.
 */
public enum Category {
    /**
     * Category for restaurant-related invoices.
     */
    RESTAURANT("Restaurant"),

    /**
     * Category for supermarket-related invoices.
     */
    SUPERMARKET("Supermarket");

    private final String displayName;
    public double customRefundAmount = -1; // -1 means that no custom value has been set.

    /**
     * Constructor that sets the display name for the category.
     *
     * @param displayName Human-readable name of the category.
     */
    Category(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the refund amount for this category.
     * If a custom refund amount is set (≥ 0), it is returned;
     * otherwise, the default value is returned.
     *
     * @return Refund amount in euros.
     */
    public double getRefundAmount() {
        // If the custom refund amount has been set, use this.
        // Otherwise, return the default value.
        return customRefundAmount >= 0 ? customRefundAmount : getDefaultRefund();
    }

    /**
     * Sets a custom refund amount for a specific category.
     *
     * @param category The category to update.
     * @param amount   The custom refund amount (must be ≥ 0).
     * @throws IllegalArgumentException if the amount is negative.
     */
    public static void setCustomRefundAmount(Category category, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount has to be positive");
        }
        category.customRefundAmount = amount;
    }

    /**
     * Returns the default refund amount based on the category type.
     *
     * @return Default refund value in euros.
     * @throws IllegalArgumentException if category is unknown.
     */
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
