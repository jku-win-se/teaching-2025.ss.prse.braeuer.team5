package jku.se;

import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.util.List;

public class Administrator extends User{
    private static final Logger LOGGER = Logger.getLogger(Administrator.class.getName());

    public Administrator (String name, String email, String password){
        super(name, email, password, true);
    }

    //to see all invoices in admin dashboard
    public List<Invoice> viewAllInvoices() { //#15- Magdalena
        return InvoiceRepository.getAllInvoicesAdmin();
    }


    //new user isAdministrator = false
    public void addUser(String name, String email, String password, boolean isAdministrator) { //#18 Magdalena
        User newUser = new User(name, email, password, isAdministrator);
        UserRepository.addUser(newUser);  //add user in database
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Add new User: " + email);
        }
    }

    //new admin isAdministrator = true
    public void addAdministrator(String name, String email, String password, boolean isAdministrator) { //#18 Magdalena
        User newUser = new User(name, email, password, isAdministrator);
        UserRepository.addUser(newUser);  //add user in database
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Add new Admin: " + email);
        }
    }

    //delete admin/user
    public boolean deleteUser(String email) {
        return UserRepository.deleteUser(email);
    }


    // using these methods, the admins can approve the individual invoices, etc.
    public void approveInvoice(Invoice invoice) {
        invoice.setStatus(Status.APPROVED); // set status
        InvoiceRepository.updateInvoiceStatus(invoice); // update status in the database
    }

    public void declinedInvoice(Invoice invoice)  {
        invoice.setStatus(Status.DECLINED); // set status
        InvoiceRepository.updateInvoiceStatus(invoice); // update status in the database
    }

    public void correctInvoice(Invoice invoice, double newAmount, Category newCategory, LocalDate newDate) {
        invoice.correct(newAmount, newCategory, newDate);
    }

}
