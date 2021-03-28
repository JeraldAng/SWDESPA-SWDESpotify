package views;

import javafx.scene.control.Label;

public class ErrorPrompt extends View {

    public Label titleLb;
    public Label errorLb;

    @Override
    public void onShown() {
        super.onShown();
    }

    public void setErrorMessage(String title, String message){
        titleLb.setText(title);
        errorLb.setText(message);
    }


}
