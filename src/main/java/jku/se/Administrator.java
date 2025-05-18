package jku.se;

import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Administrator extends User{
    private static final Logger logger = Logger.getLogger(Administrator.class.getName());

    public Administrator (String name, String email, String password){
        super(name, email, password, true);
    }

    //to see all invoices in admin dashboard
    public List<Invoice> viewAllInvoices() { //#15- Magdalena
        return InvoiceRepository.getAllInvoicesAdmin();
    }


    public void checkInovices(){}

    public void flagAnomalies(){}


    public void sendNotification(String message){}

    public void configureRefundAmounts(double restaurant, double supermarket){}

    //new user isAdministrator = false
    public void addUser(String name, String email, String password, boolean isAdministrator) { //#18 Magdalena
        User newUser = new User(name, email, password, isAdministrator);
        UserRepository.addUser(newUser);  //add user in database
        logger.info("Add new User: " + email);
    }

    //new admin isAdministrator = true
    public void addAdministrator(String name, String email, String password, boolean isAdministrator) { //#18 Magdalena
        User newUser = new User(name, email, password, isAdministrator);
        UserRepository.addUser(newUser);  //add user in database
        logger.info("Add new Admin: " + email);
    }

    //delete admin/user
    public boolean deleteUser(String email) {
        return UserRepository.deleteUser(email);
    }


    // using these methods, the admins can approve the individual invoices, etc.
    public void approveInvoice(Invoice invoice) throws SQLException {
        invoice.setStatus(Status.APPROVED); // Status setzen
        InvoiceRepository.updateInvoiceStatus(invoice); // Status in der Datenbank aktualisieren
    }

    public void declinedInvoice(Invoice invoice) throws SQLException {
        invoice.setStatus(Status.DECLINED); // Status setzen
        InvoiceRepository.updateInvoiceStatus(invoice); // Status in der Datenbank aktualisieren
    }

    public void correctInvoice(Invoice invoice, double newAmount, Category newCategory, LocalDate newDate) {
        invoice.correct(newAmount, newCategory, newDate);
    }

}
