package jku.se;

public enum Category {
    //mihjov13
    RESTAURANT("Restaurant"),
    SUPERMARKET("Supermarkt");

    private final String displayName;
    private volatile double customRefundAmount = -1; // -1 = Standardwert

    Category(String displayName) {
        this.displayName = displayName;
    }

    // Admin kann Beträge überschreiben
    public static void setCustomRefundAmount(Category category, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Betrag muss positiv sein");
        category.customRefundAmount = amount;
    }

    public double getRefundAmount() {
        return customRefundAmount >= 0 ? customRefundAmount : getDefaultRefund();
    }

    private double getDefaultRefund() {
        return switch (this) {
            case RESTAURANT -> 3.00;
            case SUPERMARKET -> 2.50;
        };
    }
}
