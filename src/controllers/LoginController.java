package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import models.Session;
import models.Users;
import utils.Const;
import views.*;

import java.util.ArrayList;

public class LoginController extends Controller {

    private views.Login login;

    public LoginController(View view) {
        super(view);

        login = (Login) view;

        login.signInBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Users user = new Users();

                ArrayList<Object> bind = new ArrayList<>();

                bind.add(login.usernameTf.getText());
                bind.add(Const.pp.hashPassword(login.passwordTf.getText()));

                user = (Users) user.findOne("username LIKE ? AND password LIKE ?", bind);

                if(user != null){
                    Session.add("user", user);
                    login.setScene(Dashboard.class);
                }else{
                    ErrorPrompt errorPrompt = (ErrorPrompt) login.addToPane(ErrorPrompt.class, (Pane)login.getRoot(), login, null);
                    errorPrompt.setErrorMessage("Sorry, we couldn't log you in.", "You have provided incorrect login credentials. Please try again.");
                }

            }
        });

        login.signUpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                login.setScene(SignUp.class);
            }
        });
    }
}
