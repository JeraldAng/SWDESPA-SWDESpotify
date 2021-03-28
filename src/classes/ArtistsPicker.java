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
import models.Session;
import models.Users;

import javax.swing.text.html.ImageView;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArtistsPicker {

    private HashMap<String, String> contextMenus;
    private TableView usersTv;

    private Callback<Map.Entry<String, Users>, Void> onContextMenuCallback;
    private Callback<Users, Void> onSelectedUserCallback;

    private Iterator<Users> users;

    public ArtistsPicker(TableView usersTv, HashMap<String, String> contextMenus){

        this.usersTv = usersTv;
        this.contextMenus = contextMenus;

        Users user = (Users) Session.get("user");

        TableColumn followStatusColumn = new TableColumn("Followed");
        followStatusColumn.setCellValueFactory(new PropertyValueFactory<Users, ImageView>("followStatus"));

        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Users, String>("name"));

        TableColumn followersColumn = new TableColumn("Followers");
        followersColumn.setCellValueFactory(new PropertyValueFactory<Users, Integer>("followers"));

        TableColumn albumsCount = new TableColumn("No. of Albums");
        albumsCount.setCellValueFactory(new PropertyValueFactory<Users, Integer>("albumsCount"));

        usersTv.getColumns().addAll(followStatusColumn, nameColumn , followersColumn, albumsCount);

        if(user.user_type == 1){
            //followStatusColumn.setVisible(false);
        }

        usersTv.setRowFactory(new Callback<TableView, TableRow>() {
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
                                Users user = getSelectedUser();

                                if (user != null) {
                                    if (onContextMenuCallback != null) {
                                        Map.Entry<String, Users> entry = new AbstractMap.SimpleEntry<String, Users>(menuItem.getId(), user);
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
                            if (usersTv.getItems().size() > 0) {
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

        usersTv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY){
                    return;
                }

                if(event.getTarget() instanceof TableColumnHeader){
                    return;
                }

                Users user = getSelectedUser();
                if(onSelectedUserCallback != null){
                    onSelectedUserCallback.call(user);
                }

            }
        });
    }

    public void setUsersList(Iterator<Users> users){
        this.users = users;
        refreshAlbumsList();
    }

    public void refreshAlbumsList(){
        usersTv.getItems().clear();
        if(users != null){
            while(users.hasNext()){
                usersTv.getItems().add(users.next());
            }
        }
    }

    public Users getSelectedUser(){
        if(usersTv.getSelectionModel().getSelectedItem() == null) return null;
        return (Users) usersTv.getSelectionModel().getSelectedItem();
    }

    public void setContextMenuCallBack(Callback<Map.Entry<String, Users>, Void> callback){
        onContextMenuCallback = callback;
    }

    public void setOnSelectedUserCallback(Callback<Users, Void> callback){
        onSelectedUserCallback = callback;
    }
}
