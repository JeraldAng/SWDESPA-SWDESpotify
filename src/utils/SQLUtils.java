package utils;

import com.mysql.cj.MysqlType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SQLUtils {

    private static HashMap<String, Connection> connections = new HashMap<>();
    private static Connection defaultConnection;
    private static Integer lastInsertId;
    private static String error;
    private static String errorCode;

    public static String getLastError() {
        String e = error;
        error = null;
        return e;
    }

    public static void setDefaultConnection(String connectionName) throws Exception {
        if (getConnection(connectionName) != null) {
            defaultConnection = getConnection(connectionName);
            return;
        }
        throw new Exception();
    }

    public static Connection getDefaultConnection() throws Exception {
        if (defaultConnection != null) {
            return defaultConnection;
        }
        throw new Exception();
    }

    public static void begin() throws SQLException {
        if (defaultConnection != null) {
            defaultConnection.setAutoCommit(false);
        }
    }

    public static void commit() throws SQLException {
        if (defaultConnection != null) {
            defaultConnection.commit();
            defaultConnection.setAutoCommit(false);
        }
    }

    public static void rollback() throws SQLException {
        if (defaultConnection != null) {
            defaultConnection.rollback();
        }
    }

    public static void addConnection(String connectionName, String host, String database, String username, String password) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        String url = "jdbc:MySQL://"+host+"/"+database+"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
        Connection con = DriverManager.getConnection(url, username, password);
        connections.put(connectionName, con);
        setDefaultConnection(connectionName);
    }

    public static ResultSet fetch(Connection connection, String sql) throws Exception {
        return fetch(connection, sql, null);
    }

    public static ResultSet fetch(Connection connection, String sql, ArrayList<Object> bind) throws Exception {
        error = null;

        PreparedStatement stmt = connection.prepareStatement(sql);

        Integer count = 1;

        if(bind == null){
            bind = new ArrayList<>();
        }

        for(Object value : bind){
            if(value == null) {
                stmt.setNull(count, 0);
            }else if(value.getClass() == Integer.class){
                stmt.setInt(count, (Integer)value);
            }else if(value.getClass() == Float.class){
                stmt.setFloat(count, (Float)value);
            }else if(value.getClass() == Double.class){
                stmt.setDouble(count, (Double)value);
            }else if(value.getClass() == byte[].class) {
                stmt.setBytes(count, (byte[]) value);
            }else if(value.getClass() == String.class){
                stmt.setString(count, (String)value);
            }else if(value.getClass() == Blob.class) {
                stmt.setBlob(count, (Blob)value);
            }
            count += 1;
        }

        ResultSet rs = stmt.executeQuery();
        return rs;
    }

    public static boolean execute(String sql, ArrayList<Object> bind) throws Exception {
        return execute(getDefaultConnection(), sql, bind);
    }

    public static boolean execute(String connectionName, String sql, ArrayList<Object> bind) throws Exception {
        return execute(getConnection(connectionName), sql, bind);
    }

    //INSERT UPDATE DELETE
    public static boolean execute(Connection connection, String sql, ArrayList<Object> bind) throws Exception {
        error = null;

        PreparedStatement stmt = connection.prepareStatement(sql);

        if(bind == null){
            bind = new ArrayList<>();
        }

        Integer count = 1;
        for(Object value : bind){
            if(value == null) {
                stmt.setNull(count, 0);
            }else if(value.getClass() == Integer.class){
                stmt.setInt(count, (Integer)value);
            }else if(value.getClass() == Float.class){
                stmt.setFloat(count, (Float)value);
            }else if(value.getClass() == Double.class){
                stmt.setDouble(count, (Double)value);
            }else if(value.getClass() == byte[].class){
                stmt.setBytes(count, (byte[])value);
            }else if(value.getClass() == String.class){
                stmt.setString(count, (String)value);
            }else if(value.getClass() == Blob.class) {
                stmt.setBlob(count, (Blob)value);
            }
            count += 1;
        }

        return stmt.execute();
    }

    public static ArrayList<HashMap<String, String>> fetchArray(Connection connection, String sql, ArrayList<Object> bind) throws Exception {
        ResultSet rs = fetch(connection, sql, bind);

        ArrayList<HashMap<String, String>> resultSetArray = new ArrayList<>();

        while(rs.next()){
            HashMap<String, String> row = new HashMap<>();
            for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++){
                String column = rs.getMetaData().getColumnName(i);
                row.put(column, rs.getString(i));
            }
        }

        return resultSetArray;
    }

    public static boolean delete(String table, String where, ArrayList<Object> bind) throws Exception {
        return delete(getDefaultConnection(), table, where, bind);
    }

    public static boolean delete(String connection, String table, String where, ArrayList<Object> bind) throws Exception {
        return delete(getConnection(connection), table, where, bind);
    }

    public static boolean delete(Connection connection, String table, String where, ArrayList<Object> bind) throws Exception {
        error = null;
        String sql = "DELETE FROM "+table;

        if(where != null){
            sql += " WHERE " + where;
        }

        return SQLUtils.execute(connection, sql, bind);
    }

    public static void update(String connection, String table, HashMap<String, Object> data) throws SQLException {
        update(getConnection(connection), table, data, new ArrayList<>(), null);
    }

    public static void update(String connection, String table, HashMap<String, Object> data, ArrayList<String> exclusions, String where) throws SQLException {
        update(getConnection(connection), table, data, exclusions, where);
    }

    public static void update(Connection connection, String table, HashMap<String, Object> data, ArrayList<String> exclusions, String where) throws SQLException {
        error = null;

        String sql = "UPDATE `"+table+"` SET ";

        ArrayList<String> keys = new ArrayList<>(); //USED FOR SET STATEMENT

        Object[] values = new Object[data.values().size()];

        Integer count = 0;


        for(Map.Entry<String, Object> entry : data.entrySet()){
            if(!exclusions.contains(entry.getKey())) {
                keys.add(entry.getKey());
                values[count] = entry.getValue();
                count += 1;
            }
        }

        for(String exclusion : exclusions){
            values[count] = data.get(exclusion);
            count += 1;
        }

        String set = "`"+ String.join("` = ?, `", keys.toArray(new String[keys.size()]))+"` = ?";

        sql += set;

        if(where != null){
            sql += " WHERE "+where;
        }

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        for(int i = 0; i < values.length; i++){
            Object value = values[i];
            if(value == null) {
                preparedStatement.setNull(i+1, 0);
            }else if(value.getClass() == Integer.class){
                preparedStatement.setInt(i+1, (Integer)value);
            }else if(value.getClass() == Float.class){
                preparedStatement.setFloat(i+1, (Float)value);
            }else if(value.getClass() == Double.class){
                preparedStatement.setDouble(i+1, (Double)value);
            }else if(value.getClass() == byte[].class){
                preparedStatement.setBytes(i+1, (byte[])value);
            }else if(value.getClass() == String.class){
                preparedStatement.setString(i+1, (String)value);
            }else if(value.getClass() == Blob.class) {
                preparedStatement.setBlob(i+1, (Blob)value);
            }
        }

        preparedStatement.execute();
    }

    public static void insert(String table, HashMap<String, Object> data) throws Exception {
        insert(defaultConnection, table, data);
    }

    public static void insert(String connectionName, String table, HashMap<String, Object> data) throws Exception {
        insert(getConnection(connectionName), table, data);
    }

    public static Integer getLastInserID(){
        return lastInsertId;
    }

    public static void insert(Connection connection, String table, HashMap<String, Object> data) throws Exception {
        try {
            error = null;
            if (data == null || data.size() == 0) {
                return;
            }

            String fields = "`" + String.join("`, `", data.keySet().toArray(new String[data.keySet().size()])) + "`";

            String[] placeHolders = new String[data.size()];
            Arrays.fill(placeHolders, "?");

            String placeHoldersStr = String.join(", ", placeHolders);

            String sql = "INSERT INTO " + table + " (" + fields + ") VALUES (" + placeHoldersStr + ")";

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            Integer count = 1;

            for (Object value : data.values()) {
                if (value == null) {
                    preparedStatement.setObject(count, null);
                } else if (value.getClass() == Integer.class) {
                    preparedStatement.setInt(count, (Integer) value);
                } else if (value.getClass() == Float.class) {
                    preparedStatement.setFloat(count, (Float) value);
                } else if (value.getClass() == Double.class) {
                    preparedStatement.setDouble(count, (Double) value);
                } else if (value.getClass() == byte[].class) {
                    preparedStatement.setBytes(count, (byte[]) value);
                }else if (value.getClass() == String.class) {
                    preparedStatement.setString(count, (String) value);
                } else if (value.getClass() == Blob.class) {
                    preparedStatement.setBlob(count, (Blob) value);
                }
                count += 1;
            }

            preparedStatement.execute();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                lastInsertId = rs.getInt(1);
            }
        }catch (Exception e){
            error = e.getMessage();
            throw e;
        }
    }

    public static Connection getConnection(String connectionName){
        if(connections.containsKey(connectionName)){
            return connections.get(connectionName);
        }
        return null;
    }
}
