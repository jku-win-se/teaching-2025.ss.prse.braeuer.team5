package jku.se;

import jku.se.repository.InvoiceRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    private static final Logger LOGGER = Logger.getLogger(User.class.getName());

    private String name;
    private String email;
    private String password;
    private boolean isAdministrator;
    private List<Invoice> invoices;

    private String preferredNotificationMethod;
    private Notification notification = new Notification();

    //Constructor
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


    public void login() { //frida #3
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info(name + " hat sich eingeloggt.");
        }
    }

    //to see all invoices in user dashboard
    public List<Invoice> viewAllInvoices() { //#15- Magdalena
        return InvoiceRepository.getAllInvoicesUser(getEmail());
    } //#9 -Magdalena

    public List<Invoice> viewHistory() { //jovana #11??
        return invoices;
    }

    public void receiveNotification(String message) { // jovana # 8
        Notification notification = new Notification();
        notification.sendInApp(this,message);
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

}
