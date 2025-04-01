package jku.se.repository;

import jku.se.Invoice;

public class InvoiceRepository {
    public void saveInvoice(Invoice invoice) {
        // Hier die Logik zum Speichern der Rechnung in der Datenbank
        // z.B. mit JDBC, Hibernate, etc.
        System.out.println("Rechnung gespeichert: " + invoice);
    }
}