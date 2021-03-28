package views;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class Login extends View {

    public TextField usernameTf;
    public PasswordField passwordTf;
    public Button signInBtn;
    public Button signUpBtn;
    public Pane loginPane;

    @Override
    public void beforeShown() {
        setTitle("Welcome to SWDESPOTIFY!");
    }
}
