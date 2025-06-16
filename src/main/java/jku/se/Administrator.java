package jku.se;

import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents an administrator user with extended permissions.
 * Administrators can view and manage all invoices, add or delete users,
 * and approve or decline invoices.
 */
public class Administrator extends User{
    private static final Logger LOGGER = Logger.getLogger(Administrator.class.getName());

    /**
     * Constructs an Administrator object.
     * @param name Administrator's name.
     * @param email Administrator's email.
     * @param password Administrator's password.
     */
    public Administrator (String name, String email, String password){
        super(name, email, password, true);
    }

    /**
     * Retrieves all invoices stored in the system (visible to admins only).
     * @return List of all invoices.
     */
    public List<Invoice> viewAllInvoices() { //#15- Magdalena
        return InvoiceRepository.getAllInvoicesAdmin();
    }

    /**
     * Adds a new user to the system.
     * @param name Name of the user.
     * @param email Email address.
     * @param password Password.
     * @param isAdministrator false for normal users.
     */
    public void addUser(String name, String email, String password, boolean isAdministrator) { //#18 Magdalena
        User newUser = new User(name, email, password, isAdministrator);
        UserRepository.addUser(newUser);  //add user in database
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Add new User: " + email);
        }
    }

    /**
     * Adds a new administrator to the system.
     * @param name Name of the administrator.
     * @param email Email address.
     * @param password Password.
     * @param isAdministrator true for admin accounts.
     */
    public void addAdministrator(String name, String email, String password, boolean isAdministrator) { //#18 Magdalena
        User newUser = new User(name, email, password, isAdministrator);
        UserRepository.addUser(newUser);  //add user in database
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Add new Admin: " + email);
        }
    }

    /**
     * Deletes a user or administrator from the system.
     * @param email Email of the user to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteUser(String email) {
        return UserRepository.deleteUser(email);
    }


    /**
     * Approves an invoice by setting its status to APPROVED and updating the database.
     * @param invoice Invoice to approve.
     */
    public void approveInvoice(Invoice invoice) {
        invoice.setStatus(Status.APPROVED); // set status
        InvoiceRepository.updateInvoiceStatus(invoice); // update status in the database
    }

    /**
     * Declines an invoice by setting its status to DECLINED and updating the database.
     * @param invoice Invoice to decline.
     */
    public void declinedInvoice(Invoice invoice)  {
        invoice.setStatus(Status.DECLINED); // set status
        InvoiceRepository.updateInvoiceStatus(invoice); // update status in the database
    }

    /**
     * Corrects invoice data such as amount, category, or date.
     * @param invoice Invoice to correct.
     * @param newAmount Corrected amount.
     * @param newCategory Corrected category.
     * @param newDate Corrected date.
     */
    public void correctInvoice(Invoice invoice, double newAmount, Category newCategory, LocalDate newDate) {
        invoice.correct(newAmount, newCategory, newDate);
    }

}
