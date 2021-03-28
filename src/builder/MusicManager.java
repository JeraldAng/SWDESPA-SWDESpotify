package builder;

import classes.SongPicker;
import iterator.Iterator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;
import models.Playlists;
import models.Songs;
import utils.GetMediaMetaData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MusicManager {

    private Slider volumeScrubber;
    private Slider songScrubber;

    private Button addSongButton;
    private Window parent;

    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Button repeatButton;
    private Button shuffleButton;

    private Button repeatOneButton;

    private Button nextButton;
    private Button previousButton;

    private Label songTitleLabel;
    private Label currentPlayBackTimeLabel;
    private Label totalPlayBackTimeLabel;

    private ImageView albumCoverIV;

    private ListView<String> playListSelector;

    private TableView songsSelectorTv;
    private HashMap<String, String> contextMenus;

    private MediaPlayer player;

    private Songs currentSongPlayed;
    private Playlists currentSelectedPlayList;

    private boolean playerReady = true;
    private boolean repeat = false;
    private boolean repeatOne = false;
    private boolean shuffle = false;
    private boolean sliderPressed = false;

    //private Users user;
    private ArrayList<Playlists> playList;
    private ArrayList<Songs> songsList;

    private Image defaultAlbumPlaceHolder = new Image("/resources/images/default-album-cover.jpg");

    private SongPicker songPicker;

    private Callback<List<File>, Void> onSelectedSongsCallback;

    private MediaView mediaView;

    private Double volume;

    private String http;


    public MusicManager(MusicManagerBuilder musicManagerBuilder){
        //LOGIC HERE?
        //this.user = musicManagerBuilder.user;

        this.mediaView = musicManagerBuilder.mediaView;

        this.volumeScrubber = musicManagerBuilder.volumeScrubber;
        this.songScrubber = musicManagerBuilder.songScrubber;

        this.addSongButton = musicManagerBuilder.addSongButton;
        this.parent = musicManagerBuilder.parent;

        this.playButton = musicManagerBuilder.playButton;
        this.pauseButton = musicManagerBuilder.pauseButton;
        this.stopButton = musicManagerBuilder.stopButton;

        this.repeatButton = musicManagerBuilder.repeatButton;
        this.shuffleButton = musicManagerBuilder.shuffleButton;
        this.nextButton = musicManagerBuilder.nextButton;
        this.previousButton = musicManagerBuilder.previousButton;

        this.songTitleLabel = musicManagerBuilder.songTitleLabel;

        //this.playListSelector = musicManagerBuilder.playListSelector;
        this.songsSelectorTv = musicManagerBuilder.songsSelectorTv;
        this.contextMenus = musicManagerBuilder.contextMenus;

        this.albumCoverIV = musicManagerBuilder.albumCoverIV;
        this.totalPlayBackTimeLabel = musicManagerBuilder.totalPlayBackTimeLabel;
        this.currentPlayBackTimeLabel = musicManagerBuilder.currentPlayBackTimeLabel;

        this.onSelectedSongsCallback = musicManagerBuilder.onSelectedSongsCallback;

        this.repeatOneButton = musicManagerBuilder.repeatOneButton;

        this.http = musicManagerBuilder.http;

        if(getVolumeSlider() != null) {
            volumeScrubber.setMin(0);
            volumeScrubber.setMax(100);

            volumeScrubber.setValue(100);

            volume = volumeScrubber.getValue()/100;

            volumeScrubber.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    volume = volumeScrubber.getValue() / 100;

                    if (player != null) {
                        player.setVolume(volume);
                    }
                }
            });
        }

        if(playButton != null){
            playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(player != null){
                        player.play();
                    }
                }
            });
        }

        if(pauseButton != null){
            pauseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(player != null){
                        player.pause();
                    }
                }
            });
        }


        if(stopButton != null){
            stopButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(player != null){
                        player.stop();
                    }
                }
            });
        }

        if(repeatButton != null){
            repeatButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(!repeat) {
                        repeatButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                    }else{
                        repeatButton.setStyle("");
                    }
                    repeat = !repeat;

                }
            });
        }

        if(repeatOneButton != null){
            repeatOneButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(!repeatOne) {
                        repeatOneButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                    }else{
                        repeatOneButton.setStyle("");
                    }
                    repeatOne = !repeatOne;

                }
            });
        }

        if(shuffleButton != null){
            shuffleButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(!shuffle) {
                        shuffleButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                    }else{
                        shuffleButton.setStyle("");
                    }
                    shuffle = !shuffle;
                }
            });
        }

        if(previousButton != null){
            previousButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    playPrev();
                }
            });
        }

        if(nextButton != null){
            nextButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    playNext();
                }
            });
        }

        if(songScrubber != null){
            songScrubber.setMin(0);
            songScrubber.setMax(100);

            songScrubber.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    sliderPressed = true;
                }
            });

            songScrubber.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(player == null) return;
                    Duration newDuration = new Duration((songScrubber.getValue() / 100) * player.getTotalDuration().toMillis());
                    Integer newSec = (int)(newDuration.toSeconds()%60);
                    Integer newMin = (int)Math.floor(newDuration.toMinutes());
                    if(currentPlayBackTimeLabel != null) {
                        currentPlayBackTimeLabel.setText(newMin + ":" + String.format("%02d", newSec));
                    }
                }
            });

            songScrubber.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(player == null) return;
                    if(!player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                        player.play();
                    }
                    player.seek(new Duration((songScrubber.getValue() / 100) * player.getTotalDuration().toMillis()));
                    sliderPressed = false;
                }
            });
        }
        
        if(songsSelectorTv != null){

            songPicker = new SongPicker(songsSelectorTv, false, contextMenus);

            songPicker.setOnSongSelected(new Callback<Songs, Void>() {
                @Override
                public Void call(Songs song) {
                    playSong(song);
                    return null;
                }
            });

            refreshSongsList();
        }

        if(addSongButton != null){
            addSongButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Add Songs");
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
                    fileChooser.getExtensionFilters().add(extFilter);
                    List<File> files = fileChooser.showOpenMultipleDialog(parent);

                    if (files != null) {
                        onSelectedSongsCallback.call(files);
                    }
                }
            });
        }
    }

    public SongPicker getSongPicker(){
        return songPicker;
    }

    public void setSongList(Iterator<Songs> songs){
        if(songPicker != null){
            songPicker.setSongList(songs);
        }
    }

    public void setVolume(Double volume){
        this.volume = volume;
        if(volumeScrubber != null){
            volumeScrubber.setValue(volume*100);
        }
    }

    public Slider getVolumeSlider(){
        return volumeScrubber;
    }

    public ImageView getAlbumCoverIv(){
        return albumCoverIV;
    }

    public Songs getCurrentSongPlayed(){
        return currentSongPlayed;
    }

    public void playSong(Songs song){
        if(song != null){

            String location = song.location;

            if(http != null){
                location = http + song.location;
            }

            disposePlayer();

            if(!playerReady) return;
            playerReady = false;

            if(currentSongPlayed != null && song.song_id.equals(currentSongPlayed.song_id)) {
                if (player != null) {
                    if (player.getStatus().equals(MediaPlayer.Status.PAUSED)) {
                        player.play();
                    }
                    playerReady = true;
                    return;
                }
            }

//            File f = new File(song.location);
//
//            if(f.exists()) {
            String finalLocation = location;

            if(songsSelectorTv != null){
                songsSelectorTv.getSelectionModel().select(song);
                songsSelectorTv.getFocusModel().focus(songsSelectorTv.getSelectionModel().getSelectedIndex());
            }

            new GetMediaMetaData(location, this.mediaView, new Callback<GetMediaMetaData, Void>() {
                @Override
                public Void call(GetMediaMetaData param) {

                    currentSongPlayed = song;

                    if (songTitleLabel != null) {
                        songTitleLabel.setText(song.name);
                    }

                    Image img = defaultAlbumPlaceHolder;

                    Image albumCover = song.getAlbumCover();

                    if(song.album_id != 0) {
                        if (albumCover != null) {
                            img = albumCover;
                        }else{
                            img = defaultAlbumPlaceHolder;
                        }
                    }else{
                        if(param.coverImage != null) {
                            img = param.coverImage;
                        }else{
                            img = defaultAlbumPlaceHolder;
                        }
                    }

                    if (albumCoverIV != null) {
                        albumCoverIV.setImage(img);
                    }

                    Media media = new Media(finalLocation);

                    player = new MediaPlayer(media);

                    setupMediaPlayer(player);

                    player.setVolume(volume);

//                        if(song.freq == null){
//                            song.freq = 0;
//                        }
//
//                        song.freq += 1;
//                        song.save();

                    if(onPlayCallback != null){
                        onPlayCallback.call(song);
                    }
                    player.play();

                    playerReady = true;

                    return null;
                }
            }, new Callback<Image, Void>() {
                @Override
                public Void call(Image param) {

                    Image img = defaultAlbumPlaceHolder;

                    Image albumCover = song.getAlbumCover();

                    if(song.album_id != 0) {
                        if (albumCover != null) {
                            img = albumCover;
                        }else{
                            img = defaultAlbumPlaceHolder;
                        }
                    }else{
                        img = param;
                    }

                    if (albumCoverIV != null) {
                        albumCoverIV.setImage(img);
                    }

                    return null;
                }
            }, new Callback<String, Void>() {
                @Override
                public Void call(String param) {
                    disposePlayer();
                    playerReady = true;
                    return null;
                }
            });
        }
    }

    public void disposePlayer(){
        if(player != null) {
            albumCoverIV.setImage(defaultAlbumPlaceHolder);
            player.stop();
            player.dispose();
            player = null;
            System.gc();
            playerReady = true;
        }
    }

    public void resetCurrentSong(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(songTitleLabel != null) {
                    songTitleLabel.setText("--");
                }
                if(currentPlayBackTimeLabel != null) {
                    currentPlayBackTimeLabel.setText("-:--");
                }
                if(totalPlayBackTimeLabel != null) {
                    totalPlayBackTimeLabel.setText("-:--");
                }
                if(albumCoverIV != null) {
                    albumCoverIV.setImage(defaultAlbumPlaceHolder);
                }
                currentSongPlayed = null;
            }
        });
    }

    private void setupMediaPlayer(MediaPlayer player){

        player.setAutoPlay(true);

        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {

                Integer currentSec = (int)observable.getValue().toSeconds()%60;
                Integer currentMin = (int)Math.floor(observable.getValue().toMinutes());

                Integer totalSec = (int)(player.getTotalDuration().toSeconds()%60);
                Integer totalMin = (int)Math.floor(player.getTotalDuration().toMinutes());

                if(currentSec > totalSec && currentMin == totalMin){
                    currentSec = totalSec;
                }

                if(totalPlayBackTimeLabel != null) {
                    totalPlayBackTimeLabel.setText(totalMin + ":" + String.format("%02d", totalSec));
                }

                if(!sliderPressed) {
                    if(currentPlayBackTimeLabel != null) {
                        currentPlayBackTimeLabel.setText(currentMin + ":" + String.format("%02d", currentSec));
                    }
                    if(songScrubber != null) {
                        songScrubber.setValue((observable.getValue().toSeconds() / player.getTotalDuration().toSeconds()) * 100);
                    }
                }
            }
        });

        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                playerReady = true;

                if(songsSelectorTv == null) return;

                Integer i = songsSelectorTv.getSelectionModel().getSelectedIndex();
                if(repeatOne){
                    player.seek(new Duration(0));
                    player.play();
                    return;
                }
                if(shuffle){
                    playRandom();
                    return;
                }
                if(repeat) {
                    if((i+1) < songPicker.songCount()){
                        playNext();
                    }else{
                        playSong(songPicker.goToFirst());
                        return;
                    }
                    if(!player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                        player.play();
                    }
                    player.seek(new Duration(0));
                }else{
                    if((i+1) == songsSelectorTv.getItems().size()){
                        player.stop();
                        resetCurrentSong();
                        disposePlayer();
                        songsSelectorTv.getSelectionModel().clearSelection();
                        if(onLastSongFinishedCallback != null){
                            onLastSongFinishedCallback.call(null);
                        }
                    }else {
                        playNext();
                    }
                }
            }
        });

        player.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                playerReady = true;
                if(playButton != null){
                    playButton.setVisible(false);
                }
                if(pauseButton != null) {
                    pauseButton.setVisible(true);
                }
            }
        });

        player.setOnStopped(new Runnable() {
            @Override
            public void run() {
                if(playButton != null){
                    playButton.setVisible(true);
                }
                if(pauseButton != null) {
                    pauseButton.setVisible(false);
                }
            }
        });

        player.setOnPaused(new Runnable() {
            @Override
            public void run() {
                if(playButton != null){
                    playButton.setVisible(true);
                }
                if(pauseButton != null) {
                    pauseButton.setVisible(false);
                }
            }
        });
    }

    private Callback<Void, Void> onLastSongFinishedCallback;
    public void onLastSongFinished(Callback<Void, Void> callback){
        this.onLastSongFinishedCallback = callback;
    }

    private Callback<Songs, Void> onPlayCallback;
    public void onPlay(Callback<Songs, Void> callback){
        this.onPlayCallback = callback;
    }

    public void refreshSongsList(){
        if(songPicker != null){
            songPicker.refreshPlayListSongs();
        }
    }

    public void playRandom(){
        if(songPicker == null) return;
        if(!playerReady) return;
        playSong(songPicker.getRandom());
    }

    public void playNext(){
        if(songPicker == null) return;
        if(!playerReady) return;
        if(shuffle){
            playRandom();
            return;
        }
        playSong(songPicker.goToNext());
    }

    public void playPrev(){
        if(songPicker == null) return;
        if(!playerReady) return;
        if(shuffle){
            playRandom();
            return;
        }
        playSong(songPicker.goToPrevious());
    }

    public static class MusicManagerBuilder{

        private Slider volumeScrubber;
        private Slider songScrubber;

        private Button addSongButton;
        private Window parent;

        private Button playButton;
        private Button pauseButton;
        private Button stopButton;
        private Button repeatButton;
        private Button shuffleButton;

        private Button repeatOneButton;

        private Button nextButton;
        private Button previousButton;

        private Label songTitleLabel;
        private Label currentPlayBackTimeLabel;
        private Label totalPlayBackTimeLabel;

        private ImageView albumCoverIV;

//        private ListView<String> playListSelector;
        private TableView songsSelectorTv;

        //private Users user;

        private Callback<List<File>, Void> onSelectedSongsCallback;

//        private TableView songListSelectorTv;
//        private ListView<String> albumSelector;
//        private ListView<String> genreSelector;
//        private ListView<String> yearSelector;

        private MediaView mediaView;
        private String http;
        private HashMap<String, String> contextMenus;

        public MusicManagerBuilder(MediaView mediaView){
            //this.user = user;
            this.mediaView = mediaView;
        }

        public MusicManagerBuilder withVolumeScrubber(Slider volumeScrubber) {
            this.volumeScrubber = volumeScrubber;
            return this;
        }

        public MusicManagerBuilder withSongScrubber(Slider songScrubber) {
            this.songScrubber = songScrubber;
            return this;
        }

        public MusicManagerBuilder withAddSongButton(Button addSongButton, Window parent, Callback<List<File>, Void> onSelectedSongsCallback) {
            this.addSongButton = addSongButton;
            this.parent = parent;
            this.onSelectedSongsCallback = onSelectedSongsCallback;
            return this;
        }

        public MusicManagerBuilder withPlayButton(Button playButton) {
            this.playButton = playButton;
            return this;
        }

        public MusicManagerBuilder withPauseButton(Button pauseButton) {
            this.pauseButton = pauseButton;
            return this;
        }

        public MusicManagerBuilder withStopButton(Button stopButton) {
            this.stopButton = stopButton;
            return this;
        }

        public MusicManagerBuilder withRepeatButton(Button repeatButton) {
            this.repeatButton = repeatButton;
            return this;
        }

        public MusicManagerBuilder withShuffleButton(Button shuffleButton) {
            this.shuffleButton = shuffleButton;
            return this;
        }

        public MusicManagerBuilder withNextButton(Button nextButton) {
            this.nextButton = nextButton;
            return this;
        }

        public MusicManagerBuilder withPreviousButton(Button previousButton) {
            this.previousButton = previousButton;
            return this;
        }

        public MusicManagerBuilder withSongTitleLabel(Label songTitleLabel) {
            this.songTitleLabel = songTitleLabel;
            return this;
        }
        public MusicManagerBuilder withCurrentPlayBackTimeLabel(Label currentPlayBackTimeLabel) {
            this.currentPlayBackTimeLabel = currentPlayBackTimeLabel;
            return this;
        }
        public MusicManagerBuilder withTotalPlayBackTimeLabel(Label totalPlayBackTimeLabel) {
            this.totalPlayBackTimeLabel = totalPlayBackTimeLabel;
            return this;
        }

        public MusicManagerBuilder withAlbumCoverIV(ImageView albumCoverIV) {
            this.albumCoverIV = albumCoverIV;
            return this;
        }

        public MusicManagerBuilder withRepeatOneButton(Button repeatButton){
            this.repeatOneButton = repeatButton;
            return this;
        }
//        public MusicManagerBuilder withPlayListSelector(ListView<String> playListSelector) {
//            this.playListSelector = playListSelector;
//            return this;
//        }

        public MusicManagerBuilder withSongPicker(TableView songsSelectorTv, HashMap<String, String> contextMenus) {
            this.songsSelectorTv = songsSelectorTv;
            this.contextMenus = contextMenus;
            return this;
        }

        public MusicManagerBuilder withHTTP(String http){
            this.http = http;
            return this;
        }

        public MusicManager buildMusicManager(){
            return new MusicManager(this);
        }
    }
}
