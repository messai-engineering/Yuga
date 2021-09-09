package com.twelfthmile.yuga.utils;

import com.twelfthmile.yuga.types.GenTrie;
import com.twelfthmile.yuga.types.Pair;
import com.twelfthmile.yuga.types.RootTrie;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
        if (s == null || s.length() == 0)
            return false;
        for (int i = 0; i < s.length(); i++)
            if (!isNumber(s.charAt(i)))
                return false;
        return true;
    }

    public static boolean isDateOperator(char c) {
        return c == Constants.CH_SLSH || c == Constants.CH_HYPH || c == Constants.CH_SPACE;
    }

    public static boolean isDelimiter(char c) {
        return c == Constants.CH_SPACE || c == Constants.CH_FSTP || c == Constants.CH_COMA || c == Constants.CH_RBKT;
    }

    public static boolean isTimeOperator(char c) {
        return c == Constants.CH_COLN; //colon
    }

    public static Pair<Integer, String> checkTypes(RootTrie root, String type, String word) {
        int i;
        GenTrie t = root.next.get(type);
        for (i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (t.leaf && !t.next.containsKey(ch) && isTypeEnd(ch))
                return new Pair<>(i - 1, t.token);
            if (t.child && t.next.containsKey(ch)) {
                t = t.next.get(ch);
            } else{
                break;
            }
        }
        if (t.leaf && i == word.length())
            return new Pair<>(i - 1, t.token);

        // All months across languages start with same alphabet.While supporting new language,review this condition.
        // if the 1st char doesnt match the trie,its not a month. Save further overhead.
        if(type.equals("FSA_MONTHS") && i<1){
            return null;
        }

        if(type.equals("FSA_MONTHS") || type.equals("FSA_DAYS")){
            return checkNonEngMonth(i,word,type);
        }

        return null;
    }

    public static Pair<Integer, String> checkNonEngMonth(int i,String word,String type){
        HashMap map = type.equals("FSA_MONTHS")? Constants.month : Constants.day;
        char ch = 0;
        if (i != word.length()){
            while(i < word.length() && !isTypeEnd(ch)){
                ch = word.charAt(i);
                i++;
            }
        }
        // It could be just "June" Or "June, 31".
        String toCheck = (i==word.length() && !isTypeEnd(ch))? word.substring(0,i) : word.substring(0,i-1);
        Iterator<Map.Entry<Set<String>,String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Set<String>,String> pair = it.next();
            if (pair.getKey().contains(toCheck) ){
                return new Pair<>(i - 1, pair.getValue());
            }
        }
        return null;
    }

    public static boolean isTypeEnd(char ch) {
        return (isNumber(ch) || ch == Constants.CH_FSTP || ch == Constants.CH_SPACE || ch == Constants.CH_HYPH || ch == Constants.CH_COMA || ch == Constants.CH_SLSH || ch == Constants.CH_RBKT || ch == Constants.CH_PLUS || ch == Constants.CH_STAR || ch == '\r' || ch == '\n' || ch =='\'');
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

    public static boolean isUpperAlpha(String str) {
        if (str == null || str.length() == 0)
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (!isUpperAlpha(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isLowerAlpha(String str) {
        if (str == null || str.length() == 0)
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (!isLowerAlpha(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isAlpha(String str) {
        if (str == null || str.length() == 0)
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (!isAlpha(str.charAt(i)))
                return false;
        }
        return true;
    }

}
