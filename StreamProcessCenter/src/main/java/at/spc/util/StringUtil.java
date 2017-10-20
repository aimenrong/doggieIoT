package at.spc.util;

/**
 * Created by Terry LIANG on 2017/10/1.
 */
public class StringUtil {
    public static String extractServiceId(String str) {
        int index = str.indexOf("://");
        return str.substring(index + "://".length());
    }

}
