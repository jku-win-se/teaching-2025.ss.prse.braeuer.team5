package jku.se;

import jku.se.repository.InvoiceRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code User} class represents a user in the system, which can either be a regular user or an administrator.
 * <p>
 * A user has a name, email, password, and a role (administrator or not).
 * Users can log in, view their invoices, receive notifications, and manage their preferences.
 */
public class User {
    private static final Logger LOGGER = Logger.getLogger(User.class.getName());

    private String name;
    private String email;
    private String password;
    private boolean isAdministrator;
    private List<Invoice> invoices;

    private String preferredNotificationMethod;
    private Notification notification = new Notification();

    /**
     * Constructs a new User with the given parameters.
     *
     * @param name the user's name
     * @param email the user's email
     * @param password the user's password
     * @param isAdministrator {@code true} if the user is an administrator; {@code false} otherwise
     */
    public User(String name, String email, String password, boolean isAdministrator) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdministrator = isAdministrator;
        this.invoices = new ArrayList<>();
        this.preferredNotificationMethod = "In-App";
    }

    // Getter & Setter
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword(){return password;}
    public boolean isAdministrator() {
        return isAdministrator;
    }

    /**
     * Logs the user in and writes a log message.
     */
    public void login() { //frida #3
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info(name + " has logged in.");
        }
    }

    /**
     * Gets all invoices submitted by this user (fetched from the repository).
     *
     * @return a list of all invoices for the user
     */
    public List<Invoice> viewAllInvoices() { //#15- Magdalena
        return InvoiceRepository.getAllInvoicesUser(getEmail());
    } //#9 -Magdalena

    /**
     * Gets the invoice history (locally stored in the user object).
     *
     * @return a list of past invoices (local)
     */
    public List<Invoice> viewHistory() { //jovana #11??
        return invoices;
    }

    /**
     * Sends an in-app notification to the user with the given message.
     *
     * @param message the message to send
     */
    public void receiveNotification(String message) { // jovana # 8
        Notification notification = new Notification();
        notification.sendInApp(this,message);
    }

    /**
     * Sets the Notification object used by this user.
     *
     * @param notification the notification handler
     */
    public void setNotification(Notification notification) {
        this.notification = notification;
    }

}
