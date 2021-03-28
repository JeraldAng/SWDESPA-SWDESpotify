package models;

import iterator.Iterator;
import iterator.UsersNotificationCollection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.SQLUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Users extends Model {

    public Integer user_id;
    public Integer user_type;
    public String username;
    public String password;
    public String email;
    public String birth_date;
    public String account_date_created;

    @Override
    public String setPKField() {
        return "user_id";
    }

    @Override
    public HashMap<Class, RelationshipType> setRelationships() {
        HashMap<Class, RelationshipType> relationships = new HashMap<>();

        relationships.put(Users.class, RelationshipType.belongsTo);
        relationships.put(Albums.class, RelationshipType.hasMany);
        relationships.put(Playlists.class, RelationshipType.hasMany);
        relationships.put(Follows.class, RelationshipType.hasMany);
        relationships.put(Songs.class, RelationshipType.hasMany);
        relationships.put(Activities.class, RelationshipType.hasMany);

        return relationships;
    }

    @Override
    public boolean validate() {
        if(username.isEmpty()){
            addMessage("Please enter username.");
        }
        if(password.isEmpty()){
            addMessage("Please enter your password.");
        }
        if(email.isEmpty()){
            addMessage("Please enter your email.");
        }
        if(birth_date == null || birth_date.isEmpty()){
            addMessage("Please select your birth date");
        }
        if(SQLUtils.getLastError() != null && !SQLUtils.getLastError().isEmpty()){
            addMessage(SQLUtils.getLastError());
        }
        return getMessages().size() == 0;
    }

    public Integer getFollowers(){
        Follows follow = new Follows();

        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user_id);

        ArrayList<Follows> follows = (ArrayList<Follows>) follow.find("user_id_followed = ?", bind);

        if(follows != null){
            return follows.size();
        }

        return 0;
    }

    public ArrayList<Follows> getFollowersList(){
        Follows follow = new Follows();

        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user_id);

        ArrayList<Follows> follows = (ArrayList<Follows>) follow.find("user_id_followed = ?", bind);

        return follows;
    }

    public String getName(){
        return username;
    }

    public ImageView getFollowStatus(){

        ImageView iv = new ImageView(new Image("/resources/images/black-heart.png"));

        boolean followed = getFollowStatusBool();

        if(followed){
            iv.setImage(new Image("/resources/images/red-heart.png"));
        }

        iv.setFitWidth(20);
        iv.setFitHeight(20);
        iv.setPreserveRatio(false);

        return iv;
    }

    public boolean follow(){
        Follows follow = new Follows();

        Users currentUser = (Users) Session.get("user");

        follow.user_id = currentUser.user_id;
        follow.user_id_followed = user_id;

        return follow.create();
    }

    public void unfollow(){
        Follows follow = new Follows();

        Users currentUser = (Users) Session.get("user");

        ArrayList<Object> bind = new ArrayList<>();
        bind.add(currentUser.user_id);
        bind.add(user_id);

        follow = (Follows) follow.findOne("user_id = ? AND user_id_followed = ?", bind);

        if(follow !=  null){
            follow.delete();
        }
    }

    public boolean getFollowStatusBool(){

        Follows follow = new Follows();

        Users currentUser = (Users) Session.get("user");

        ArrayList<Object> bind = new ArrayList<>();
        bind.add(currentUser.user_id);
        bind.add(user_id);

        follow = (Follows) follow.findOne("user_id = ? AND user_id_followed = ?", bind);

        if(follow !=  null){
            return true;
        }

        return false;
    }

    public Integer getAlbumsCount(){
        ArrayList<Albums> albums = (ArrayList<Albums>) get(Albums.class);
        if(albums != null){
            return albums.size();
        }
        return 0;
    }

    public void seen(){
        try {
            HashMap<String, Object> data = new HashMap<>();
            data.put("seen", "1");
            data.put("user_id", user_id);
            ArrayList<String> exclusion = new ArrayList<>();
            exclusion.add("user_id");
            SQLUtils.update(SQLUtils.getDefaultConnection(), "notifications", data, exclusion, "user_id = ?");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
