package models;

import javafx.scene.image.Image;

import java.util.HashMap;

public class Songs extends Model {

    public Integer song_id;
    public Integer user_id;
    public Integer album_id;
    public String name;
    public String artist;
    public String album_name;
    public String genre;
    public String year;
    public String date_uploaded;
    public String location;

    @Override
    public String setPKField() {
        return "song_id";
    }

    public String getName(){
        return name;
    }

    public String getArtist(){
        Users user = (Users) get(Users.class);

        Users currentUser = (Users) Session.get("user");

        if(currentUser.user_id == user.user_id){
            return "Me";
        }

        return user.username;
    }

    public String getAlbum(){
        Albums album = (Albums) get(Albums.class);
        if(album == null) {
            if(album_name == null) {
                return "Unknown";
            }else {
                return album_name;
            }
        }else{
            return album.album_name;
        }
    }

    public String getYear(){
        return year;
    }
    public String getGenre(){
        return genre;
    }

    public Image getAlbumCover(){
        Albums album = (Albums) get(Albums.class);

        if(album != null){
            return album.getCover().getImage();
        }

        return null;
    }

    @Override
    public HashMap<Class, RelationshipType> setRelationships() {
        HashMap<Class, RelationshipType> relationships = new HashMap<>();

        relationships.put(Users.class, RelationshipType.belongsTo);
        relationships.put(Albums.class, RelationshipType.belongsTo);

        return relationships;
    }
}
