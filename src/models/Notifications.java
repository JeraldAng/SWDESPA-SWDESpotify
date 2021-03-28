package models;

import javafx.scene.control.Label;
import utils.StringUtils;

public class Notifications extends Model {

    public Integer notification_id;
    public Integer user_id;
    public String description;
    public String date_created;
    public Integer seen;

    @Override
    public String setPKField() {
        return "notification_id";
    }

    public String getDate(){
        return StringUtils.toPrettyDateTime(date_created);
    }

    public Label getNotification(){

        Label lb = new Label();

        lb.setPrefWidth(250);
        lb.setPrefHeight(40);

        lb.setWrapText(true);

        lb.setText(description);

        return lb;
    }
}
