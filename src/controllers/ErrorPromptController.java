package controllers;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import views.ErrorPrompt;
import views.View;

public class ErrorPromptController extends Controller {

    private ErrorPrompt errorPrompt;

    public ErrorPromptController(View view) {
        super(view);

        errorPrompt = (ErrorPrompt) view;

        errorPrompt.getRoot().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                errorPrompt.remove();
            }
        });
    }
}
