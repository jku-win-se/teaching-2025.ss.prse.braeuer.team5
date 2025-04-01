package jku.se;

import java.util.List;
import java.util.ArrayList;

public class User { //Magdalena #18
    private String name;
    private String email;
    private String password;
    private boolean isAdministrator;
    private List<Invoice> invoices;

    private String preferredNotificationMethod;

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
    public boolean isAdministrator() { return isAdministrator; }
    public String getPreferredNotificationMethod(){return preferredNotificationMethod;}

    public void login() { //frida #3
        System.out.println(name + " hat sich eingeloggt.");
    }

    public void uploadInvoice(Invoice invoice) { //jovana #4
        invoices.add(invoice);

        double amount = invoice.calculateRefund();
        String message = "Deine Rechnung über " + amount + " € wurde erfolgreich eingereicht.";

        receiveNotification(message);
    }

    public List<Invoice> viewHistory() { //jovana #11??
        return invoices;
    }

    public void receiveNotification(String message) { // jovana # 8
        Notification notification = new Notification();
        switch (preferredNotificationMethod){
            case "In-App":
                notification.sendInApp(this, message);
                break;
            case "Email":
                notification.sendEmail(this, message);
                break;
            case "Both":
                notification.sendInApp(this, message);
                notification.sendEmail(this, message);
                break;
            default:
                notification.sendInApp(this,message);
        }
    }

    public void changeNotificationSettings(String newPreference) { //jovana - #22
        if(newPreference.equals("Email") || newPreference.equals("In-App")|| newPreference.equals("Both")){
            this.preferredNotificationMethod = newPreference;
            System.out.println("Benachrichtigungspräferenz geändert zu: " + newPreference);
        }else{
            System.out.println("Ungültige Eingabe. Mögliche Optionen: Email, In-App, Both");
        }
    }

}

