package controllers;

import classes.SongPicker;
import iterator.FollowedUsersSongsCollection;
import iterator.Iterator;
import javafx.util.Callback;
import models.Follows;
import models.Session;
import models.Songs;
import models.Users;
import views.SongsView;
import views.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongsViewController extends Controller {

    private SongsView songsView;

    private SongPicker songPicker;

    private DashboardController parentController;

    public SongsViewController(View view) {
        super(view);

        songsView = (SongsView) view;

        boolean viewMode = false;

        HashMap<String, Object> data = view.getData();

        parentController = (DashboardController) songsView.getParent().getController();

        if(data.containsKey("view")){
            viewMode = (boolean) data.get("view");
        }

        HashMap<String, String> contextMenus = new HashMap<>();

        Users user = (Users) data.get("user");
        Users currentUser = (Users) Session.get("user");

        if(currentUser.user_type == 2) {
            contextMenus.put("add_to_playlist", "Add song to playlist");
        }

        contextMenus.put("queue", "Add to queue");

        songPicker = new SongPicker(songsView.songsTv, true, contextMenus);

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

        Iterator<Songs> songs = null;

        if(user != null){
            FollowedUsersSongsCollection followedUsersSongsCollection = new FollowedUsersSongsCollection(user);
            songs = followedUsersSongsCollection.getIterator();
        }

        songPicker.setSongList(songs);
    }
}
