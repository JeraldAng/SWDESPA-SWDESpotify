package models;

import models.Model;
import models.Playlists;
import models.Users;

import java.util.HashMap;

public class Highlights extends Model {
    public Integer highlight_id;
    public Integer playlist_id;
    public Integer user_id;
    public String name;

    @Override
    public String setPKField() {
        return "highlight_id";
    }
    @Override
    public String setTableName() {
        return "highlights";
    }

    @Override
    public HashMap<Class, RelationshipType> setRelationships() {
        HashMap<Class, RelationshipType> relationships = new HashMap<>();

        relationships.put(Users.class, RelationshipType.belongsTo);
        relationships.put(Playlists.class, RelationshipType.hasOne);

        return relationships;
    }
}
