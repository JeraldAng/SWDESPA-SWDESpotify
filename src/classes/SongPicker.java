package classes;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import iterator.Iterator;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import models.Songs;
import utils.NumbersUtils;

import java.awt.*;
import java.util.*;

public class SongPicker {

    private TableView songListSelectorTv;

    private Callback<Songs, Void> onSelectedSongCallback;
    private Callback<Songs, Void> onDoubleClickSongCallback;
    private Callback<Map.Entry<String, Songs>, Void> onContextMenuCallback;

    private Iterator<Songs> songs;

    private boolean enableMultiSelect;

    private HashMap<String, String> contextMenus;

    public SongPicker(TableView songListSelectorTv){
        this.songListSelectorTv = songListSelectorTv;
        enableMultiSelect = false;
        setup();
    }

    public SongPicker(TableView songListSelectorTv, boolean enableMultiSelect){
        this.songListSelectorTv = songListSelectorTv;
        this.enableMultiSelect = enableMultiSelect;
        setup();
    }

    public SongPicker(TableView songListSelectorTv, boolean enableMultiSelect, HashMap<String, String> contextMenus){
        this.songListSelectorTv = songListSelectorTv;
        this.enableMultiSelect = enableMultiSelect;
        this.contextMenus = contextMenus;
        setup();
    }

    private void setup(){

        this.songListSelectorTv.getColumns().clear();

        if(enableMultiSelect){
            this.songListSelectorTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }

        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setPrefWidth(100);
        nameColumn.setCellValueFactory(new PropertyValueFactory<Songs, Object>("name"));
        TableColumn artistColumn = new TableColumn("Artist");
        artistColumn.setCellValueFactory(new PropertyValueFactory<Songs, Object>("artist"));
        TableColumn albumColumn = new TableColumn("Album");
        albumColumn.setCellValueFactory(new PropertyValueFactory<Songs, Object>("album"));
        TableColumn yearColumn = new TableColumn("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<Songs, Object>("year"));
        TableColumn genreColumn = new TableColumn("Genre");
        genreColumn.setCellValueFactory(new PropertyValueFactory<Songs, Object>("genre"));
        songListSelectorTv.getColumns().addAll(nameColumn, artistColumn, albumColumn, yearColumn, genreColumn);

        ObservableList<TableColumn> columns = songListSelectorTv.getColumns();

        for(TableColumn column : columns){
            column.setCellFactory(new Callback<TableColumn, TableCell>() {
                @Override
                public TableCell call(TableColumn param) {
                    return new TableCell<Songs, Object>() {
                        @Override
                        public void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            if(item == null){
                                setText("");
                                return;
                            }
                            setStyle("");
                            if (!isEmpty()) {
                                if(getTableRow() == null) return;
                                Songs song = (Songs) getTableRow().getItem();
                                if(song != null) {
                                    setText(String.valueOf(item));
//                                    if (song.favorite.equals(1)) {
//                                        setStyle("-fx-text-fill: violet");
//                                    }
                                }
                            }
                        }
                    };
                }
            });
        }

        songListSelectorTv.setRowFactory(new Callback<TableView, TableRow>() {
            @Override
            public TableRow call(TableView param) {
                TableRow row = new TableRow();

                ContextMenu contextMenu = new ContextMenu();

                if(contextMenus != null) {

                    for(Map.Entry<String, String> entry : contextMenus.entrySet()){
                        MenuItem menuItem = new MenuItem();
                        menuItem.textProperty().bind(Bindings.format(entry.getValue()));
                        menuItem.setId(entry.getKey());

                        menuItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Songs song = getSelectedSong();

                                if (song != null) {
                                    if (onContextMenuCallback != null) {
                                        Map.Entry<String, Songs> entry = new AbstractMap.SimpleEntry<String, Songs>(menuItem.getId(), song);
                                        onContextMenuCallback.call(entry);
                                    }
                                }
                            }
                        });

                        contextMenu.getItems().add(menuItem);

                    }

                    row.emptyProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isNowEmpty) {
                            if (songListSelectorTv.getItems().size() > 0) {
                                if (isNowEmpty) {
                                    row.setContextMenu(null);
                                } else {
                                    row.setContextMenu(contextMenu);
                                }
                            }
                        }
                    });
                }

                return row;
            }
        });

        songListSelectorTv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY){
                    return;
                }

                if(event.getTarget() instanceof TableColumnHeader || event.getTarget() instanceof javafx.scene.shape.Rectangle){
                    return;
                }

                Songs song = getSelectedSong();
                if(onSelectedSongCallback != null){
                    onSelectedSongCallback.call(song);
                }

                if(event.getClickCount() > 1){
                   if(onDoubleClickSongCallback != null) onDoubleClickSongCallback.call(song);
                }
            }
        });
    }

    public void setOnSongDoubleClick(Callback<Songs, Void> callback){
        this.onDoubleClickSongCallback = callback;
    }

    public ArrayList<Songs> getSelectedSongs(){
        Songs[] songsArray = (Songs[]) songListSelectorTv.getSelectionModel().getSelectedItems().toArray(new Songs[]{});
        ArrayList<Songs> songs = new ArrayList<>(Arrays.asList(songsArray));
        return songs;
    }

    public Songs getRandom(){
        if(songListSelectorTv == null) return null;

        if(songListSelectorTv.getItems().size() == 1 || songListSelectorTv.getItems().size() == 0) return null;

        int i = NumbersUtils.rand(0, songListSelectorTv.getItems().size()-1);

        while(i == songListSelectorTv.getSelectionModel().getSelectedIndex()){
            i = NumbersUtils.rand(0, songListSelectorTv.getItems().size()-1);
        }

        songListSelectorTv.getSelectionModel().select(i);
        songListSelectorTv.scrollTo(i);
        Songs song = getSelectedSong();
        return song;
    }

    public Integer songCount(){
        if(songListSelectorTv != null){
            return songListSelectorTv.getItems().size();
        }else {
            return 0;
        }
    }

    public Songs goToFirst(){
        if(songListSelectorTv == null) return null;
        songListSelectorTv.getSelectionModel().select(0);
        songListSelectorTv.scrollTo(0);
        return getSelectedSong();
    }

    public Songs goToPrevious(){
        if(songListSelectorTv == null) return null;

        Integer i = songListSelectorTv.getSelectionModel().getSelectedIndex();

        if(i == -1){
            if(songListSelectorTv.getItems().size() > 0){
                songListSelectorTv.getSelectionModel().select(0);
                songListSelectorTv.scrollTo(0);
                i = 1;
            }else{
                return null;
            }
        }

        if((i-1) > -1){
            songListSelectorTv.getSelectionModel().select(i-1);
            songListSelectorTv.scrollTo(i-1);
            Songs song = getSelectedSong();
            return song;
        }
        return null;
    }

    public Songs goToNext(){
        if(songListSelectorTv == null) return null;

        Integer i = songListSelectorTv.getSelectionModel().getSelectedIndex();

        if(i == -1){
            if(songListSelectorTv.getItems().size() > 0){
                songListSelectorTv.getSelectionModel().select(0);
                songListSelectorTv.scrollTo(0);
            }else{
                return null;
            }
        }

        if((i+1) < songListSelectorTv.getItems().size()){
            songListSelectorTv.getSelectionModel().select(i+1);
            songListSelectorTv.scrollTo(i+1);
            Songs song = getSelectedSong();
            return song;
        }

        return null;
    }

    private Songs getSelectedSong(){
        if(songListSelectorTv.getSelectionModel().getSelectedItem() == null) return null;
        return (Songs) songListSelectorTv.getSelectionModel().getSelectedItem();
    }

    public void setContextMenuCallBack(Callback<Map.Entry<String, Songs>, Void> callback){
        onContextMenuCallback = callback;
    }

    public void setOnSongSelected(Callback<Songs, Void> callback){
        this.onSelectedSongCallback = callback;
    }

    public void setSongList(iterator.Iterator<Songs> songs){
        this.songs = songs;
        refreshPlayListSongs();
    }

    public void refreshPlayListSongs(){
        songListSelectorTv.getItems().clear();
        if(songs != null){
            while(songs.hasNext()){
                songListSelectorTv.getItems().add(songs.next());
            }
        }
    }
}
