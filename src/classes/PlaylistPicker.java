package classes;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import iterator.Iterator;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import models.Playlists;
import models.Songs;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistPicker {

    private TableView playlistTv;
    private HashMap<String, String> contextMenus;

    private Callback<Map.Entry<String, Playlists>, Void> onContextMenuCallback;
    private Callback<Playlists, Void> onSelectedPlaylistCallback;

    private Iterator<Playlists> playlists;

    public PlaylistPicker(TableView playlistTv, HashMap<String, String> contextMenus){

        this.playlistTv = playlistTv;
        this.contextMenus = contextMenus;

        TableColumn coverColumn = new TableColumn("Playlists Name");
        coverColumn.setCellValueFactory(new PropertyValueFactory<Playlists, Object>("name"));
        TableColumn nameColumn = new TableColumn("Creator");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Playlists, Object>("creator"));
        TableColumn cteatedColumn = new TableColumn("Date Created");
        cteatedColumn.setCellValueFactory(new PropertyValueFactory<Playlists, Object>("date"));

        this.playlistTv.getColumns().addAll(coverColumn, nameColumn , cteatedColumn);

        playlistTv.setRowFactory(new Callback<TableView, TableRow>() {
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
                                Playlists playlists = getSelectedPlaylist();

                                if (playlists != null) {
                                    if (onContextMenuCallback != null) {
                                        Map.Entry<String, Playlists> entry = new AbstractMap.SimpleEntry<String, Playlists>(menuItem.getId(), playlists);
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
                            if (playlistTv.getItems().size() > 0) {
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

        playlistTv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY){
                    return;
                }

                if(event.getTarget() instanceof TableColumnHeader){
                    return;
                }

                Playlists playlists = getSelectedPlaylist();
                if(onSelectedPlaylistCallback != null){
                    onSelectedPlaylistCallback.call(playlists);
                }

            }
        });

    }

    public void setOnSelectedPlaylistCallback(Callback<Playlists, Void> callback){
        this.onSelectedPlaylistCallback = callback;
    }

    public Playlists getSelectedPlaylist(){
        if(playlistTv.getSelectionModel().getSelectedItem() == null) return null;
        return (Playlists) playlistTv.getSelectionModel().getSelectedItem();
    }

    public void setContextMenuCallBack(Callback<Map.Entry<String, Playlists>, Void> callback){
        onContextMenuCallback = callback;
    }

    public void setPlaylistList(Iterator<Playlists> playlists){
        this.playlists = playlists;
        refreshAlbumsList();
    }

    public void refreshAlbumsList(){
        playlistTv.getItems().clear();
        if(playlists != null){
            while(playlists.hasNext()){
                playlistTv.getItems().add(playlists.next());
            }
        }
    }
}
