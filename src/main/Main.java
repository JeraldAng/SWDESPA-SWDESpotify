package main;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import models.Session;
import models.Users;
import observer.ActivityObserver;
import observer.NotificationObserver;
import observer.Subject;
import sun.rmi.runtime.Log;
import utils.Const;
import utils.FileUploader;
import utils.GetMediaMetaData;
import utils.SQLUtils;
import views.Dashboard;
import views.Login;
import views.Sample;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

public class Main extends Application {

    private boolean testMode = false;

    @Override
    public void start(Stage primaryStage) throws Exception{

        if(!testMode) {
            setup();
            Login login = new Login();
            login.show();
        }else {
            setup();
            test(primaryStage);
        }


    }

    private void setup() {
        try {
            SQLUtils.addConnection("swedespotify_dc3", Const.host, Const.database, Const.username, Const.password);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void test(Stage primaryStage) throws IOException {

        /*

        array(
            'activities'=>[created...,update..],
            'notifications'=>[artist has uploaded...]
        )


         */
//
//        new ActivityObserver(users1){
//            @Override
//            public void update() {
//                subject.getState();
//            }
//        };
//        new NotificationObserver(users1){
//            @Override
//            public void update() {
//                subject.getState();
//            }
//        };
//
//        users1.setState();

        Users user = new Users();

        user = (Users) user.findOne("user_id = 31");

        Session.add("user", user);

        Dashboard dashboard = new Dashboard();
        dashboard.show();

        if(true) return;
//

//

//
//        FileUploader fileUploader = new FileUploader(new File("")){
//            @Override
//            public void onPostExecute(Object params) {
//                System.out.println(params);
//            }
//        };
//
//        fileUploader.execute("http://localhost/swdespotify/upload.php");

//        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Sample.fxml"));
//        primaryStage.setTitle("Hello World");

        Sample sample = new Sample();
        sample.show();

        String base = "http://localhost/swdespotify/songs/";

        Media media = new Media(base+ URLEncoder.encode("Heikousen - Rainych.mp3", "UTF-8").replaceAll("\\+", "%20"));

        //Media media = new Media("https://sample-videos.com/audio/mp3/wave.mp3");

        MediaPlayer player = new MediaPlayer(media);
        MediaView mediaView = new MediaView(player);

        ((Pane)sample.getScene().getRoot()).getChildren().add(mediaView);


        new GetMediaMetaData(media, mediaView, new Callback<GetMediaMetaData, Void>() {
            @Override
            public Void call(GetMediaMetaData param) {
                System.out.println(param.filePath);
                System.out.println(param.name);
                return null;
            }
        });

        player.setAutoPlay(true);
        player.play();

        player.setVolume(0.2);

        player.cycleDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                System.out.println(oldValue+">"+newValue);
            }
        });

        //Scene scene = new Scene(sample.getRoot(), 300, 275);


        //primaryStage.setScene(scene);

        //primaryStage.show();


        player.onHaltedProperty().addListener(new ChangeListener<Runnable>() {
            @Override
            public void changed(ObservableValue<? extends Runnable> observable, Runnable oldValue, Runnable newValue) {
                System.out.println("Halted");
            }
        });

        player.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                System.out.println("Playing");
            }
        });

        player.setOnStopped(new Runnable() {
            @Override
            public void run() {
                System.out.println("Stopped");
            }
        });

        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                System.out.println("Done Playing");
            }
        });

        player.onStalledProperty().addListener(new ChangeListener<Runnable>() {
            @Override
            public void changed(ObservableValue<? extends Runnable> observable, Runnable oldValue, Runnable newValue) {
                System.out.println("Stalled");
            }
        });

        player.setOnError(new Runnable() {
            @Override
            public void run() {
                System.out.println(player.getError());
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
