package classes;

import iterator.Iterator;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Notifications;

import java.util.ArrayList;

public class NotificationsViewer {

    private TableView notificationsTv;
    private Iterator<Notifications> notifications;

    public NotificationsViewer (TableView notificationsTv){

        this.notificationsTv = notificationsTv;

        TableColumn dateColumn = new TableColumn("Date");
        dateColumn.setPrefWidth(150);
        dateColumn.setCellValueFactory(new PropertyValueFactory<Notifications, String>("date"));
        TableColumn notification = new TableColumn("Notification");
        notification.setPrefWidth(300);
        notification.setCellValueFactory(new PropertyValueFactory<Notifications, Label>("notification"));

        this.notificationsTv.getColumns().addAll(dateColumn, notification);

    }

    public void setNotifications(Iterator<Notifications> notifications){
        if(notifications == null) return;
        this.notifications = notifications;
        refreshNotificationsList();
    }

    public void refreshNotificationsList(){
        notificationsTv.getItems().clear();
        if(notifications != null){
            while(notifications.hasNext()){
                notificationsTv.getItems().add(notifications.next());
            }
        }
    }
}
