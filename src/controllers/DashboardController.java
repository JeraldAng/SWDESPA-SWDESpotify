package controllers;

import builder.MusicManager;
import classes.*;
import iterator.*;
import iterator.Iterator;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import javafx.util.Duration;
import models.*;
import observer.ActivityObserver;
import observer.FollowersObserver;
import observer.NotificationObserver;
import observer.Subject;
import utils.*;
import views.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

public class DashboardController extends Controller {

    private PlaylistPicker highlightsPicker;
    private Dashboard dashboard;
    private Users user;

    private FilePicker filePicker;
    private SongPicker songPicker;
    private SongPicker queueSongPicker;
    private SongPicker likedSongPicker;

    private AlbumPicker albumPicker;
    private SongPicker albumSongs;

    private PlaylistPicker playlistPicker;
    private Playlists currentSelectedPlaylist;
    private SongPicker playlistSongPicker;

    private ArtistsPicker artistsPicker;
    private ListenerPicker listenerPicker;

    private ActivitiesViewer activitiesViewer;
    private NotificationsViewer notificationsViewer;

    private LinkedHashMap<Integer, Songs> queueSongsList = new LinkedHashMap<>();
    private LinkedHashMap<Integer, Songs> likedSongsList = new LinkedHashMap<>();

    private MusicManager musicManager;

    private boolean viewMode = false;
    private Integer userType = 0;
    private Integer currentUserType = 0;

    private Dashboard parentDashboard;
    private DashboardController parentDashboardController;

    private Users currentUser;

    private Subject userSubject;

    private MediaPlayer player;

    private PauseTransition wait = new PauseTransition(Duration.seconds(5));
    private Songs currentSong;

    public DashboardController(View view) {
        super(view);


        Media sound = null;
        try {
            sound = new Media(getClass().getResource("/resources/sounds/NotificationSound-Discord.mp3").toURI().toString());
            player = new MediaPlayer(sound);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        dashboard = (Dashboard) view;

        HashMap<String, Object> viewData = dashboard.getData();

        if(viewData != null){
            user = (Users) viewData.get("user");
            currentUser = (Users) Session.get("user");
            currentUserType = currentUser.user_type;
            viewMode = true;
        }else{
            user = (Users) Session.get("user");
            currentUser = user;
            currentUserType = currentUser.user_type;
        }

        userType = user.user_type;

        dashboard.setTitle("Welcome to SWDESpotify "+user.username+"! :)");

        dashboard.followersCountLb.setText(user.getFollowers().toString());

        if(userType == 1){
            //dashboard.profileTabPane.getTabs().remove(1);
            dashboard.searchTabPane.getTabs().remove(1);
            dashboard.profileTabPane.getTabs().remove(4);
            dashboard.profileTabPane.getTabs().remove(3);
            dashboard.userTabPane.getTabs().remove(1);
        }else if(userType == 2){
            dashboard.userTabPane.getTabs().remove(3);
            dashboard.userTabPane.getTabs().remove(3);
        }

        dashboard.emailLb.setText(user.email);
        dashboard.bdayLb.setText(StringUtils.toPrettyDate(user.birth_date));

        //VIEW MODE
        if(viewMode){
            dashboard.usernameLb.setText(user.username+"'s Profile");

            parentDashboard = (Dashboard) dashboard.getParent();
            parentDashboardController = (DashboardController) parentDashboard.getController();

            dashboard.addSongBtn.setVisible(false);
            dashboard.newAlbumBtn.setVisible(false);
            dashboard.newPlaylistBtn.setVisible(false);
            dashboard.logoutBtn.setVisible(false);

            if(userType == 1) {
                dashboard.userTabPane.getTabs().remove(5);
                dashboard.userTabPane.getTabs().remove(1);
            }else if(userType == 2){
                dashboard.userTabPane.getTabs().remove(4);
                dashboard.userTabPane.getTabs().remove(2);
                dashboard.userTabPane.getTabs().remove(1);
            }

            dashboard.setTitle("You are now viewing the profile of "+user.username);

            if(user.getFollowStatusBool()) {
                dashboard.followBtn.setVisible(false);
                dashboard.unfollowBtn.setVisible(true);
            }else{
                dashboard.followBtn.setVisible(true);
                dashboard.unfollowBtn.setVisible(false);
            }

            dashboard.followBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(user.follow()){

                        if(viewMode) {
                            dashboard.followersCountLb.setText(user.getFollowers().toString());
                        }

                        dashboard.followBtn.setVisible(false);
                        dashboard.unfollowBtn.setVisible(true);
                        parentDashboardController.refreshArtists();
                        parentDashboardController.refreshListeners();

                        String followed = currentUser.username+" has followed "+(user.user_type == 1 ? "artist" : "listener")+" "+user.username+".";

                        Notifications notifications = new Notifications();
                        notifications.description = followed;
                        notifications.user_id = user.user_id;
                        notifications.create();

                        parentDashboardController.addActivity(followed);
                        parentDashboardController.notifyFollowers(followed, 2, user);
                        parentDashboardController.refreshFollowsPane();
                    }else{
                        // TODO: 4/10/2019 add error handling
                    }
                }
            });

            dashboard.unfollowBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    user.unfollow();

                    if(viewMode) {
                        dashboard.followersCountLb.setText(user.getFollowers().toString());
                    }

                    dashboard.followBtn.setVisible(true);
                    dashboard.unfollowBtn.setVisible(false);
                    parentDashboardController.refreshArtists();
                    parentDashboardController.refreshListeners();
                    parentDashboardController.refreshFollowsPane();
                }
            });
        }else{
            dashboard.usernameLb.setText("My Profile");
            dashboard.followBtn.setVisible(false);
            dashboard.unfollowBtn.setVisible(false);
        }

        dashboard.logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(musicManager != null){
                    wait.stop();
                    if(player != null){
                        player.stop();
                        player.dispose();
                        player = null;
                    }
                    musicManager.disposePlayer();
                    musicManager.resetCurrentSong();
                    dashboard.setScene(Login.class);
                }
            }
        });

        dashboard.userTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if(newValue.getText().contains("Notifications")){
                    user.seen();
                    userSubject.getState().replace("unseenNotifications", null);
                    userSubject.setState(userSubject.getState());
                }
            }
        });

        dashboard.searchSongsTf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refreshUserSongs();
            }
        });

        filePicker = new FilePicker("Add Songs", dashboard.getRoot());
        filePicker.addFilter("MP3 Files (*.mp3)", "*.mp3");


        HashMap<String, String> contextMenus = new HashMap<>();

        contextMenus.put("delete", "Remove from queue");
        if (userType == 2)
            contextMenus.put("add_to_likes", "Like this song");

        musicManager = new MusicManager.MusicManagerBuilder(dashboard.mediaView)
                .withVolumeScrubber(dashboard.volumeScrubber)
                .withSongScrubber(dashboard.songScrubber)
                .withPlayButton(dashboard.playBtn)
                .withPauseButton(dashboard.pauseBtn)
                .withRepeatButton(dashboard.repeatBtn)
                .withShuffleButton(dashboard.shuffleBtn)
                .withNextButton(dashboard.nextBtn)
                .withPreviousButton(dashboard.prevBtn)
                .withSongTitleLabel(dashboard.songName2Lb)
                .withCurrentPlayBackTimeLabel(dashboard.currentTimeLb)
                .withTotalPlayBackTimeLabel(dashboard.totalTimeLb)
                .withAlbumCoverIV(dashboard.albumCover2Iv)
                .withRepeatOneButton(dashboard.repeatOneBtn)
                .withSongPicker(dashboard.queueSongTv, contextMenus)
                .withHTTP(Const.httpBase+"/songs/")
                .buildMusicManager();

        musicManager.setVolume(.2);

        musicManager.onLastSongFinished(new Callback<Void, Void>() {
            @Override
            public Void call(Void param) {
                resetCurrentPlaying();
                return null;
            }
        });

        musicManager.onPlay(new Callback<Songs, Void>() {
            @Override
            public Void call(Songs song) {
                String songPlayed = currentUser.username + " played the song " + song.name + ".";
                currentSong = song;
                notifyFollowers(songPlayed, 2, null);
                addActivity(songPlayed);
                dashboard.songNameLb.setText(song.name);
                dashboard.albumLb.setText(song.getAlbum());
                dashboard.artistLb.setText(song.getArtist());
                dashboard.artistLb2.setText(song.getArtist());
                return null;
            }
        });

        queueSongPicker = musicManager.getSongPicker();

//        queueSongPicker.setOnSongSelected(new Callback<Songs, Void>() {
//            @Override
//            public Void call(Songs param) {
//                playQueueSong(param);
//                return null;
//            }
//        });

        queueSongPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Songs>, Void>() {
            @Override
            public Void call(Map.Entry<String, Songs> param) {
                Songs song = param.getValue();
                switch (param.getKey()){
                    case "add_to_likes":
                        addSongToLikes(param.getValue());
                        break;
                    case "delete":
                        if(queueSongsList.containsKey(song.song_id)){
                            queueSongsList.remove(song.song_id);
                        }
                        if(currentSong != null && currentSong == song){
                            musicManager.resetCurrentSong();
                            musicManager.disposePlayer();
                            resetCurrentPlaying();
                        }
                        refreshQueueSongs();
                        break;
                }
                return null;
            }
        });

        queueSongPicker.setOnSongSelected(new Callback<Songs, Void>() {
            @Override
            public Void call(Songs param) {
                playQueueSong(param);
                return null;
            }
        });

        contextMenus = new HashMap<>();
        if(!viewMode) {
            contextMenus.put("delete", "Delete");
            contextMenus.put("edit", "Edit");
        }else{
            if(currentUserType == 2){
                contextMenus.put("add_to_playlist", "Add song to playlist");
            }
        }
        contextMenus.put("queue", "Add to queue");

        songPicker = new SongPicker(dashboard.songsListTv, true, contextMenus);

        songPicker.setOnSongDoubleClick(new Callback<Songs, Void>() {
            @Override
            public Void call(Songs param) {
                addSongToQueue(param);
                playQueueSong(param);
                return null;
            }
        });

        songPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Songs>, Void>() {
            @Override
            public Void call(Map.Entry<String, Songs> param) {
                if(param.getKey().equals("queue")) {
                    ArrayList<Songs> songs = songPicker.getSelectedSongs();

                    if(songs.size() > 0){
                        for(Songs song : songs){
                            addSongToQueue(song);
                        }
                    }
                    //refreshQueueSongs();
                }else if(param.getKey().equals("edit")){

                    SongForm songForm = new SongForm();
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("song", param.getValue());
                    songForm.setData(data);
                    songForm.showDialog(dashboard);

                    songForm.setOnClose(new Callback<Integer, Void>() {
                        @Override
                        public Void call(Integer param) {
                            refreshAllSongs();
                            return null;
                        }
                    });
                }else if(param.getKey().equals("delete")){
                    param.getValue().delete();
                    refreshAllSongs();
                }else if(param.getKey().equals("add_to_playlist")){
                    ArrayList<Songs> songs = songPicker.getSelectedSongs();
                    addSongsToPlaylist(songs);
                }
                return null;
            }
        });

        refreshUserSongs();

        dashboard.albumCover2Iv.imageProperty().addListener(new ChangeListener<Image>() {
            @Override
            public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {
                dashboard.albumCoverIv.setImage(observable.getValue());
            }
        });

        dashboard.addSongBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                List<File> files = filePicker.choose();

                if(files == null) return;

                List<Void> uploaded = new ArrayList<>();

                for(File file : files){
                    new GetMediaMetaData(file, dashboard.mediaView, new Callback<GetMediaMetaData, Void>() {
                        @Override
                        public Void call(GetMediaMetaData param) {
                            new FileUploader(file){
                                @Override
                                public void onPostExecute(Object params) {
                                    super.onPostExecute(params);
                                    System.out.println(params);
                                    uploaded.add(null);

                                    Songs song = new Songs();

                                    song.name = param.name;
                                    song.genre = param.genre;
//                                    song.album_name = param.album;
                                    song.album_name = param.name;
                                    song.year = param.year;

                                    String songUploaded = currentUser.username+" has uploaded the song "+song.name+".";

                                    addActivity(songUploaded);

                                    notifyFollowers(songUploaded, 2, null);

                                    song.location = StringUtils.urlEncode(params.toString());

                                    song.user_id = user.user_id;

                                    if(!song.create()){
                                        System.out.println(song.getMessages());
                                    }

                                    if(uploaded.size() == files.size()) {
                                        System.out.println("done!");
                                    }

                                    refreshUserSongs();
                                }
                            }.execute(Const.httpBase+"/upload.php");
                            return null;
                        }
                    });
                }
            }
        });

        dashboard.newAlbumBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AlbumForm albumForm = new AlbumForm();
                albumForm.setOnClose(new Callback<Integer, Void>() {
                    @Override
                    public Void call(Integer exitCode) {
                        if(exitCode == 1){
                            String albumCreate = currentUser.username+" has created an album called "+albumForm.albumNameTf.getText()+".";
                            addActivity(albumCreate);
                            notifyFollowers(albumCreate, 2, null);
                            refreshUserAlbums();
                        }
                        return null;
                    }
                });
                albumForm.showDialog(dashboard);
            }
        });

        dashboard.searchAlbumsTf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refreshUserAlbums();
            }
        });

        contextMenus = new HashMap<>();

        if(!viewMode) {
            //contextMenus.put("edit", "Edit");
            contextMenus.put("delete", "Delete");
            contextMenus.put("add_songs", "Add Songs");
        }

        albumPicker = new AlbumPicker(dashboard.albumTv, contextMenus);

        albumPicker.setOnSelectedAlbumCallback(new Callback<Albums, Void>() {
            @Override
            public Void call(Albums param) {
                refreshAlbumSongs();
                return null;
            }
        });

        refreshUserAlbums();

        albumPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Albums>, Void>() {
            @Override
            public Void call(Map.Entry<String, Albums> param) {

                switch (param.getKey()){
                    case "delete":
                        param.getValue().delete();
                        refreshUserAlbums();
                        break;
                    case "add_songs":
                        SongChooser songChooser = new SongChooser();
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("type", 1);
                        data.put("user", user);

                        // TODO: 4/8/2019 change to iterator pattern

                        songChooser.setData(data);
                        songChooser.showDialog(dashboard);

                        songChooser.selectBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Songs[] songsArray = (Songs[]) songChooser.songListTv.getSelectionModel().getSelectedItems().toArray(new Songs[]{});
                                ArrayList<Songs> songs = new ArrayList<>(Arrays.asList(songsArray));

                                if(songs.size() > 0){
                                    // TODO: 4/8/2019 add error handling
                                    for(Songs song : songs){
                                        song.album_id = param.getValue().album_id;
                                        song.save();

                                        AlbumSongs albumSong = new AlbumSongs();
                                        albumSong.album_id = param.getValue().album_id;
                                        albumSong.song_id = song.song_id;

                                        String addAlbumSong = currentUser.username+" has added "+song.name+" to album "+param.getValue().album_name+".";
                                        addActivity(addAlbumSong);
                                        notifyFollowers(addAlbumSong, 2, null);

                                        albumSong.create();
                                    }
                                    refreshAllSongs();
                                    songChooser.close();
                                }
                            }
                        });

                        break;
                }
                return null;
            }
        });

        contextMenus = new HashMap<>();

        contextMenus.put("add_to_queue", "Add to queue");

        if(!viewMode) {
            contextMenus.put("delete", "Remove from album");
        }else{
            if(userType == 1 && currentUserType == 2){
                contextMenus.put("add_to_playlist", "Add song to playlist");
            }
        }

        albumSongs = new SongPicker(dashboard.albumSongsTv, false, contextMenus);

        albumSongs.setContextMenuCallBack(new Callback<Map.Entry<String, Songs>, Void>() {
            @Override
            public Void call(Map.Entry<String, Songs> param) {
                switch (param.getKey()){
                    case "delete":
                        ArrayList<Object> bind = new ArrayList<>();
                        bind.add(param.getValue().album_id);
                        bind.add(param.getValue().song_id);
                        AlbumSongs albumSong = (AlbumSongs) new AlbumSongs().findOne("album_id = ? AND song_id = ?", bind);

                        if(albumSong != null){
                            // TODO: 4/8/2019 add error handling
                            if(albumSong.delete()){
                                param.getValue().album_id = null;
                                if(param.getValue().save()) {
                                    refreshAllSongs();
                                }
                            }
                        }
                        break;
                    case "add_to_queue":
                        addSongToQueue(param.getValue());
                        break;
                    case "add_to_playlist":
                        ArrayList<Songs> songs = new ArrayList<>();
                        songs.add(param.getValue());
                        addSongsToPlaylist(songs);
                        break;
                    /*case "add_to_likes":
                        addSongToLikes(param.getValue());*/
                }
                return null;
            }
        });

        albumSongs.setOnSongDoubleClick(new Callback<Songs, Void>() {
            @Override
            public Void call(Songs param) {
                addSongToQueue(param);
                playQueueSong(param);
                return null;
            }
        });

        dashboard.newPlaylistBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PlaylistForm playlistForm = new PlaylistForm();
                playlistForm.showDialog(dashboard);

                playlistForm.createBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Playlists playlists = new Playlists();
                        playlists.user_id = user.user_id;
                        playlists.name = playlistForm.playlistNameTf.getText();

                        if(playlists.create()){

                            String playlistCreate = currentUser.username+" has created a playlist called "+playlists.name+".";

                            addActivity(playlistCreate);
                            notifyFollowers(playlistCreate, 2, null);

                            refreshUserPlaylist();
                            playlistForm.close();
                        }else{
                            // TODO: 4/8/2019 add error message
                            System.out.println(playlists.getMessages());
                        }
                    }
                });
            }
        });

        contextMenus = new HashMap<>();

        if(!viewMode) {
            //contextMenus.put("edit", "Edit");
            contextMenus.put("add_songs", "Add Songs");
            contextMenus.put("delete", "Delete playlist");
        }

        if(currentUser.user_type == 2){
            contextMenus.put("highlight", "Highlight");
        }

        contextMenus.put("add_to_queue", "Add Playlist to queue");


        playlistPicker = new PlaylistPicker(dashboard.playlistTv, contextMenus);

        playlistPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Playlists>, Void>() {
            @Override
            public Void call(Map.Entry<String, Playlists> param) {
                Playlists playlist = param.getValue();
                switch (param.getKey()){
                    case "delete":
                        if(playlist.delete()){
                            refreshUserPlaylist();
                            refreshPlaylistSongs();
                            refreshHighlights();
                        }
                        break;
                    case "add_songs":
                        SongChooser songChooser = new SongChooser();
                        HashMap<String, Object> data = new HashMap<>();

                        data.put("type", 2);
                        data.put("user", user);

                        data.put("playlist", playlist);

                        if(currentUserType == 2){
                            data.replace("type", 3);
                        }

                        // TODO: 4/8/2019 change to iterator pattern

                        songChooser.setData(data);
                        songChooser.showDialog(dashboard);

                        songChooser.selectBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Songs[] songsArray = (Songs[]) songChooser.songListTv.getSelectionModel().getSelectedItems().toArray(new Songs[]{});
                                ArrayList<Songs> songs = new ArrayList<>(Arrays.asList(songsArray));

                                if(songs.size() > 0){
                                    // TODO: 4/8/2019 add error handling
                                    for(Songs song : songs){

                                        PlaylistSongs playlistSong = new PlaylistSongs();

                                        playlistSong.playlist_id = playlist.playlist_id;
                                        playlistSong.song_id = song.song_id;

                                        String addPlaylistSong = currentUser.username+" has added "+song.name+" to playlist "+playlist.name+".";

                                        addActivity(addPlaylistSong);
                                        notifyFollowers(addPlaylistSong, 2, null);

                                        playlistSong.create();
                                    }
                                    refreshPlaylistSongs();
                                    songChooser.close();
                                }
                            }
                        });
                        break;
                    case "edit":
                        // TODO: 4/8/2019 edit for playlist
                        break;
                    case "add_to_queue":
                        Playlists playlists = playlistPicker.getSelectedPlaylist();

                        PlaylistSongsCollection playlistSongsCollection = new PlaylistSongsCollection(playlists);
                        Iterator<Songs> playlistSongs = playlistSongsCollection.getIterator();

                        while(playlistSongs.hasNext()){
                            addSongToQueue(playlistSongs.next());
                        }

                        break;
                    case "highlight":
                        addToHighlights(playlist);
                        refreshHighlights();
                        break;
                }
                return null;
            }
        });

        playlistPicker.setOnSelectedPlaylistCallback(new Callback<Playlists, Void>() {
            @Override
            public Void call(Playlists param) {
                currentSelectedPlaylist = param;
                refreshPlaylistSongs();
                return null;
            }
        });

        refreshUserPlaylist();

        contextMenus = new HashMap<>();

        if(!viewMode) {
            contextMenus.put("delete", "Remove from playlist");
        }else{
            if(currentUserType == 2){
                contextMenus.put("add_to_playlist", "Add song to playlist");
            }
        }

        contextMenus.put("add_to_queue", "Add to queue");

        playlistSongPicker = new SongPicker(dashboard.playListSongsTv, false, contextMenus);

        playlistSongPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Songs>, Void>() {
            @Override
            public Void call(Map.Entry<String, Songs> param) {
                Songs song = param.getValue();
                switch (param.getKey()){
                    case "delete":
                        if(currentSelectedPlaylist != null){
                            ArrayList<Object> bind = new ArrayList<>();
                            bind.add(currentSelectedPlaylist.playlist_id);
                            bind.add(song.song_id);
                            PlaylistSongs playlistSong = (PlaylistSongs) new PlaylistSongs().findOne("playlist_id = ? AND song_id = ?", bind);

                            if(playlistSong != null) {
                                if (playlistSong.delete()) {
                                    refreshPlaylistSongs();
                                }
                            }
                        }
                        break;
                    case "add_to_queue":
                        addSongToQueue(song);
                        break;
                    case "add_to_playlist":
                        ArrayList<Songs> songs = new ArrayList<>();
                        songs.add(song);
                        addSongsToPlaylist(songs);
                        break;
                }

                return null;
            }
        });

        playlistSongPicker.setOnSongDoubleClick(new Callback<Songs, Void>() {
            @Override
            public Void call(Songs param) {
                addSongToQueue(param);
                playQueueSong(param);
                return null;
            }
        });

        dashboard.searchPlaylistTf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refreshUserPlaylist();
            }
        });

        contextMenus = new HashMap<>();
        contextMenus.put("view", "View Profile");

        artistsPicker = new ArtistsPicker(dashboard.usersTv, contextMenus);

        artistsPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Users>, Void>() {
            @Override
            public Void call(Map.Entry<String, Users> param) {
                Users user = param.getValue();

                switch (param.getKey()){
                    case "view":
                        viewProfile(user);
                        break;
                }
                return null;
            }
        });

        refreshArtists();

        dashboard.searchArtistsTf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refreshArtists();
            }
        });

        contextMenus = new HashMap<>();
        contextMenus.put("view", "View Profile");

        listenerPicker = new ListenerPicker(dashboard.listenersTv, contextMenus);

        listenerPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Users>, Void>() {
            @Override
            public Void call(Map.Entry<String, Users> param) {
                Users user = param.getValue();

                switch (param.getKey()){
                    case "view":
                        viewProfile(user);
                        break;
                }
                return null;
            }
        });

        dashboard.searchListenerTf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refreshListeners();
            }
        });

        refreshListeners();

        contextMenus = new HashMap<>();
        contextMenus.put("delete", "Remove from likes");
        contextMenus.put("add_to_queue", "Add to queue");

        likedSongPicker = new SongPicker(dashboard.likedsongsTv, false, contextMenus);

        likedSongPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Songs>, Void>() {
            @Override
            public Void call(Map.Entry<String, Songs> param) {
                Songs song = param.getValue();
                switch (param.getKey()){
                    case "delete":
                        ArrayList<Object> bind = new ArrayList<>();
                        bind.add(currentUser.user_id);
                        bind.add(song.song_id);
                        LikedSongs likedSongs = (LikedSongs) new LikedSongs().findOne("user_id = ? AND song_id = ?", bind);

                        if(likedSongs != null) {
                            if (likedSongs.delete()) {
                                refreshLikedSongs();
                            }
                        }
                        refreshLikedSongs();
                        break;
                    case "add_to_queue":
                        addSongToQueue(song);
                        break;
                }

                return null;
            }
        });

        likedSongPicker.setOnSongDoubleClick(new Callback<Songs, Void>() {
            @Override
            public Void call(Songs param) {
                addSongToQueue(param);
                playQueueSong(param);
                return null;
            }
        });

        dashboard.searchLikedSongsTf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refreshLikedSongs();
            }
        });

        refreshLikedSongs();

        contextMenus = new HashMap<>();
        if(!viewMode) {
            contextMenus.put("remove", "Remove from highlights");
        }
        contextMenus.put("add_to_queue", "Add to queue");

        highlightsPicker = new PlaylistPicker(dashboard.highlightsTv, contextMenus);

        highlightsPicker.setContextMenuCallBack(new Callback<Map.Entry<String, Playlists>, Void>() {
            @Override
            public Void call(Map.Entry<String, Playlists> param) {
                Playlists playlists = param.getValue();
                switch (param.getKey()){
                    case "remove":
                        ArrayList<Object> bind = new ArrayList<>();
                        bind.add(currentUser.user_id);
                        bind.add(playlists.playlist_id);
                        Highlights highlights = (Highlights) new Highlights().findOne("user_id = ? AND playlist_id = ?", bind);

                        if(highlights != null) {
                            if (highlights.delete()) {
                                refreshHighlights();
                            }
                        }
                        refreshHighlights();
                        break;
                    case "add_to_queue":
                        Playlists playlist = highlightsPicker.getSelectedPlaylist();
                        ArrayList<PlaylistSongs> playlistSongs = (ArrayList<PlaylistSongs>) playlist.get(PlaylistSongs.class);
                        if (playlistSongs != null) {
                            for (PlaylistSongs playlistSong : playlistSongs) {
                                // TODO: 4/8/2019 check if song is null add error handling
                                addSongToQueue((Songs) playlistSong.get(Songs.class));
                            }
                        }
                        break;
                }

                return null;
            }
        });

        dashboard.searchHighlightsTf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refreshHighlights();
            }
        });

        refreshHighlights();

        Collection<String> items = new ArrayList<>();

        items.add("Artists");

        if(userType == 2){
            items.add("Listeners");
        }

        items.add("Songs");
        items.add("Albums");
        items.add("Playlists");

        dashboard.followsLv.getItems().addAll(items);

        dashboard.followsLv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                Integer index = dashboard.followsLv.getSelectionModel().getSelectedIndex();

                if(index < 0) return;

                HashMap<String, Object> data = new HashMap<>();

                data.put("view", viewMode);
                data.put("user", user);

                switch (((ArrayList<String>) items).get(index)){
                    case "Artists":
                        data.put("type", "FOLLOWS");
                        dashboard.loadToPane(ArtistsView.class, dashboard.followsPane, dashboard, data);
                        break;
                    case "Listeners":
                        data.put("type", "FOLLOWS");
                        dashboard.loadToPane(ListenersView.class, dashboard.followsPane, dashboard, data);
                        break;
                    case "Songs":
                        dashboard.loadToPane(SongsView.class, dashboard.followsPane, dashboard, data);
                        break;
                    case "Albums":
                        dashboard.loadToPane(AlbumsView.class, dashboard.followsPane, dashboard, data);
                        break;
                    case "Playlists":
                        dashboard.loadToPane(PlaylistView.class, dashboard.followsPane, dashboard, data);
                        break;
                }
            }
        });

        dashboard.followsLv.getSelectionModel().select(0);

        ArrayList itemsFollowers = new ArrayList<>();

        itemsFollowers.add("Listeners");

        if(userType == 1){
            itemsFollowers.add("Artists");
        }

        dashboard.followersLv.getItems().addAll(itemsFollowers);

        dashboard.followersLv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Integer index = dashboard.followersLv.getSelectionModel().getSelectedIndex();

                if(index < 0) return;
                HashMap<String, Object> data = new HashMap<>();

                data.put("view", viewMode);
                data.put("user", user);

                switch (((ArrayList<String>) itemsFollowers).get(index)){
                    case "Artists":
                        data.put("type", "FOLLOWERS");
                        dashboard.loadToPane(ArtistsView.class, dashboard.followersPane, dashboard, data);
                        break;
                    case "Listeners":
                        data.put("type", "FOLLOWERS");
                        dashboard.loadToPane(ListenersView.class, dashboard.followersPane, dashboard, data);
                        break;
                }
            }
        });

        dashboard.followersLv.getSelectionModel().select(0);

        activitiesViewer = new ActivitiesViewer(dashboard.activityTv);

        notificationsViewer = new NotificationsViewer(dashboard.notificationsTv);

        userSubject = new Subject();

        //Iterator<Activities> has = activities.iterator();

        HashMap<String, Object> state = new HashMap<>();
        state.put("activities",  new UserActivitiesCollection(user).getIterator());
        state.put("notifications", new UsersNotificationCollection(user, false).getIterator());
        state.put("unseenNotifications", new UsersNotificationCollection(user, true).getIterator());
        state.put("followers", user.getFollowers());

        new ActivityObserver(userSubject){
            @Override
            public void update() {
                super.update();
                Iterator<Activities> activitiesIterator = (Iterator<Activities>) subject.getState().get("activities");
                activitiesIterator.first();
                activitiesViewer.setActivities(activitiesIterator);
            }
        };

        new NotificationObserver(userSubject){
            @Override
            public void update() {
                super.update();

                if(userType == 1 || viewMode) return;

                Iterator<Notifications> unseenNotifications = (Iterator<Notifications>) subject.getState().get("unseenNotifications");

                Integer unseen = 0;

                if(unseenNotifications != null){
                    unseenNotifications.first();
                    while (unseenNotifications.hasNext()){
                        unseen += 1;
                        unseenNotifications.next();
                    }
                }
                dashboard.userTabPane.getTabs().get(1).setText("Notifications ("+unseen+")");

                Iterator<Notifications> notifications = (Iterator<Notifications>) subject.getState().get("notifications");
                if(notifications == null) return;

                notifications.first();
                notificationsViewer.setNotifications(notifications);
            }
        };

        new FollowersObserver(userSubject){
            @Override
            public void update() {
                super.update();
                if(viewMode) return;
                dashboard.followersCountLb.setText(String.valueOf(subject.getState().get("followers")));
                refreshFollowersPane();
            }
        };

        userSubject.setState(state);

        if(!viewMode) {

            wait.setOnFinished((e) -> {

                userSubject.getState().replace("followers", user.getFollowers());
                userSubject.setState(userSubject.getState());

                if(currentUserType == 2){
                    checkNotifications(true);
                    System.out.println("checking...");
                    if(dashboard.userTabPane.getSelectionModel().getSelectedIndex() == 1){
                        user.seen();
                        userSubject.getState().replace("unseenNotifications", null);
                        userSubject.setState(userSubject.getState());
                    }
                }

                wait.playFromStart();

            });
            checkNotifications(false);
            wait.play();
        }
    }

    public void refreshFollowsPane(){
        Integer lastIndex = dashboard.followsLv.getSelectionModel().getSelectedIndex();
        dashboard.followsLv.getSelectionModel().clearSelection();
        dashboard.followsLv.getSelectionModel().selectIndices(lastIndex, lastIndex);
    }

    private void refreshFollowersPane(){
        Integer lastIndex = dashboard.followersLv.getSelectionModel().getSelectedIndex();
        dashboard.followersLv.getSelectionModel().clearSelection();
        dashboard.followersLv.getSelectionModel().selectIndices(lastIndex, lastIndex);
    }

    private void checkNotifications(boolean playSound){
        Iterator<Notifications> newNotifications = new UsersNotificationCollection(user, false).getIterator();

        if(newNotifications != null){

            Iterator<Notifications> currentNotifications = (Iterator<Notifications>) userSubject.getState().get("notifications");

            Integer newNotificationSize = 0;
            Integer currentNotificationSize = 0;

            if (currentNotifications == null) {
                currentNotificationSize = 0;
            }else{
                currentNotifications.first();
                while(currentNotifications.hasNext()){
                    currentNotificationSize += 1;
                    currentNotifications.next();
                }
            }

            while (newNotifications.hasNext()){
                newNotificationSize += 1;
                newNotifications.next();
            }

            if(newNotificationSize > currentNotificationSize){
                //notify
                if(playSound) {
                    if(player != null) {
                        player.seek(new Duration(0));
                        player.play();
                        refreshFollowsPane();
                    }
                }

                userSubject.getState().replace("notifications", newNotifications);
                userSubject.getState().replace("unseenNotifications", new UsersNotificationCollection(user, true).getIterator());

                userSubject.setState(userSubject.getState());
            }
        }
    }

    public void addActivity(String description) {
        Activities activity = new Activities();
        activity.user_id = currentUser.user_id;
        activity.description = description;
        activity.create();
        userSubject.getState().replace("activities", new UserActivitiesCollection(user).getIterator());
        userSubject.setState(userSubject.getState());
    }

    public void addSongsToPlaylist(ArrayList<Songs> songs) {
        if(currentUser != null){

            HashMap<String, Object> data = new HashMap<>();

            data.put("user", currentUser);

            PlaylistChooser playlistChooser = new PlaylistChooser();
            playlistChooser.setData(data);
            playlistChooser.showDialog(dashboard);

            playlistChooser.selectBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(playlistChooser.playlistTv.getSelectionModel().getSelectedIndex() > -1){
                        Playlists playlist = (Playlists) playlistChooser.playlistTv.getSelectionModel().getSelectedItem();
                        if(playlist != null) {
                            if (songs.size() > 0) {
                                // TODO: 4/11/2019 ADD ERROR HANDLING
                                for (Songs song : songs) {
                                    PlaylistSongs playlistSong = new PlaylistSongs();
                                    playlistSong.song_id = song.song_id;
                                    playlistSong.playlist_id = playlist.playlist_id;

                                    if(playlistSong.create()) {
                                        String addPlaylistSong = currentUser.username + " has added " + song.name + " to playlist " + playlist.name + ".";
                                        if (parentDashboardController != null) {
                                            parentDashboardController.addActivity(addPlaylistSong);
                                            parentDashboardController.notifyFollowers(addPlaylistSong, 2, null);
                                        } else {
                                            addActivity(addPlaylistSong);
                                            notifyFollowers(addPlaylistSong, 2, null);
                                        }
                                    }
                                }

                                playlistChooser.close();
                                if(parentDashboardController != null) {
                                    parentDashboardController.refreshUserPlaylist();
                                    parentDashboardController.refreshPlaylistSongs();
                                }else{
                                    refreshUserPlaylist();
                                    refreshPlaylistSongs();
                                }
                            }
                        }
                    }

                }
            });
        }
    }

    private void resetCurrentPlaying(){
        dashboard.songNameLb.setText("-");
        dashboard.albumLb.setText("-");
        dashboard.artistLb.setText("-");
        dashboard.artistLb2.setText("-");
    }

    public void addSongToLikes (Songs song){
        if(currentUser != null){
            HashMap<String, Object> data = new HashMap<>();
            data.put("user", currentUser);
            String liked = currentUser.username + " liked the song " + song.name + ".";

            LikedSongs likedSong = new LikedSongs();
            likedSong.song_id = song.song_id;
            likedSong.user_id = currentUser.user_id;
            likedSong.name = song.name;
            likedSong.create();
            addActivity(liked);
            notifyFollowers(liked, 2, null);

            refreshLikedSongs();
        }
        System.out.println("added to likes");
    }

    public void addToHighlights (Playlists playlists){
        if(currentUser != null && currentUser.user_type == 2) {
            if(viewMode){
                parentDashboardController.addToHighlights(playlists);
                return;
            }
            HashMap<String, Object> data = new HashMap<>();
            data.put("user", currentUser);
            String highlighted = currentUser.username + " highlighted the playlist " + playlists.name + ".";

            Highlights highlights = new Highlights();
            highlights.playlist_id = playlists.playlist_id;
            if(currentUser.user_type == 2){
                highlights.user_id = currentUser.user_id;
            }
            highlights.name = playlists.name;
            highlights.create();
            addActivity(highlighted);
            notifyFollowers(highlighted, 2, null);
            refreshHighlights();
            //System.out.println("added to highlights BA TALAGA?");
        }
        //System.out.println("added to highlights");
    }

    public void viewProfile(Users user){
        Dashboard dashboardView = new Dashboard();
        dashboardView.setParent(dashboard);

        HashMap<String, Object> data = new HashMap<>();

        data.put("user", user);
        dashboardView.setData(data);
        dashboardView.showDialog(dashboard);
        dashboardView.getStage().setHeight(405);
    }

    public void refreshListeners(){
        ListenersCollection listenersCollection = new ListenersCollection(user, "%"+dashboard.searchListenerTf.getText()+"%");
        listenerPicker.setUsersList(listenersCollection.getIterator());
    }

    public void refreshArtists(){
        ArtistsCollection artistsCollection = new ArtistsCollection(user, "%"+dashboard.searchArtistsTf.getText()+"%");
        artistsPicker.setUsersList(artistsCollection.getIterator());
    }

    private void refreshAllSongs(){
        refreshAlbumSongs();
        refreshPlaylistSongs();
        refreshQueueSongs();
        refreshUserSongs();
        refreshLikedSongs();
    }

    // TODO: 4/8/2019 replace all with iterators starting here ...

    private void refreshUserPlaylist(){
        UsersPlaylistCollection usersPlaylistCollection = new UsersPlaylistCollection(user, "%"+dashboard.searchPlaylistTf.getText()+"%");
        playlistPicker.setPlaylistList(usersPlaylistCollection.getIterator());
    }

    private void refreshPlaylistSongs(){
        Playlists playlists = currentSelectedPlaylist;
        Iterator<Songs> songs = null;

        if(playlists != null) {
            PlaylistSongsCollection playlistSongsCollection = new PlaylistSongsCollection(playlists);
            songs = playlistSongsCollection.getIterator();
        }
        this.playlistSongPicker.setSongList(songs);
    }

    public void addSongToQueue(Songs param) {
        if(param == null) return;
        if(viewMode){
            parentDashboardController.addSongToQueue(param);
            return;
        }
        if(!queueSongsList.containsKey(param.song_id)){
            queueSongsList.put(param.song_id, param);
        }else{
            if(!queueSongsList.get(param.song_id).equals(param)){
                queueSongsList.replace(param.song_id, param);
            }
        }
        refreshQueueSongs();
    }

    private void refreshAlbumSongs(){
        Albums album = albumPicker.getSelectedAlbum();
        iterator.Iterator<Songs> songs = null;
        if(album != null){
            AlbumSongsCollection albumSongsCollection = new AlbumSongsCollection(album);
            songs = albumSongsCollection.getIterator();
        }
        this.albumSongs.setSongList(songs);
    }

    private void refreshUserAlbums(){
        UserAlbumsCollection userAlbumsCollection = new UserAlbumsCollection(user, "%"+dashboard.searchAlbumsTf.getText()+"%");
        albumPicker.setAlbumsList(userAlbumsCollection.getIterator());
    }

    public void notifyFollowers(String description, Integer type, Users exception){
        ArrayList<Follows> followers = currentUser.getFollowersList();

        if(followers != null) {
            for (Follows follower : followers) {
                Users user = follower.getUser();

                if(exception != null){
                    if(exception.user_id == follower.user_id){
                        continue;
                    }
                }

                if(user.user_type == type || type == 3) {
                    Notifications notification = new Notifications();

                    notification.user_id = follower.user_id;
                    notification.description = description;

                    notification.create();
                }
            }
        }
    }

    public void playQueueSong(Songs song){
        if(song == null) return;

        currentSong = song;

        if(viewMode){
            parentDashboardController.playQueueSong(song);
            return;
        }

        musicManager.playSong(song);
    }

    private void refreshUserSongs(){
        UserSongsCollection userSongs = new UserSongsCollection(user, "%"+dashboard.searchSongsTf.getText()+"%");
        songPicker.setSongList(userSongs.getIterator());
    }

    private void refreshQueueSongs(){
        QueueSongCollection queueSongCollection = new QueueSongCollection(queueSongsList.values());
        queueSongPicker.setSongList(queueSongCollection.getIterator());
    }

    private void refreshLikedSongs() {
        UsersLikedSongsCollection usersLikedSongsCollection = new UsersLikedSongsCollection(user, "%" + dashboard.searchLikedSongsTf.getText() + "%");
        this.likedSongPicker.setSongList(usersLikedSongsCollection.getIterator());
    }

    private void refreshHighlights(){
        UserHighlightsCollection userHighlightsCollection = new UserHighlightsCollection(user,"%" + dashboard.searchHighlightsTf.getText() + "%" );
        this.highlightsPicker.setPlaylistList(userHighlightsCollection.getIterator());
        //MAKE ITERATOR
        /* Playlists playlists = new Playlists();
        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user.user_id);
        bind.add("%" + dashboard.searchHighlightsTf.getText() + "%"); */
        //this.highlightsPicker.setPlaylistList((ArrayList<Playlists>) playlists.find("user_id = ? AND highlight = 1 AND name LIKE ?", bind));
    }
}
