package models;

import utils.SQLUtils;
import utils.StringUtils;

import java.util.HashMap;

public class Playlists extends Model {

    public Integer playlist_id;
    public Integer user_id;
    public String name;
    public String date_created;

    @Override
    public String setPKField() {
        return "playlist_id";
    }

    @Override
    public HashMap<Class, RelationshipType> setRelationships() {
        HashMap<Class, RelationshipType> relationships = new HashMap<>();

        relationships.put(Users.class, RelationshipType.belongsTo);
        relationships.put(PlaylistSongs.class, RelationshipType.hasMany);

        return relationships;
    }

    @Override
    public boolean validate() {

        if(name.isEmpty()){
            addMessage("Please enter playlist name.");
        }

        return getMessages().size() == 0;
    }

    public String getName(){
        return this.name;
    }

    public String getCreator(){
        Users user = (Users) get(Users.class);

        if(user == null){
            return "Unknown";
        }else{
            Users currentUser = (Users) Session.get("user");
            if(currentUser == null) {
                return user.username;
            }else{
                if(currentUser.user_id == user.user_id){
                    return "Me";
                }else{
                    return user.username;
                }
            }
        }
    }

    public String getDate(){
        return StringUtils.toPrettyDateTime(date_created);
    }
}
