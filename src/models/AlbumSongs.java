package models;

import java.util.HashMap;

public class AlbumSongs extends Model {

    public Integer album_song_id;
    public Integer album_id;
    public Integer song_id;

    @Override
    public String setTableName() {
        return "album_songs";
    }

    @Override
    public String setPKField() {
        return "album_song_id";
    }

    @Override
    public HashMap<Class, RelationshipType> setRelationships() {
        HashMap<Class, RelationshipType> relationships = new HashMap<>();

        relationships.put(Albums.class, RelationshipType.belongsTo);
        relationships.put(Songs.class, RelationshipType.hasOne);

        return relationships;
    }
}
