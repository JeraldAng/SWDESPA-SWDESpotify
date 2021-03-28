package models;

import java.util.HashMap;

public class LikedSongs extends Model {

    public Integer liked_song_id;
    public Integer user_id;
    public Integer song_id;
    public String name;

    @Override
    public String setPKField() {
        return "liked_song_id";
    }

    @Override
    public String setTableName() {
        return "liked_songs";
    }

    @Override
    public HashMap<Class, RelationshipType> setRelationships() {
        HashMap<Class, RelationshipType> relationships = new HashMap<>();

        relationships.put(Users.class, RelationshipType.belongsTo);
        relationships.put(Songs.class, RelationshipType.hasOne);

        return relationships;
    }

}
