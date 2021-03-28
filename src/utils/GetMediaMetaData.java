package utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaErrorEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Callback;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class GetMediaMetaData {

    public String name;
    public String artist;
    public String album;
    public String year;
    public String genre;
    public String filePath;
    public Image coverImage;
    private Callback<GetMediaMetaData, Void> callback;
    private Callback<String, Void> onError;
    private Callback<Image, Void> onImage;

    private MediaView mediaView;

    public GetMediaMetaData(File file, MediaView mediaView, Callback<GetMediaMetaData, Void> callback){

        Media media = new Media(file.toURI().toString());
        filePath = file.getPath();
        name = StringUtils.getFileWithoutExtension(file.getName());
        this.callback = callback;
        this.mediaView = mediaView;

        process(media);
    }

    public GetMediaMetaData(String url, MediaView mediaView, Callback<GetMediaMetaData, Void> callback, Callback<Image,Void> onImage, Callback<String, Void> onError){

        this.onError = onError;
        this.onImage = onImage;

        Media media = new Media(url);
        filePath = url;
        try {
            URI uri = new URI(url);
            name = new File(uri.getPath()).getName();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.callback = callback;
        this.mediaView = mediaView;

        process(media);
    }

    public GetMediaMetaData(Media media, MediaView mediaView, Callback<GetMediaMetaData, Void> callback){

        filePath = media.getSource();
        try {
            URI uri = new URI(media.getSource());
            name = new File(uri.getPath()).getName();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        this.callback = callback;
        this.mediaView = mediaView;

        process(media);
    }

    private void process(Media media){
        MediaPlayer player = new MediaPlayer(media);
        mediaView.setMediaPlayer(player);

        player.setOnError(new Runnable() {
            @Override
            public void run() {
                onError.call(player.getError().toString());
                System.out.println(player.getError());
            }
        });

        media.getMetadata().addListener(new MapChangeListener<String, Object>() {
            @Override
            public void onChanged(Change<? extends String, ?> change) {
                if(change.wasAdded()){
                    if(change.getKey().equals("image")){
                        if(onImage != null){
                            onImage.call((Image) change.getValueAdded());
                        }
                    }
                }
            }
        });

        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Object> entry : media.getMetadata().entrySet()){
                    if(entry.getKey().equals("title")){
                        name = entry.getValue().toString().replaceAll("\\P{Print}", "");
                    }else if(entry.getKey().equals("artist")){
                        artist = entry.getValue().toString().replaceAll("\\P{Print}", "");
                    }else if(entry.getKey().equals("album")){
                        album = entry.getValue().toString().replaceAll("\\P{Print}", "");
                    }else if(entry.getKey().equals("year")){
                        year = entry.getValue().toString().replaceAll("\\P{Print}", "");
                    }else if(entry.getKey().equals("genre")){
                        genre = entry.getValue().toString().replaceAll("\\P{Print}", "");
                    }else if(entry.getKey().equals("image")){
                        coverImage = (Image) entry.getValue();
                    }
                }
                callback.call(GetMediaMetaData.this);
            }
        });
    }
}
