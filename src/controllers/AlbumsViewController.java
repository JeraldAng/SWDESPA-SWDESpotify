package controllers;

import classes.AlbumPicker;
import classes.SongPicker;
import iterator.AlbumSongsCollection;
import iterator.FollowedUsersAlbumsCollection;
import iterator.Iterator;
import javafx.util.Callback;
import models.*;
import views.AlbumsView;
import views.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlbumsViewController extends Controller {

    private AlbumsView albumsView;
    private DashboardController parentController;

    public AlbumsViewController(View view) {

        super(view);

        albumsView = (AlbumsView) view;

        parentController = (DashboardController) albumsView.getParent().getController();

        boolean viewMode = false;
        HashMap<String, Object> data = view.getData();

        viewMode = (boolean) data.get("view");

        Users user = (Users) data.get("user");
        Users currentUser = (Users) Session.get("user");

        AlbumPicker albumPicker = new AlbumPicker(albumsView.albumsTv, null);

        FollowedUsersAlbumsCollection followedUsersAlbumsCollection = new FollowedUsersAlbumsCollection(user);
        albumPicker.setAlbumsList(followedUsersAlbumsCollection.getIterator());

        HashMap<String, String> contextMenus = new HashMap<>();

        if(currentUser.user_type == 2) {
            contextMenus.put("add_to_playlist", "Add song to playlist");
        }

        contextMenus.put("queue", "Add to queue");

        SongPicker songPicker = new SongPicker(albumsView.songsTv, true, contextMenus);

        songPicker.setOnSongDoubleClick(new Callback<Songs, Void>() {
            @Override
            public Void call(Songs param) {
                parentController.addSongToQueue(param);
                parentController.playQueueSong(param);
                return null;
            }
        });

        songPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Songs>, Void>() {
            @Override
            public Void call(Map.Entry<String, Songs> param) {

                switch (param.getKey()){
                    case "queue":
                        if(param.getKey().equals("queue")) {
                            ArrayList<Songs> songs = songPicker.getSelectedSongs();

                            if(songs.size() > 0){
                                for(Songs song : songs){
                                    parentController.addSongToQueue(song);
                                }
                            }
                        }
                        break;
                    case "add_to_playlist":
                        ArrayList<Songs> songs = songPicker.getSelectedSongs();
                        parentController.addSongsToPlaylist(songs);
                        break;
                }
                return null;
            }
        });

        albumPicker.setOnSelectedAlbumCallback(new Callback<Albums, Void>() {
            @Override
            public Void call(Albums param) {
                // TODO: 13/04/2019 change to iterator
                Albums album = albumPicker.getSelectedAlbum();
                Iterator<Songs> songs = null;
                if(album != null){
                    AlbumSongsCollection albumSongsCollection = new AlbumSongsCollection(album);
                    songs = albumSongsCollection.getIterator();
                }
                songPicker.setSongList(songs);
                return null;
            }
        });
    }
}
