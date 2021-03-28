package controllers;

import classes.FilePicker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import models.Albums;
import models.Session;
import models.Users;
import utils.AsyncTask;
import utils.Const;
import utils.FileUploader;
import views.AlbumForm;
import views.View;

import java.awt.*;
import java.io.File;
import java.util.List;

public class AlbumFormController extends Controller {

    private AlbumForm albumForm;
    private FilePicker filePicker;

    private File imageFile;
    private String toSaveName = "";

    public AlbumFormController(View view) {
        super(view);

        albumForm = (AlbumForm) view;

        filePicker = new FilePicker("Add Songs", albumForm.getRoot());
        filePicker.addFilter("JPEG (*.jpg)", "*.jpg");
        filePicker.addFilter("PNG (*.png)", "*.png");

         albumForm.albumCoverBtn.setOnAction(new EventHandler<ActionEvent>() {
             @Override
             public void handle(ActionEvent event) {
                 imageFile = filePicker.chooseOne();
                 if(imageFile != null) {
                     albumForm.albumCoverIv.setImage(new Image(imageFile.toURI().toString()));
                 }
             }
         });

        albumForm.createAlbumBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(imageFile != null) {
                    AsyncTask task = new FileUploader(imageFile){
                        @Override
                        public void onPostExecute(Object params) {
                            toSaveName = params.toString();
                            save();
                        }
                    };
                    task.execute(Const.httpBase+"/upload_image.php");
                }else{
                    toSaveName = "";
                    save();
                }

            }
        });

    }

    private void save(){
        Albums albums = new Albums();

        Users user = (Users) Session.get("user");

        albums.album_name = albumForm.albumNameTf.getText();
        albums.user_id = user.user_id;

        if(toSaveName.isEmpty()){
            toSaveName = null;
        }

        albums.album_cover = toSaveName;

        if(!albums.create()){
            if(albums.album_name == null || albums.album_name.isEmpty()){
                albumForm.albumNameTf.requestFocus();
            }
        }else {
            albumForm.setExitCode(1);
            albumForm.close();
        }

    }
}
