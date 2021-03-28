package utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringUtils {

    public static String toMySQLDate(LocalDate date){
        if(date == null){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return formatter.format(date);
    }

    public static String toPrettyDate(String date){
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("MMMM dd, yyyy");

        try {
            String reformattedStr = myFormat.format(fromUser.parse(date));
            return reformattedStr;
        } catch (ParseException e) {
            return date;
        }
    }

    public static String toPrettyDateTime(String date){
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        SimpleDateFormat myFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");

        try {
            String reformattedStr = myFormat.format(fromUser.parse(date));
            return reformattedStr;
        } catch (ParseException e) {
            return date;
        }
    }

    public static String getFileWithoutExtension (String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(0, pos);
    }

    public static String toCamelCase(String s){
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts){
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

    public static String toDisplayCase(String s) {

        final String ACTIONABLE_DELIMITERS = " '-/";

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0);
        }
        return sb.toString();
    }

    public static String urlEncode(String toString) {
        try {
            return URLEncoder.encode(toString, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
