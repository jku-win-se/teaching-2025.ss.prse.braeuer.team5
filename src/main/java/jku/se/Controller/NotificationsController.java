package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import jku.se.Utilities.NotificationManager;

public class NotificationsController {

    @FXML
    private ListView<String> notificationList;

    @FXML
    public void initialize() {
        System.out.println("DEBUG COUNT: " + NotificationManager.getInstance().getNotifications().size());

        notificationList.getItems().clear();
        for (jku.se.Notification notification : NotificationManager.getInstance().getNotifications()) {
            notificationList.getItems().add(notification.getMessage());
        }
    }



    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}

