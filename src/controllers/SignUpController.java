package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import models.Users;
import utils.Const;
import utils.SQLUtils;
import utils.StringUtils;
import views.*;

import java.util.HashMap;

public class SignUpController extends Controller {

    private SignUp signUp;
    private Users user;

    public SignUpController(View view) {
        super(view);

        signUp = (SignUp) view;

        signUp.submitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                user = new Users();

                user.user_type = signUp.artistRb.isSelected() ? 1 : 2;
                user.username = signUp.usernameTf.getText();
                user.password = Const.pp.hashPassword(signUp.passwordTf.getText());
                user.email = signUp.emailTf.getText();
                user.birth_date = StringUtils.toMySQLDate(signUp.birthdateDp.getValue());

                if(!user.create()){
                    String message = "";

                    if(user.getMessages().size() > 1){
                        message = String.join("\n", user.getMessages().toArray(new String[]{}));
                    }else if(user.getMessages().size() == 1){
                        if(user.getMessages().get(0).contains("Duplicate")){
                            message = "Username already taken.";
                        }else{
                            message = user.getMessages().get(0);
                        }
                    }
                    ErrorPrompt errorPrompt = (ErrorPrompt) signUp.addToPane(ErrorPrompt.class, (Pane)signUp.getRoot(), signUp, null);
                    errorPrompt.setErrorMessage("Sign up failed.", message);

                }else{
                    SuccessPrompt successPrompt = (SuccessPrompt) signUp.addToPane(SuccessPrompt.class, (Pane)signUp.getRoot(), signUp, null);
                    successPrompt.setMessage("Registration Successful!", "You may now login in to your account!");

                    successPrompt.onRemoveCallback(new Callback<Void, Void>() {
                        @Override
                        public Void call(Void param) {
                            signUp.setScene(Login.class);
                            return null;
                        }
                    });
                }
            }
        });

        signUp.backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                signUp.setScene(Login.class);
            }
        });
    }
}
