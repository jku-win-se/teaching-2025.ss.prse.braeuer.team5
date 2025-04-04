package jku.se;

public enum Category {
    //mihjov13
    RESTAURANT("Restaurant"),
    SUPERMARKET("Supermarkt");

    private final String displayName;
    public volatile double customRefundAmount = -1; // -1 = Standardwert

    Category(String displayName) {
        this.displayName = displayName;
    }

    // Admin kann Beträge überschreiben
    public static void setCustomRefundAmount(Category category, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Betrag muss positiv sein");
        category.customRefundAmount = amount;
    }

}
