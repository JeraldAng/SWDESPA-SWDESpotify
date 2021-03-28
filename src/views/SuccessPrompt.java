package views;

import javafx.scene.control.Label;

public class SuccessPrompt extends View {

    public Label titleLb;
    public Label messageLb;

    public void setMessage(String title, String message){
        titleLb.setText(title);
        messageLb.setText(message);
    }
}
