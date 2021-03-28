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
import models.Albums;
import models.Songs;

import javax.swing.text.html.ImageView;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlbumPicker {

    private TableView albumsTv;

    private HashMap<String, String> contextMenus;

    private Callback<Map.Entry<String, Albums>, Void> onContextMenuCallback;
    private Callback<Albums, Void> onSelectedAlbumCallback;

    private Iterator<Albums> albums;

    public AlbumPicker (TableView albumsTv, HashMap<String, String> contextMenus){

        this.albumsTv = albumsTv;
        this.contextMenus = contextMenus;

        TableColumn coverColumn = new TableColumn("Album Cover");
        coverColumn.setCellValueFactory(new PropertyValueFactory<Albums, ImageView>("cover"));

        TableColumn nameColumn = new TableColumn("Album Name");
        nameColumn.setPrefWidth(100);
        nameColumn.setCellValueFactory(new PropertyValueFactory<Albums, Object>("name"));

        TableColumn artistColumn = new TableColumn("Artists");
        artistColumn.setCellValueFactory(new PropertyValueFactory<Albums, Object>("artist"));

        TableColumn cteatedColumn = new TableColumn("Date Created");
        cteatedColumn.setCellValueFactory(new PropertyValueFactory<Albums, Object>("date"));

        albumsTv.getColumns().addAll(coverColumn, nameColumn , artistColumn, cteatedColumn);

        albumsTv.setRowFactory(new Callback<TableView, TableRow>() {
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
                                Albums album = getSelectedAlbum();

                                if (album != null) {
                                    if (onContextMenuCallback != null) {
                                        Map.Entry<String, Albums> entry = new AbstractMap.SimpleEntry<String, Albums>(menuItem.getId(), album);
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
                            if (albumsTv.getItems().size() > 0) {
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

        albumsTv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY){
                    return;
                }

                if(event.getTarget() instanceof TableColumnHeader){
                    return;
                }

                Albums album = getSelectedAlbum();
                if(onSelectedAlbumCallback != null){
                    onSelectedAlbumCallback.call(album);
                }

            }
        });
    }

    public void setOnSelectedAlbumCallback(Callback<Albums, Void> callback){
        onSelectedAlbumCallback = callback;
    }

    public Albums getSelectedAlbum(){
        if(albumsTv.getSelectionModel().getSelectedItem() == null) return null;
        return (Albums) albumsTv.getSelectionModel().getSelectedItem();
    }

    public void setContextMenuCallBack(Callback<Map.Entry<String, Albums>, Void> callback){
        onContextMenuCallback = callback;
    }

    public void setAlbumsList(Iterator<Albums> albums){
        this.albums = albums;
        refreshAlbumsList();
    }

    public void refreshAlbumsList(){
        albumsTv.getItems().clear();
        if(albums != null){
            while(albums.hasNext()){
                albumsTv.getItems().add(albums.next());
            }
        }
    }
}
