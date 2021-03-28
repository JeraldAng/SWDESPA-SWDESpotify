package classes;

import iterator.Iterator;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Activities;
import models.Albums;
import models.Songs;

import java.util.ArrayList;

public class ActivitiesViewer {

    private TableView activitiesTv;
    private Iterator<Activities> activities;

    public ActivitiesViewer (TableView activitiesTv){

        this.activitiesTv = activitiesTv;

        TableColumn dateColumn = new TableColumn("Date");
        dateColumn.setPrefWidth(150);
        dateColumn.setCellValueFactory(new PropertyValueFactory<Activities, String>("date"));
        TableColumn activityColumn = new TableColumn("Activity");
        activityColumn.setPrefWidth(300);
        activityColumn.setCellValueFactory(new PropertyValueFactory<Activities, Label>("activity"));

        this.activitiesTv.getColumns().addAll(dateColumn, activityColumn);

    }

    public void setActivities(Iterator<Activities> activities){
        if(activities == null) return;
        this.activities = activities;
        refreshActivitiesList();
    }

    public void refreshActivitiesList(){
        activitiesTv.getItems().clear();
        if(activities != null){
            while(activities.hasNext()){
                activitiesTv.getItems().add(activities.next());
            }
        }
    }
}
