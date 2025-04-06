package jku.se;

import java.util.ArrayList;
import java.util.List;

public class Notification {

    public static List<String> messagesSent = new ArrayList<>();
    public void sendInApp(User user, String message){
        String formatted = "[In-App] " + message;
        messagesSent.add(formatted);
        System.out.println("In-App-Benachrichtigung für " + user.getName()+ ": " + message);
    }

    public void sendEmail(User user, String message){
        String formatted = "[Email] " + message;
        messagesSent.add(formatted);
        System.out.println("E-Mail an " + user.getEmail() + ": "+ message);
    }

    public static void clearMessages() {
        messagesSent.clear();
    }
}
