package com.twelfthmile.yuga.utils;


import com.twelfthmile.yuga.Yuga;
import com.twelfthmile.yuga.types.*;

/**
 * Created by johnjoseph on 19/03/17.
 */

public class Util {

    public static boolean isHour(char c1, char c2) {
        return (((c1 == '0' || c1 == '1') && isNumber(c2)) || (c1 == '2' && (c2 == '0' || c2 == '1' || c2 == '2' || c2 == '3' || c2 == '4')));
    }

    public static boolean isNumber(char c) {
        return c >= 48 && c <= 57;
    }

    public static boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++)
            if (!isNumber(s.charAt(i)))
                return false;
        return true;
    }

    public static boolean isDateOperator(char c) {
        return c == Constants.CH_SLSH || c == Constants.CH_HYPH || c == Constants.CH_SPACE;
    }

    public static boolean isDelimeter(char c) {
        return c == Constants.CH_SPACE || c == Constants.CH_FSTP || c == Constants.CH_COMA;
    }

    public static boolean isTimeOperator(char c) {
        return c == Constants.CH_COLN; //colon
    }

    public static Pair<Integer, String> checkTypes(String type, String word) {
        int i;
        GenTrie t = Yuga.root.next.get(type);
        for (i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (t.leaf && !t.next.containsKey(ch) && isTypeEnd(ch))
                return new Pair<Integer, String>(i - 1, t.token);
            if (t.child && t.next.containsKey(ch)) {
                t = t.next.get(ch);
            } else break;
        }
        if (t.leaf && i == word.length())
            return new Pair<Integer, String>(i - 1, t.token);
        return null;
    }

    private static boolean isTypeEnd(char ch) {
        return (isNumber(ch) || ch == Constants.CH_FSTP || ch == Constants.CH_SPACE || ch == Constants.CH_HYPH || ch == Constants.CH_COMA || ch == Constants.CH_SLSH || ch == Constants.CH_RBKT || ch == Constants.CH_PLUS || ch == Constants.CH_STAR || ch == '\r' || ch == '\n');
    }

    public static boolean isAlpha(char c) {
        return ((c >= 65 && c <= 90) || (c >= 97 && c <= 122));
    }

    public static boolean isUpperAlpha(char c) {
        return (c >= 65 && c <= 90);
    }

    public static boolean isLowerAlpha(char c) {
        return (c >= 97 && c <= 122);
    }
}
