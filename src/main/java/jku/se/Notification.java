package jku.se;

public class Notification {
    public void sendInApp(User user, String message){
        System.out.println("In-App-Benachrichtigung für " + user.getName()+ ": " + message);
    }

    public void sendEmail(User user, String message){
        System.out.println("E-Mail an " + user.getEmail() + ": "+ message);
    }
}
