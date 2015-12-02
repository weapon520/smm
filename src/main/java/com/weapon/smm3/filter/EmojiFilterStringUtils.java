package com.weapon.smm3.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by weapon on 2015-11-25.
 */
public class EmojiFilterStringUtils {
    public static String cleanEmoji(String value) {
        Pattern unicodeOutliers = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(value);
        return unicodeOutlierMatcher.replaceAll("");
    }
}
