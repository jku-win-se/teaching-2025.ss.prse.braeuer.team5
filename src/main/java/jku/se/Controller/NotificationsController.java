package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import jku.se.Utilities.NotificationManager;

/**
 * Controller for displaying user notifications in a ListView.
 * Notifications are loaded from the NotificationManager singleton.
 */
public class NotificationsController {

    @FXML
    private ListView<String> notificationList;

    /**
     * Initializes the controller and loads all notifications into the ListView.
     */
    @FXML
    public void initialize() {
        notificationList.getItems().clear();
        for (jku.se.Notification notification : NotificationManager.getInstance().getNotifications()) {
            notificationList.getItems().add(notification.getMessage());
        }
    }

    /**
     * Closes the notification window.
     * @param event the action event that triggered the close
     */
    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}

