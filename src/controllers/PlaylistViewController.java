package controllers;

import classes.PlaylistPicker;
import classes.SongPicker;
import iterator.FollowedUsersPlaylistsCollection;
import iterator.Iterator;
import iterator.PlaylistSongsCollection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Callback;
import models.*;
import views.PlaylistView;
import views.SongChooser;
import views.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlaylistViewController extends Controller {

    private PlaylistView playlistView;
    private DashboardController parentController;

    public PlaylistViewController(View view) {
        super(view);

        playlistView = (PlaylistView) view;

        parentController = (DashboardController) playlistView.getParent().getController();

        boolean viewMode = false;
        HashMap<String, Object> data = view.getData();

        viewMode = (boolean) data.get("view");

        Users user = (Users) data.get("user");
        Users currentUser = (Users) Session.get("user");

        HashMap<String, String> contextMenus = new HashMap<>();

        contextMenus.put("add_to_queue", "Add Playlist to queue");

        if(currentUser.user_type == 2) {
            contextMenus.put("highlight", "Highlight");
        }

        PlaylistPicker playlistPicker = new PlaylistPicker(playlistView.playlistsTv, contextMenus);

        ArrayList<Playlists> playlists = new ArrayList<>();


        FollowedUsersPlaylistsCollection followedUsersPlaylistsCollection = new FollowedUsersPlaylistsCollection(user);

        playlistPicker.setPlaylistList(followedUsersPlaylistsCollection.getIterator());

        playlistPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Playlists>, Void>() {
            @Override
            public Void call(Map.Entry<String, Playlists> param) {
                Playlists playlist = param.getValue();
                switch (param.getKey()){
                    case "add_to_queue":
                        Playlists playlists = playlistPicker.getSelectedPlaylist();
                        PlaylistSongsCollection playlistSongsCollection = new PlaylistSongsCollection(playlist);

                        Iterator<Songs> songsIterator = playlistSongsCollection.getIterator();
                        while(songsIterator.hasNext()){
                            parentController.addSongToQueue(songsIterator.next());
                        }
//
//                        ArrayList<PlaylistSongs> playlistSongs = (ArrayList<PlaylistSongs>) playlists.get(PlaylistSongs.class);
//                        if (playlistSongs != null) {
//                            for (PlaylistSongs playlistSong : playlistSongs) {
//                                // TODO: 4/8/2019 check if song is null add error handling
//                               (Songs) playlistSong.get(Songs.class));
//                            }
//                        }
                        break;
                    case "highlight":
                        parentController.addToHighlights(param.getValue());
                        break;
                }
                return null;
            }
        });


        contextMenus = new HashMap<>();

        if(currentUser.user_type == 2) {
            contextMenus.put("add_to_playlist", "Add song to playlist");
        }

        contextMenus.put("queue", "Add to queue");

        SongPicker songPicker = new SongPicker(playlistView.songsTv, true, contextMenus);
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

        playlistPicker.setOnSelectedPlaylistCallback(new Callback<Playlists, Void>() {
            @Override
            public Void call(Playlists playlists) {
                // TODO: 13/04/2019 change to iterator 
                Iterator<Songs> songs = null;
                if(playlists != null) {
                    PlaylistSongsCollection playlistSongsCollection = new PlaylistSongsCollection(playlists);
                    songs = playlistSongsCollection.getIterator();
                }
                songPicker.setSongList(songs);
                return null;
            }
        });
    }
}
