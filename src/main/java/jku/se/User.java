package jku.se;

import java.util.List;
import java.util.ArrayList;

public class User { //Magdalena #18
    private String name;
    private String email;
    private String password;
    private boolean isAdministrator;
    private List<Invoice> invoices;

    public User(String name, String email, String password, boolean isAdministrator) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdministrator = isAdministrator;
        this.invoices = new ArrayList<>();
    }

    // Getter & Setter
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword(){return password;}
    public boolean isAdministrator() { return isAdministrator; }

    public void login() { //frida #3
        System.out.println(name + " hat sich eingeloggt.");
    }

    public void uploadInvoice(Invoice invoice) { //jovana #4

    }

    public List<Invoice> viewHistory() { //jovana #11??
        return invoices;
    }

    public void receiveNotification(String message) { // jovana # 8

    }

    public void changeNotificationSettings() { //jovana - #22

    }

}

