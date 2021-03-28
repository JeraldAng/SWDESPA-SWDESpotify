package views;

import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.HashMap;

public class SignUp extends View
{
    public TextField usernameTf;
    public PasswordField passwordTf;
    public TextField emailTf;
    public DatePicker birthdateDp;
    public Button submitBtn;
    public Button backBtn;
    public RadioButton artistRb;
    public RadioButton listenerRb;

    public Pane signUpPane;

    @Override
    public void beforeShown() {
        setTitle("Not yet a member? Sign up now!");
    }

}
