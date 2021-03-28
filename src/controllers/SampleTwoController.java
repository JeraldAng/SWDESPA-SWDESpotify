package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import views.Sample;
import views.SampleTwo;
import views.View;

public class SampleTwoController extends Controller {

    public SampleTwo sampleTwo;

    public SampleTwoController(View view) {
        super(view);

        sampleTwo = (SampleTwo) view;

        sampleTwo.backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sampleTwo.setScene(Sample.class);
            }
        });

        sampleTwo.sampleBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
    }
}
