package models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Const;
import utils.SQLUtils;
import utils.StringUtils;

import java.io.File;
import java.util.HashMap;

public class Albums extends Model {

    public Integer album_id;
    public Integer user_id;
    public String album_name;
    public String album_cover;
    public String date_created;

    @Override
    public String setPKField() {
        return "album_id";
    }

    @Override
    public boolean validate() {
        if(album_name.isEmpty()){
            addMessage("Please enter album name.");
        }
//        if(album_cover == null){
//            addMessage("Please seleect album cover.");
//        }
        if(SQLUtils.getLastError() != null && !SQLUtils.getLastError().isEmpty()){
            addMessage(SQLUtils.getLastError());
        }
        return getMessages().size() == 0;
    }

    @Override
    public HashMap<Class, RelationshipType> setRelationships() {
        HashMap<Class, RelationshipType> relationships = new HashMap<>();

        relationships.put(Users.class, RelationshipType.belongsTo);
        relationships.put(AlbumSongs.class, RelationshipType.hasMany);

        return relationships;
    }

    public String getArtist(){
        Users currentUser = (Users) Session.get("user");
        Users user = (Users) get(Users.class);

        if(currentUser.user_id == user.user_id){
            return "Me";
        }

        return user.username;
    }

    public ImageView getCover(){
        ImageView iv = new ImageView(new Image("/resources/images/default-album-cover.jpg"));
        iv.setFitHeight(50);
        iv.setFitWidth(50);

        iv.setPreserveRatio(false);

        if(album_cover != null){
            try {
                iv.setImage(new Image(Const.httpBase + "/covers/" + StringUtils.urlEncode(album_cover)));
            }catch (Exception e){
                iv = new ImageView(new Image("/resources/images/default-album-cover.jpg"));
            }
        }
        return iv;
    }

    public String getName(){
        return album_name;
    }

    public String getDate(){
        return date_created;
    }

}
