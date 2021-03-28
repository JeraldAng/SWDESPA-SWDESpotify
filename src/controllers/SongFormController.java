package controllers;

import iterator.Iterator;
import iterator.UserAlbumsCollection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import models.Albums;
import models.Session;
import models.Songs;
import models.Users;
import views.SongForm;
import views.View;

import java.util.ArrayList;
import java.util.HashMap;

public class SongFormController extends Controller {

    private SongForm songForm;
    private ArrayList<Integer> albumIDs;

    public SongFormController(View view) {
        super(view);

        songForm = (SongForm) view;

        HashMap<String, Object> data = view.getData();

        Songs song = (Songs) data.get("song");

        songForm.nameTf.setText(song.name);

        if(song.album_id == 0){
            song.album_id = null;
        }

        if(song.genre != null) {
            songForm.genreTf.setText(song.genre);
        }else{
            songForm.genreTf.setText("");
        }

        Users currentUser = (Users) Session.get("user");

        UserAlbumsCollection userAlbumsCollection = new UserAlbumsCollection(currentUser, "%%");

        Iterator<Albums> albumsIterator = userAlbumsCollection.getIterator();

        albumIDs = new ArrayList<>();

        while (albumsIterator.hasNext()){
            Albums album = albumsIterator.next();
            songForm.albumCb.getItems().add(album.album_name);
            albumIDs.add(album.album_id);
        }

        if(albumIDs.size() > 0) {
            songForm.albumCb.getSelectionModel().select(albumIDs.indexOf(song.album_id));
        }



//        if(song.album_name != null) {
//            songForm.albumTf.setText(song.album_name);
//        }else{
//            songForm.albumTf.setText("");
//        }

        if(song.year != null) {
            songForm.yearTf.setText(song.year);
        }else{
            songForm.yearTf.setText("");
        }

        songForm.saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                song.name = songForm.nameTf.getText();
                song.genre = songForm.genreTf.getText();
                //song.album_name = songForm.albumTf.getText();
                if(songForm.albumCb.getSelectionModel().getSelectedIndex() != -1){
                    song.album_id = albumIDs.get(songForm.albumCb.getSelectionModel().getSelectedIndex());
                }
                song.year = songForm.yearTf.getText();

                // TODO: 09/04/2019 add error checking and message
                if(song.save()){
                    songForm.close();
                }
            }
        });


    }
}
