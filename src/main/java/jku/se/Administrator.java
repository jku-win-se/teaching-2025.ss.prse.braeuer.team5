package jku.se;

import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;

import java.util.List;

public class Administrator extends User{

    public Administrator (String name, String email, String password){
        super(name, email, password, true);
    }

    //to see all invoices in admin dashboard
    public List<Invoice> viewAllInvoices() { //#15- Magdalena
        return InvoiceRepository.getAllInvoices();
    }

    public void checkInovices(){}

    public void flagAnomalies(){}

    public void manageUsers(){} //weglassen???

    public void sendNotification(String message){}

    public void configureRefundAmounts(double restaurant, double supermarket){}


    //Sollen wir die zwei Methoden zusammenfassen? Wir haben ja jetzt keine getrennten Tables mehr nur der boolean unterscheidet die zwei
    //new user isAdministrator = false
    public void addUser(String name, String email, String password, boolean isAdministrator) { //#18 Magdalena
        User newUser = new User(name, email, password, isAdministrator);
        UserRepository.addUser(newUser);  //add user in database
        System.out.println("Add new User: " + email);
    }

    //new admin isAdministrator = true
    public void addAdministrator(String name, String email, String password, boolean isAdministrator) { //#18 Magdalena
        User newUser = new User(name, email, password, isAdministrator);
        UserRepository.addUser(newUser);  //add user in database
        System.out.println("Add new Admin: " + email );
    }

    //delete admin/user
    public boolean deleteUser(String email) {
        return UserRepository.deleteUser(email);
    }
}
