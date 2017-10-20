package com.at.amq.hc;

import java.util.IllegalFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Terry LIANG on 2017/10/1.
 */
public class RegexUtil {
    private static Pattern serviceIdPattern = Pattern.compile("(hc\\.service)\\.([0-9]+)\\.(id)");
    private static Pattern servicePortPatter = Pattern.compile("(hc\\.service)\\.([0-9]+)\\.(port)");
    private static Pattern sequencePatter = Pattern.compile("(hc\\.service)\\.([0-9]+)\\.(\\w+)");

    public static boolean isServiceId(String str) {
        return serviceIdPattern.matcher(str).find();
    }

    public static boolean isServicePort(String str) {
        return servicePortPatter.matcher(str).find();
    }

    public static int parseSequence(String str) {
        Matcher matcher = sequencePatter.matcher(str);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(2));
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(parseSequence("hc.service.10.id"));
    }
}
