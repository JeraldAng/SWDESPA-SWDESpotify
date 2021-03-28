package controllers;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import views.Sample;
import views.SampleTwo;
import views.View;

import java.util.HashMap;

public class SampleController extends Controller {

    private Sample sample;

    public SampleController(View view) {
        super(view);

        sample = (Sample) view;

        sample.sampleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                HashMap<String, Object> data = new HashMap<>();
                //SampleTwo s = new SampleTwo();

                data.put("test", "hello");
                //sample.setScene(SampleTwo.class, data);

                sample.loadToPane(SampleTwo.class, sample.panel1, sample, null);

                //s.setData(data);
                //s.show();
            }
        });
    }
}
