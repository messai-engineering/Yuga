package com.twelfthmile.yuga.utils;

import com.twelfthmile.yuga.types.GenTrie;
import com.twelfthmile.yuga.types.Pair;
import com.twelfthmile.yuga.types.RootTrie;
import com.twelfthmile.yuga.types.Trie;

import java.text.ParseException;
import java.util.*;

/**
 * Created by johnjoseph on 19/03/17.
 */

public class Util {

    public static boolean isHour(char c1, char c2) {
        return (((c1 == '0' || c1 == '1') && isNumber(c2)) || (c1 == '2' && (c2 == '0' || c2 == '1' || c2 == '2' || c2 == '3' || c2 == '4')));
    }

    public static boolean hasISDCodePrefix(String str, int i) {
        return i < str.length() - 1 && Constants.supportedISDCode.contains(str.substring(0, i)) || Constants.supportedISDCode.contains("+" + str.substring(0, i));
    }

    public static boolean possibleTimeAhead(String str, int i) {
        char c = str.charAt(i);
        return c == Constants.CH_SPACE && (meridienTimeAhead(str, i + 1) || hrsTimeAhead(str, i + 1));
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
        return c == Constants.CH_SPACE || c == Constants.CH_FSTP || c == Constants.CH_COMA || c == Constants.CH_RBKT || c == Constants.CH_NLINE;
    }

    public static boolean meridienTimeAhead(String str, int i) {
        boolean amOrPmStartAhead = i+1< str.length() && (str.charAt(i)=='a' || str.charAt(i)=='p') && str.charAt(i+1)=='m' ;
        if (amOrPmStartAhead ==false)
            return false;
        boolean isWordEndAtMeridien = ( i+2 >= str.length() );
        if (isWordEndAtMeridien)
            return true;
        char c = str.charAt(i+2);
        boolean checkIfJustWordStart = (c == Constants.CH_SPACE || c == Constants.CH_FSTP || c == Constants.CH_COMA || c == Constants.CH_RBKT ||  c == Constants.CH_HYPH || c == Constants.CH_NLINE) ;  //am or pm ahead but just a  word starting with am/pm like amp
        return checkIfJustWordStart;
    }

    public static boolean hrsTimeAhead(String str, int i) {
        boolean isHrsAhead = i + 1 < str.length() && str.charAt(i) == 'h' && str.charAt(i + 1) == 'r';
        if (!isHrsAhead)
            return false;
        boolean isWordEndAtHrs = (i + 2 >= str.length());
        if (isWordEndAtHrs)
            return true;
        char c = str.charAt(i + 2);
        if (c == 's') {
            isWordEndAtHrs = (i + 3 >= str.length());
            if (isWordEndAtHrs)
                return true;
            c = str.charAt(i + 3);
        }
        boolean isJustAWordAtStart = !(c == Constants.CH_SPACE || c == Constants.CH_FSTP || c == Constants.CH_COMA || c == Constants.CH_RBKT || c == Constants.CH_HYPH || c == Constants.CH_NLINE);  // hr or hrs ahead but just a word starting with hr(s) like hrithik or hrabble
        return !isJustAWordAtStart;
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
        return (isNumber(ch) || ch == Constants.CH_FSTP || ch == Constants.CH_SPACE || ch == Constants.CH_HYPH || ch == Constants.CH_COMA || ch == Constants.CH_SLSH || ch == Constants.CH_RBKT || ch == Constants.CH_EXCL || ch == Constants.CH_PLUS || ch == Constants.CH_STAR || ch == '\r' || ch == '\n' || ch =='\'');
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

    public static boolean isAlphaNumeric(String str) {
        if (str == null || str.length() == 0)
            return false;
        int numericCount=0;
        for (int i = 0; i < str.length(); i++) {
            boolean numeric = isNumber(str.charAt(i));
            boolean alpha = isAlpha(str.charAt(i));
            if(!(numeric || alpha))
                return false;
        }
        if(numericCount==0 || numericCount==str.length())
            return false;
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

    public static Trie loadTokens() {
        Trie tokenTrie = new Trie();
        tokenTrie.loadTrie();
        return tokenTrie;
    }

    public static Integer parseStrToInt(String text) {
        // length check in valChecks for INT overflow
        if(text==null || text.isEmpty() || text.length()>9)
            return null;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean checkForTimeRange(String val) {
        if(!isNumber(val) || val.length()<7)
            return false;
        Integer fromTimeHour = parseStrToInt(val.substring(0,2));
        Integer toTimeHour = parseStrToInt(val.substring(4,6));
        if(fromTimeHour==null || toTimeHour==null)
            return false;
        if(fromTimeHour<24 && toTimeHour <24)
            return true;
        return false;
    }

    public static boolean checkForNumRange(String val) {
        if(val == null || val.length()<3 || !val.contains("-") || val.startsWith("00"))
            return false;
        String[] parts = val.split("-");
        // 1800-20-545-5477
        if(parts.length !=2)
            return false;
        // -324
        if((parts[0].length()==0 || parts[0].length()>6) || (parts[1].length()==0 || parts[1].length()>6))
            return false;
        // 1800-20 or 91-9811
        boolean lengthRelatedChecks = (parts[1].length() >= parts[0].length()) && (parts[1].length()-parts[0].length() < 2);
        boolean valChecks = ( isNumber(parts[0]) && isNumber(parts[1]) ) && ( parseStrToInt(parts[1]) - parseStrToInt(parts[0]) ) > 0 ;
        if( lengthRelatedChecks && valChecks)
            return true;
        return false;
    }

    public static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public static String addDaysToDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return Constants.dateTimeFormatter().format(calendar.getTime());
    }

    public static Date getDateObject(String dateStr){
        try {
            Date dt = Constants.dateTimeFormatter().parse(dateStr);
            return dt;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String addTimeStampSuffix(String hour){
        return hour + ":00";
    }

    public static String getcallFrwrdCode(String str, int i) {
        String code = "";
        for(int k = i +1; k< i +5; k++){
            if(str.charAt(k) == Constants.CH_STAR){
                code = str.substring(i,k+1);
                break;
            }
        }
        return code;
    }

}
