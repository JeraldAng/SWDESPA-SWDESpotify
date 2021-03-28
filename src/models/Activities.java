package models;

import javafx.scene.control.Label;
import utils.StringUtils;

public class Activities extends Model {

    public Integer activity_id;
    public Integer user_id;
    public String description;
    public String date_created;

    @Override
    public String setPKField() {
        return "activity_id";
    }

    public String getDate(){
        return StringUtils.toPrettyDateTime(date_created);
    }

    public Label getActivity(){

        Label lb = new Label();

        lb.setPrefWidth(250);
        lb.setPrefHeight(40);

        lb.setWrapText(true);

        lb.setText(description);

        return lb;
    }
}
