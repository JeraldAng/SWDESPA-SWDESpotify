package models;

import java.util.HashMap;

public class Session {

    private static HashMap<String, Object> sessionData = new HashMap<>();

    public static void add(String key, Object value){
        sessionData.put(key, value);
    }

    public static void update(String key, Object newValue){
        add(key, newValue);
    }

    public static Object get(String key){
        if(sessionData.containsKey(key)) {
            return sessionData.get(key);
        }else{
            return null;
        }
    }

    public static void del(String key){
        if(sessionData.containsKey(key)) {
            sessionData.remove(key);
        }
    }
}
