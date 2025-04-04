package jku.se;

public enum Category {
    RESTAURANT("Restaurant"),
    SUPERMARKET("Supermarkt");

    private final String displayName;
    public volatile double customRefundAmount = -1; // -1 bedeutet, dass kein benutzerdefinierter Wert gesetzt wurde

    // Konstruktor, der den Anzeigenamen der Kategorie festlegt
    Category(String displayName) {
        this.displayName = displayName;
    }

    // Methode zur Berechnung des Rückerstattungsbetrags
    public double getRefundAmount() {
        // Wenn der benutzerdefinierte Rückerstattungsbetrag gesetzt wurde, verwende diesen.
        // Ansonsten gebe den Standardwert zurück.
        return customRefundAmount >= 0 ? customRefundAmount : getDefaultRefund();
    }

    // Stellt sicher, dass der benutzerdefinierte Rückerstattungsbetrag positiv ist
    public static void setCustomRefundAmount(Category category, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Der Betrag muss positiv sein.");
        }
        category.customRefundAmount = amount;
    }

    // Gibt den Standardrückerstattungsbetrag je nach Kategorie zurück
    private double getDefaultRefund() {
        switch (this) {
            case RESTAURANT:
                return 3.00;
            case SUPERMARKET:
                return 2.50;
            default:
                throw new IllegalArgumentException("Unbekannte Kategorie");
        }
    }

    // Getter für den Anzeigenamen der Kategorie
    public String getDisplayName() {
        return displayName;
    }
}
