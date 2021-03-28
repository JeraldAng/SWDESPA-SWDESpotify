package models;

import java.util.ArrayList;
import java.util.HashMap;

public class Follows extends Model {

    public Integer follow_id;
    public Integer user_id;
    public Integer user_id_followed;
    public String date_followed;

    @Override
    public String setPKField() {
        return "follow_id";
    }

    @Override
    public HashMap<Class, RelationshipType> setRelationships() {
        HashMap<Class, RelationshipType> relationships = new HashMap<>();

        relationships.put(Users.class, RelationshipType.belongsTo);

        return relationships;
    }

    public Users getUser(){
        return (Users) get(Users.class);
    }

    public Users getUserFollowed(){
        ArrayList<Object> bind = new ArrayList<>();
        bind.add(user_id_followed);
        Users user = (Users) new Users().findOne("user_id = ?", bind);
        return user;
    }
}
