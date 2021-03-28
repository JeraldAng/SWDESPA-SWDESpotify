package models;

import java.util.HashMap;

public class PlaylistSongs extends Model {

    public Integer playlist_song_id;
    public Integer playlist_id;
    public Integer song_id;

    @Override
    public String setPKField() {
        return "playlist_song_id";
    }

    @Override
    public String setTableName() {
        return "playlist_songs";
    }

    @Override
    public HashMap<Class, RelationshipType> setRelationships() {
        HashMap<Class, RelationshipType> relationships = new HashMap<>();

        relationships.put(Playlists.class, RelationshipType.belongsTo);
        relationships.put(Songs.class, RelationshipType.hasOne);

        return relationships;
    }
}
