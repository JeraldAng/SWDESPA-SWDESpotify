package controllers;

import classes.SongPicker;
import iterator.AllSongsNotInPlaylistCollection;
import iterator.Iterator;
import iterator.SongsNotInPlaylistCollection;
import iterator.UserSongsNoAlbumCollection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import models.Playlists;
import models.Songs;
import models.Users;
import views.SongChooser;
import views.View;

import java.util.ArrayList;
import java.util.HashMap;

public class SongChooserController extends Controller {

    private SongChooser songChooser;
    private SongPicker songPicker;

    private Integer type;
    private Users user;
    private Playlists playlist;

    public SongChooserController(View view) {
        super(view);

        songChooser = (SongChooser) view;

        HashMap<String,Object> data = songChooser.getData();

        type = (Integer) data.get("type");

        if(type == 1){
            user = (Users) data.get("user");
        }else if(type == 2){
            user = (Users) data.get("user");
            playlist = (Playlists) data.get("playlist");
        }else if(type == 3){
            playlist = (Playlists) data.get("playlist");
        }

        songPicker = new SongPicker(songChooser.songListTv, true);

        songChooser.searchTf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refreshSongsList();
            }
        });

        refreshSongsList();
    }

    private void refreshSongsList(){
        Iterator<Songs> songs = null;
        if(type == 1){
            UserSongsNoAlbumCollection userSongsNoAlbumCollection = new UserSongsNoAlbumCollection(user, "%"+songChooser.searchTf.getText()+"%");
            songs = userSongsNoAlbumCollection.getIterator();
        }else if(type == 2){
            SongsNotInPlaylistCollection songsNotInPlaylistCollection = new SongsNotInPlaylistCollection(user, playlist, "%"+songChooser.searchTf.getText()+"%");
            songs = songsNotInPlaylistCollection.getIterator();
        }else if(type == 3){
            AllSongsNotInPlaylistCollection allSongsNotInPlaylistCollection = new AllSongsNotInPlaylistCollection(playlist, "%"+songChooser.searchTf.getText()+"%");
            songs = allSongsNotInPlaylistCollection.getIterator();
        }
        songPicker.setSongList(songs);
    }


}
