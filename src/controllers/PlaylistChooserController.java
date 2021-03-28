package controllers;

import classes.PlaylistPicker;
import iterator.UsersPlaylistCollection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import models.Playlists;
import models.Users;
import views.PlaylistChooser;
import views.View;

import java.util.ArrayList;

public class PlaylistChooserController extends Controller {

    private PlaylistChooser playlistChooser;

    private PlaylistPicker playlistPicker;

    private Users user;

    public PlaylistChooserController(View view) {
        super(view);

        playlistChooser = (PlaylistChooser) view;

        playlistPicker = new PlaylistPicker(playlistChooser.playlistTv, null);

        user = (Users) view.getData().get("user");

        refreshPlaylist();

        playlistChooser.searchTf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refreshPlaylist();
            }
        });
    }

    private void refreshPlaylist(){
        UsersPlaylistCollection usersPlaylistCollection = new UsersPlaylistCollection(user, "%"+playlistChooser.searchTf.getText()+"%");
        playlistPicker.setPlaylistList(usersPlaylistCollection.getIterator());
    }
}
