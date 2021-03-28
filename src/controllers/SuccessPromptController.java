package controllers;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import views.ErrorPrompt;
import views.SuccessPrompt;
import views.View;

public class SuccessPromptController extends Controller {

    SuccessPrompt successPrompt;

    public SuccessPromptController(View view) {
        super(view);

        successPrompt = (SuccessPrompt) view;

        successPrompt.getRoot().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                successPrompt.remove();
            }
        });
    }
}
