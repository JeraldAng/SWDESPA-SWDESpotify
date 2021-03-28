package models;

import utils.SQLUtils;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

abstract class Model {

    protected enum RelationshipType {
        hasMany, hasOne, belongsTo
    }

    private ArrayList<String> messages = new ArrayList<>();

    public String setTableName(){
        return getClass().getSimpleName().toLowerCase();
    }

    public Object findOne(){
        return findOne("1=1");
    }

    public Object findOne(String where){
        return findOne(where, new ArrayList<>());
    }

    public Object findOne(String where, ArrayList<Object> bind){
        ArrayList<?> result = find("*", where, bind, "1", null);
        if(result != null && result.size() > 0){
            return result.get(0);
        }else {
            return null;
        }
    }

    public ArrayList<?> find(String s, String s1, ArrayList<Object> bind){
        return find("*","1=1", new ArrayList<>(), null, null);
    }

    public ArrayList<?> find(String where){
        return find("*", where, new ArrayList<>(), null, null);
    }

    public ArrayList<?> find(String where, ArrayList<Object> bind){
        return find("*", where, bind, null, null);
    }

    public ArrayList<?> find(HashMap<String, Object> options){

        String fields = null;
        if(options.containsKey("fields")){
            fields = (String) options.get("fields");
        }
        String where = null;
        if(options.containsKey("where")){
            where = (String) options.get("where");
        }
        ArrayList<Object> bind = null;
        if(options.containsKey("bind")) {
            bind =  (ArrayList<Object>) options.get("bind");
        }
        String limit = null;
        if(options.containsKey("limit")){
            limit = (String) options.get("limit");
        }

        String order = null;
        if(options.containsKey("order")){
            order = (String) options.get("order");
        }

        return find(fields, where, bind, limit, order);
    }

    public ArrayList<?> find(String fields, String where, ArrayList<Object> bind, String limit, String order){

        ArrayList<Object> result = new ArrayList<>();

        if(bind == null){
            bind = new ArrayList<>();
        }

        if(fields == null){
            fields = "*";
        }

        if(where == null){
            where = "1";
        }

        String sql = "SELECT "+fields+" FROM "+setTableName()+" WHERE "+where;

        if(order != null){
            sql += " ORDER BY " + order;
        }

        if(limit != null){
            sql += " LIMIT "+limit;
        }

        try {

            ResultSet rs = SQLUtils.fetch(setConnection(), sql, bind);

            if(rs.next()) {
                do{

                    Class class1 = Class.forName(getClass().getName());
                    Object obj = class1.newInstance();

                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {

                        String column = rs.getMetaData().getColumnName(i);

                        Field field = obj.getClass().getField(column);

                        String type = rs.getMetaData().getColumnTypeName(i).toLowerCase();

                        if (type.equals("int") || type.equals("bit") || type.equals("tinyint")) {
                            field.set(obj, rs.getInt(i));
                        } else if(type.equals("varbinary")) {
                            field.set(obj, rs.getBytes(i));
                        }else {
                            field.set(obj, rs.getString(i));
                        }
                    }

                    result.add(obj);
                }while (rs.next());
            }else{
                return null;
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean delete(){
        try{

            Field f = getClass().getDeclaredField(setPKField());

            if (f.get(this) != null) {

                String where = "`" + setPKField() + "` = ?";

                ArrayList<Object> bind = new ArrayList<>();

                bind.add(f.get(this));

                SQLUtils.delete(setConnection(), setTableName(), where, bind);
            }else{
                throw new Exception("PK Field is null");
            }
        }catch (Exception e){
            addMessage(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean delete(String where){
        try{
            SQLUtils.delete(setConnection(), setTableName(), where, null);
        }catch (Exception e){
            addMessage(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean delete(String where, ArrayList<Object> bind){
        try{
            SQLUtils.delete(setConnection(), setTableName(), where, bind);
        }catch (Exception e){
            addMessage(e.getMessage());
            return false;
        }
        return true;
    }

    //BEST ALL ♥.♥ <3

    public boolean save(){
        try {
            if(!validate()){
                return false;
            }
            Field f = getClass().getDeclaredField(setPKField());

            if (f.get(this) != null) {

                HashMap<String, Object> data = new HashMap<>();

                for (Field field : getClass().getDeclaredFields()) {
                    try {
                        //if(field.get(this) != null) {
                            data.put(field.getName(), field.get(this));
                        //}
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                ArrayList<String> exclusion = new ArrayList<>();

                exclusion.add(setPKField());

                try {
                    SQLUtils.update(setConnection(), setTableName(), data, exclusion, "`" + setPKField() + "` = ?");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new Exception("PK field is null.");
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public String setPKField(){
        return "id";
    }

    public boolean create(){
        if(!validate()){
            return false;
        }
        HashMap<String, Object> data = new HashMap<>();

        for (Field field : getClass().getDeclaredFields()) {
            try {
                if(field.get(this) != null) {
                    data.put(field.getName(), field.get(this));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            SQLUtils.insert(setConnection(), setTableName(), data);

            Field f = getClass().getDeclaredField(setPKField());
            f.set(this, SQLUtils.getLastInserID());
            return true;
        } catch (Exception e) {
            addMessage(SQLUtils.getLastError());
            return false;
        }
    }

    public boolean validate(){
        return true;
    }

    public Connection setConnection() throws Exception {
        return SQLUtils.getDefaultConnection();
    }

    public void addMessage(String message){
        messages.add(message);
    }

    public ArrayList<String> getMessages(){
        return messages;
    }

    public Object get(Class c){

        HashMap<Class, RelationshipType> relationships = setRelationships();

        Object results = new Object();

        if(relationships.containsKey(c)){
            try {
                Model model = (Model) c.newInstance();

                //Users u = new Users();

                //setPKField() //song_id
                //u.findOne("user_id = ?");
                //u.setPKField();

                String pk = "";
                String value = "";

                ArrayList<Object> bind = new ArrayList<>();

                switch (relationships.get(c)){
                    case belongsTo:
                    case hasOne:
                        pk = model.setPKField();
                        //user_id
                        value = String.valueOf(getClass().getDeclaredField(model.setPKField()).get(this));
                        //7 getClass().getDeclaredField("user_id").get(this)
                        bind.add(value);
                        //u.findOne(`user_id` = ?, 7);
                        results = model.findOne("`"+pk+"` = ?", bind);
                        break;
                    case hasMany:
                        pk = setPKField();
                        value = String.valueOf(getClass().getDeclaredField(setPKField()).get(this));
                        bind.add(value);
                        results = model.find("`"+pk+"` = ?", bind);
                        break;
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }

        return results;
    }

    public HashMap<Class, RelationshipType> setRelationships(){
        return new HashMap<>();
    }

}
