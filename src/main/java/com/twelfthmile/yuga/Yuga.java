package com.twelfthmile.yuga;

import com.twelfthmile.yuga.types.*;
import com.twelfthmile.yuga.utils.FsaContextMap;
import com.twelfthmile.yuga.utils.L;
import com.twelfthmile.yuga.utils.Util;
import com.twelfthmile.yuga.utils.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by johnjoseph on 19/03/17.
 */

public class Yuga {


    private static final boolean D_DEBUG = false;

    private static class LazyHolder {
        static RootTrie root = createRoot();
    }

    public static void init() {
        RootTrie root = getRoot();
    }

    private static RootTrie getRoot() {
        return LazyHolder.root;
    }

    private static RootTrie createRoot() {
        RootTrie root = new RootTrie();
        root.next.put("FSA_MONTHS", new GenTrie());
        root.next.put("FSA_DAYS", new GenTrie());
        root.next.put("FSA_TIMEPRFX", new GenTrie());
        root.next.put("FSA_AMT", new GenTrie());
        root.next.put("FSA_TIMES", new GenTrie());
        root.next.put("FSA_TZ", new GenTrie());
        root.next.put("FSA_DAYSFFX", new GenTrie());
        root.next.put("FSA_UPI", new GenTrie());
        seeding(Constants.FSA_MONTHS, root.next.get("FSA_MONTHS"));
        seeding(Constants.FSA_DAYS, root.next.get("FSA_DAYS"));
        seeding(Constants.FSA_TIMEPRFX, root.next.get("FSA_TIMEPRFX"));
        seeding(Constants.FSA_AMT, root.next.get("FSA_AMT"));
        seeding(Constants.FSA_TIMES, root.next.get("FSA_TIMES"));
        seeding(Constants.FSA_TZ, root.next.get("FSA_TZ"));
        seeding(Constants.FSA_DAYSFFX, root.next.get("FSA_DAYSFFX"));
        seeding(Constants.FSA_UPI, root.next.get("FSA_UPI"));
        return root;
    }

    private static void seeding(String type, GenTrie root) {
        GenTrie t;
        int c = 0;
        for (String fsaCldr : type.split(",")) {
            c++;
            t = root;
            int len = fsaCldr.length();
            for (int i = 0; i < len; i++) {
                char ch = fsaCldr.charAt(i);
                t.child = true;
                if (!t.next.containsKey(ch))
                    t.next.put(ch, new GenTrie());
                t = t.next.get(ch);
                if (i == len - 1) {
                    t.leaf = true;
                    t.token = fsaCldr.replace(";", "");
                } else if (i < (len - 1) && fsaCldr.charAt(i + 1) == 59) { //semicolon
                    t.leaf = true;
                    t.token = fsaCldr.replace(";", "");
                    i++;//to skip semicolon
                }
            }
        }
    }

    /**
     * Returns Pair of index upto which date was read and the date object
     *
     * @param str date string
     * @return A last index for date string, b date object
     * returns null if string is not of valid date format
     */
    public static Pair<Integer, Date> parseDate(String str) {
        Map<String, String> configMap = generateDefaultConfig();
        return getIntegerDatePair(str, configMap);
    }

    private static Pair<Integer, Date> getIntegerDatePair(String str, Map<String, String> configMap) {
        Pair<Integer, FsaContextMap> p = parseInternal(str, configMap);
        if (p == null)
            return null;
        Date d = p.getB().getDate(configMap);
        if (d == null)
            return null;
        return new Pair<>(p.getA(), d);
    }

    /**
     * Returns Pair of index upto which date was read and the date object
     *
     * @param str    date string
     * @param config pass the message date string for defaulting
     * @return A last index for date string, b date object
     * returns null if string is not of valid date format
     */

    public static Pair<Integer, Date> parseDate(String str, Map<String, String> config) {
        return getIntegerDatePair(str, config);
    }

    /**
     * Returns Response containing data-type, captured string and index upto which data was read
     *
     * @param str    string to be parsed
     * @param config config for parsing (Eg: date-defaulting)
     * @return Yuga Response type
     */

    public static Response parse(String str, Map<String, String> config) {
        return getResponse(str, config);
    }

    private static Response getResponse(String str, Map<String, String> config) {
        Pair<Integer, FsaContextMap> p = parseInternal(str, config);
         if (p == null)
            return null;
        Pair<String, Object> pr = prepareResult(str, p, config);
        return new Response(pr.getA(), p.getB().getValMap(), pr.getB(), p.getA());
    }

    /**
     * Returns Response containing data-type, captured string and index upto which data was read
     *
     * @param str string to be parsed
     * @return Yuga Response type
     */

    public static Response parse(String str) {
        Map<String, String> configMap = generateDefaultConfig();
        return getResponse(str, configMap);
    }

    // Pair <Type,String>
    private static Pair<String, Object> prepareResult(String str, Pair<Integer, FsaContextMap> p, Map<String, String> config) {
        int index = p.getA();
        FsaContextMap map = p.getB();
        if (map.getType().equals(Constants.TY_DTE)) {
            if (map.contains(Constants.DT_MMM) && map.size() < 3)//may fix
                return new Pair<>(Constants.TY_STR, str.substring(0, index));
            if (map.contains(Constants.DT_HH) && map.contains(Constants.DT_mm) && !map.contains(Constants.DT_D) && !map.contains(Constants.DT_DD) && !map.contains(Constants.DT_MM) && !map.contains(Constants.DT_MMM) && !map.contains(Constants.DT_YY) && !map.contains(Constants.DT_YYYY)) {
                map.setType(Constants.TY_TME, null);
                map.setVal("time", map.get(Constants.DT_HH) + ":" + map.get(Constants.DT_mm));
                return new Pair<>(Constants.TY_TME, str.substring(0, index));
            }
            Date d = map.getDate(config);
            if (d != null)
                return new Pair<>(p.getB().getType(), d);
            else
                return new Pair<>(Constants.TY_STR, str.substring(0, index));
        } else {
            if (map.get(map.getType()) != null) {
                if (map.getType().equals(Constants.TY_ACC) && config.containsKey(Constants.YUGA_SOURCE_CONTEXT) && config.get(Constants.YUGA_SOURCE_CONTEXT).equals(Constants.YUGA_SC_CURR))
                    return new Pair<>(Constants.TY_AMT, map.get(map.getType()).replaceAll("X", ""));
                return new Pair<>(p.getB().getType(), map.get(map.getType()));
            } else
                return new Pair<>(p.getB().getType(), str.substring(0, index));

        }
    }

    private static Map<String, String> generateDefaultConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(Constants.YUGA_CONF_DATE, Constants.dateTimeFormatter().format(new Date()));
        return config;
    }

    public static Pair<Integer, String> checkTypes(String type, String word) {
        return Util.checkTypes(getRoot(), type, word);
    }


    private static Pair<Integer, FsaContextMap> parseInternal(String str, Map<String, String> config) {
        int state = 1, i = 0;
        Pair<Integer, String> p;
        char c;
        FsaContextMap map = new FsaContextMap();
        DelimiterStack delimiterStack = new DelimiterStack();
        str = str.toLowerCase();
        int counter = 0, insi;
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if (Util.isNumber(c)) {
                        map.setType(Constants.TY_NUM, null);
                        map.put(Constants.TY_NUM, c);
                        state = 2;
                    } else if ((p = Util.checkTypes(getRoot(), "FSA_MONTHS", str.substring(i))) != null) {
                        map.setType(Constants.TY_DTE, null);
                        map.put(Constants.DT_MMM, p.getB());
                        i += p.getA();
                        state = 33;
                    } else if ((p = Util.checkTypes(getRoot(), "FSA_DAYS", str.substring(i))) != null) {
                        map.setType(Constants.TY_DTE, null);
                        map.put(Constants.DT_DD, p.getB());
                        i += p.getA();
                        state = 30;
                    } else if (c == Constants.CH_HYPH) {//it could be a negative number
                        state = 37;
                    } else if (c == Constants.CH_LSBT) {//it could be an OTP
                        state = 1;
                    } else {
                        state = accAmtNumPct(str, i, map, config);
                        if (map.getType() == null)
                            return null;
                        if (state == -1 && !map.getType().equals(Constants.TY_PCT))
                            i = i - 1;
                    }
                    break;
                case 2:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        state = 3;
                    } else if (Util.isTimeOperator(c)) {
                        delimiterStack.push(c);
                        map.setType(Constants.TY_DTE, Constants.DT_HH);
                        state = 4;
                    } else if (Util.isDateOperator(c) || c == Constants.CH_COMA) {
                        delimiterStack.push(c);
                        map.setType(Constants.TY_DTE, Constants.DT_D);
                        state = 16;
                    } else if ((p = Util.checkTypes(getRoot(), "FSA_MONTHS", str.substring(i))) != null) {
                        map.setType(Constants.TY_DTE, Constants.DT_D);
                        map.put(Constants.DT_MMM, p.getB());
                        i += p.getA();
                        state = 24;
                    } else {
                        state = accAmtNumPct(str, i, map, config);
                        if (state == -1 && !map.getType().equals(Constants.TY_PCT))
                            i = i - 1;
                    }
                    break;
                case 3:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        state = 8;
                    } else if (Util.isTimeOperator(c)) {
                        delimiterStack.push(c);
                        map.setType(Constants.TY_DTE, Constants.DT_HH);
                        state = 4;
                    } else if (Util.isDateOperator(c) || c == Constants.CH_COMA) {
                        delimiterStack.push(c);
                        map.setType(Constants.TY_DTE, Constants.DT_D);
                        state = 16;
                    } else if ((p = Util.checkTypes(getRoot(), "FSA_MONTHS", str.substring(i))) != null) {
                        map.setType(Constants.TY_DTE, Constants.DT_D);
                        map.put(Constants.DT_MMM, p.getB());
                        i += p.getA();
                        state = 24;
                    } else if ((p = Util.checkTypes(getRoot(), "FSA_DAYSFFX", str.substring(i))) != null) {
                        map.setType(Constants.TY_DTE, Constants.DT_D);
                        i += p.getA();
                        state = 32;
                    } else {
                        state = accAmtNumPct(str, i, map, config);
                        if (state == -1 && !map.getType().equals(Constants.TY_PCT))
                            i = i - 1;
                    }
                    break;
                case 4: //hours to mins
                    if (Util.isNumber(c)) {
                        map.upgrade(c);//hh to mm
                        state = 5;
                    } else { //saw a colon randomly, switch back to num from hours
                        if (!map.contains(Constants.DT_MMM))
                            map.setType(Constants.TY_NUM, Constants.TY_NUM);
                        i = i - 2; //move back so that colon is omitted
                        state = -1;
                    }
                    break;
                case 5:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        state = 5;
                    } else if (c == Constants.CH_COLN)
                        state = 6;
                    else if (c == 'a' && (i + 1) < str.length() && str.charAt(i + 1) == 'm') {
                        i = i + 1;
                        state = -1;
                    } else if (c == 'p' && (i + 1) < str.length() && str.charAt(i + 1) == 'm') {
                        map.put(Constants.DT_HH, String.valueOf(Integer.parseInt(map.get(Constants.DT_HH)) + 12));
                        i = i + 1;
                        state = -1;
                    } else if ((p = Util.checkTypes(getRoot(), "FSA_TIMES", str.substring(i))) != null) {
                        i += p.getA();
                        state = -1;
                    } else
                        state = 7;
                    break;
                case 6: //for seconds
                    if (Util.isNumber(c)) {
                        map.upgrade(c);
                        if ((i + 1) < str.length() && Util.isNumber(str.charAt(i + 1)))
                            map.append(str.charAt(i + 1));
                        i = i + 1;
                        state = -1;
                    } else
                        state = -1;
                    break;
                case 7:
                    if (c == 'a' && (i + 1) < str.length() && str.charAt(i + 1) == 'm') {
                        i = i + 1;
                        int hh = Integer.parseInt(map.get(Constants.DT_HH));
                        if (hh == 12)
                            map.put(Constants.DT_HH, String.valueOf(0));
                    } else if (c == 'p' && (i + 1) < str.length() && str.charAt(i + 1) == 'm') {
                        int hh = Integer.parseInt(map.get(Constants.DT_HH));
                        if (hh != 12)
                            map.put(Constants.DT_HH, String.valueOf(hh + 12));
                        i = i + 1;
                    } else if ((p = Util.checkTypes(getRoot(), "FSA_TIMES", str.substring(i))) != null) {
                        i += p.getA();
                    } else
                        i = i - 2;
                    state = -1;
                    break;
                case 8:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        state = 9;
                    } else {
                        state = accAmtNumPct(str, i, map, config);
                        if (c == Constants.CH_SPACE && state == -1 && (i + 1) < str.length() && Util.isNumber(str.charAt(i + 1)))
                            state = 12;
                        else if (c == Constants.CH_HYPH && state == -1 && (i + 1) < str.length() && Util.isNumber(str.charAt(i + 1)))
                            state = 45;
                        else if (state == -1 && !map.getType().equals(Constants.TY_PCT))
                            i = i - 1;
                    }
                    break;
                case 9:
                    if (Util.isDateOperator(c)) {
                        delimiterStack.push(c);
                        state = 25;
                    }
                    //handle for num case
                    else if (Util.isNumber(c)) {
                        map.append(c);
                        counter = 5;
                        state = 15;
                    } else {
                        state = accAmtNumPct(str, i, map, config);
                        if (state == -1 && !map.getType().equals(Constants.TY_PCT)) {//NUM
                            i = i - 1;
                        }
                    }
                    break;
                case 10:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        map.setType(Constants.TY_AMT, Constants.TY_AMT);
                        state = 14;
                    } else { //saw a fullstop randomly
                        map.pop();//remove the dot which was appended
                        i = i - 2;
                        state = -1;
                    }
                    break;
                case 11:
                    if (c == 42 || c == 88 || c == 120)//*Xx
                        map.append('X');
                    else if (c == Constants.CH_HYPH)
                        state = 11;
                    else if (Util.isNumber(c)) {
                        map.append(c);
                        state = 13;
                    } else if (c == ' ' && ((i + 1) < str.length() && (str.charAt(i + 1) == 42 || str.charAt(i + 1) == 88 || str.charAt(i + 1) == 120 || Util.isNumber(str.charAt(i + 1)))))
                        state = 11;
                    else if (c == Constants.CH_FSTP && (insi = lookAheadForInstr(str, i)) > 0) {
                        int x;
                        for(x = insi-i;x>0;x--){
                            map.append('X');
                        }
                        i = insi;
                    } else {
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 12:
                    if (Util.isNumber(c)) {
                        map.setType(Constants.TY_AMT, Constants.TY_AMT);
                        map.append(c);
                    } else if (c == Constants.CH_COMA) //comma
                        state = 12;
                    else if (c == Constants.CH_FSTP) { //dot
                        map.append(c);
                        state = 10;
                    } else if (c == Constants.CH_HYPH && (i + 1) < str.length() && Util.isNumber(str.charAt(i + 1))) {
                        state = 39;
                    } else {
                        if (i - 1 > 0 && str.charAt(i - 1) == Constants.CH_COMA)
                            i = i - 2;
                        else
                            i = i - 1;
                        state = -1;
                    }
                    break;
                case 13:
                    if (Util.isNumber(c))
                        map.append(c);
                    else if (c == 42 || c == 88 || c == 120)//*Xx
                        map.append('X');
                    else if (c == Constants.CH_FSTP && config.containsKey(Constants.YUGA_SOURCE_CONTEXT) && config.get(Constants.YUGA_SOURCE_CONTEXT).equals(Constants.YUGA_SC_CURR)) { //LIC **150.00 fix
                        map.setType(Constants.TY_AMT, Constants.TY_AMT);
                        map.put(Constants.TY_AMT, map.get(Constants.TY_AMT).replaceAll("X", ""));
                        map.append(c);
                        state = 10;
                    } else if (c == Constants.CH_FSTP && (insi = lookAheadForInstr(str, i)) > 0) {
                        int x;
                        for(x = insi-i;x>0;x--){
                            map.append('X');
                        }
                        i = insi;
                    } else {
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 14:
                    if (Util.isNumber(c)) {
                        map.append(c);
                    } else if (c == Constants.CH_PCT) {
                        map.setType(Constants.TY_PCT, Constants.TY_PCT);
                        state = -1;
                    } else if ((c == 'k' || c == 'c') && (i + 1) < str.length() && str.charAt(i + 1) == 'm') {
                        map.setType(Constants.TY_DST, Constants.TY_DST);
                        i += 1;
                        state = -1;
                    } else if ((c == 'k' || c == 'm') && (i + 1) < str.length() && str.charAt(i + 1) == 'g') {
                        map.setType(Constants.TY_WGT, Constants.TY_WGT);
                        i += 1;
                        state = -1;
                    } else {
                        if (c == Constants.CH_FSTP && ((i + 1) < str.length() && Util.isNumber(str.charAt(i + 1)))) {
                            String samt = map.get(map.getType());
                            if (samt.contains(".")) {
                                String[] samtarr = samt.split("\\.");
                                if (samtarr.length == 2) {
                                    map.setType(Constants.TY_DTE);
                                    map.put(Constants.DT_D, samtarr[0]);
                                    map.put(Constants.DT_MM, samtarr[1]);
                                    state = 19;
                                    break;
                                }
                            }
                        }
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 15:
                    if (Util.isNumber(c)) {
                        counter++;
                        map.append(c);
                    } else if (c == Constants.CH_COMA) //comma
                        state = 12;
                    else if (c == Constants.CH_FSTP) { //dot
                        map.append(c);
                        state = 10;
                    } else if ((c == 42 || c == 88 || c == 120) && (i + 1) < str.length() && ((Util.isNumber(str.charAt(i + 1)) || str.charAt(i + 1) == Constants.CH_HYPH) || str.charAt(i + 1) == 42 || str.charAt(i + 1) == 88 || str.charAt(i + 1) == 120)) {//*Xx
                        map.setType(Constants.TY_ACC, Constants.TY_ACC);
                        map.append('X');
                        state = 11;
                    } else if (c == Constants.CH_SPACE && (i + 2) < str.length() && Util.isNumber(str.charAt(i + 1)) && Util.isNumber(str.charAt(i + 2))) {
                        state = 41;
                    }
//                    else if (c == Constants.CH_ATRT) {
//                        delimiterStack.push(c);
//                        state = 43;
//                    }
                    else {
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 16:
                    if (Util.isNumber(c)) {
                        map.upgrade(c);
                        state = 17;
                    } else if (c == Constants.CH_SPACE || c == Constants.CH_COMA)
                        state = 16;
                    else if ((p = Util.checkTypes(getRoot(), "FSA_MONTHS", str.substring(i))) != null) {
                        map.put(Constants.DT_MMM, p.getB());
                        i += p.getA();
                        state = 24;
                    }
                    //we should handle amt case, where comma led to 16 as opposed to 12
                    else if (c == Constants.CH_FSTP) { //dot
                        map.setType(Constants.TY_NUM, Constants.TY_NUM);
                        map.append(c);
                        state = 10;
                    } else if (i > 0 && (p = Util.checkTypes(getRoot(), "FSA_TIMES", str.substring(i))) != null) {
                        map.setType(Constants.TY_TME, null);
                        String s = str.substring(0, i);
                        if (p.getB().equals("mins") || p.getB().equals("minutes"))
                            s = "00" + s;
                        extractTime(s, map.getValMap());
                        i = i + p.getA();
                        state = -1;
                    } else {//this is just a number, not a date
                        //to cater to 16 -Nov -17
                        if (delimiterStack.pop() == Constants.CH_SPACE && c == Constants.CH_HYPH && i + 1 < str.length() && (Util.isNumber(str.charAt(i + 1)) || (p = Util.checkTypes(getRoot(), "FSA_MONTHS", str.substring(i + 1))) != null)) {
                            state = 16;
                        } else {
                            map.setType(Constants.TY_NUM, Constants.TY_NUM);
                            int j = i;
                            while (!Util.isNumber(str.charAt(j)))
                                j--;
                            i = j;
                            state = -1;
                        }
                    }
                    break;
                case 17:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        state = 18;
                    } else if (Util.isDateOperator(c)) {
                        delimiterStack.push(c);
                        state = 19;
                    }
                    //we should handle amt case, where comma led to 16,17 as opposed to 12
                    else if (c == Constants.CH_COMA && delimiterStack.pop() == Constants.CH_COMA) { //comma
                        map.setType(Constants.TY_NUM, Constants.TY_NUM);
                        state = 12;
                    } else if (c == Constants.CH_FSTP && delimiterStack.pop() == Constants.CH_COMA) { //dot
                        map.setType(Constants.TY_NUM, Constants.TY_NUM);
                        map.append(c);
                        state = 10;
                    } else {
                        map.setType(Constants.TY_STR, Constants.TY_STR);
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 18:
                    if (Util.isDateOperator(c)) {
                        delimiterStack.push(c);
                        state = 19;
                    }
                    //we should handle amt case, where comma led to 16,17 as opposed to 12
                    else if (Util.isNumber(c) && delimiterStack.pop() == Constants.CH_COMA) {
                        map.setType(Constants.TY_NUM, Constants.TY_NUM);
                        state = 12;
                        map.append(c);
                    } else if (Util.isNumber(c) && delimiterStack.pop() == Constants.CH_HYPH) {
                        map.setType(Constants.TY_NUM, Constants.TY_NUM);
                        state = 42;
                        map.append(c);
                    } else if (c == Constants.CH_COMA && delimiterStack.pop() == Constants.CH_COMA) { //comma
                        map.setType(Constants.TY_NUM, Constants.TY_NUM);
                        state = 12;
                    } else if (c == Constants.CH_FSTP && delimiterStack.pop() == Constants.CH_COMA) { //dot
                        map.setType(Constants.TY_NUM, Constants.TY_NUM);
                        map.append(c);
                        state = 10;
                    } else if (c == Constants.CH_FSTP && map.contains(Constants.DT_D) && map.contains(Constants.DT_MM)) { //dot
                        state = -1;
                    }else {
                        map.setType(Constants.TY_STR, Constants.TY_STR);
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 19: //year
                    if (Util.isNumber(c)) {
                        map.upgrade(c);
                        state = 20;
                    } else {
                        i = i - 2;
                        state = -1;
                    }
                    break;
                case 20: //year++
                    if (Util.isNumber(c)) {
                        map.append(c);
                        state = 21;
                    } else if (c == ':') {
                        if(map.contains(Constants.DT_YY))
                            map.convert(Constants.DT_YY, Constants.DT_HH);
                        else if(map.contains(Constants.DT_YYYY))
                            map.convert(Constants.DT_YYYY, Constants.DT_HH);
                        state = 4;
                    } else {
                        map.remove(Constants.DT_YY);//since there is no one number year
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 21:
                    if (Util.isNumber(c)) {
                        map.upgrade(c);
                        state = 22;
                    } else if (c == ':') {
                        if(map.contains(Constants.DT_YY))
                            map.convert(Constants.DT_YY, Constants.DT_HH);
                        else if(map.contains(Constants.DT_YYYY))
                            map.convert(Constants.DT_YYYY, Constants.DT_HH);
                        state = 4;
                    } else {
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 22:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        state = -1;
                    } else {
                        map.remove(Constants.DT_YYYY);//since there is no three number year
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 24:
                    if (Util.isDateOperator(c) || c == Constants.CH_COMA) {
                        delimiterStack.push(c);
                        state = 24;
                    } else if (Util.isNumber(c)) {
                        map.upgrade(c);
                        state = 20;
                    } else if (c == Constants.CH_SQOT && (i + 1) < str.length() && Util.isNumber(str.charAt(i + 1))) {
                        state = 24;
                    } else if (c == '|') {
                        state = 24;
                    } else {
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 25://potential year start comes here
                    if (Util.isNumber(c)) {
                        map.setType(Constants.TY_DTE, Constants.DT_YYYY);
                        map.put(Constants.DT_MM, c);
                        state = 26;
                    } else if (i > 0 && (p = Util.checkTypes(getRoot(), "FSA_TIMES", str.substring(i))) != null) {
                        map.setType(Constants.TY_TME, null);
                        String s = str.substring(0, i);
                        if (p.getB().equals("mins"))
                            s = "00" + s;
                        extractTime(s, map.getValMap());
                        i = i + p.getA();
                        state = -1;
                    } else {
                        //it wasn't year, it was just a number
                        i = i - 2;
                        state = -1;
                    }
                    break;
                case 26:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        state = 27;
                    } else {
                        map.setType(Constants.TY_STR, Constants.TY_STR);
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 27:
                    if (Util.isDateOperator(c)) {
                        delimiterStack.push(c);
                        state = 28;
                    } else if (Util.isNumber(c)) {//it was a number, most probably telephone number
                        if (map.getType().equals(Constants.TY_DTE)) {
                            map.setType(Constants.TY_NUM, Constants.TY_NUM);
                        }
                        map.append(c);
                        if ((delimiterStack.pop() == Constants.CH_SLSH || delimiterStack.pop() == Constants.CH_HYPH) && i + 1 < str.length() && Util.isNumber(str.charAt(i + 1)) && (i + 2 == str.length() || Util.isDelimiter(str.charAt(i + 2)))) {//flight time 0820/0950
                            map.setType(Constants.TY_TMS, Constants.TY_TMS);
                            map.append(str.charAt(i + 1));
                            i = i + 1;
                            state = -1;
                        } else if (delimiterStack.pop() == Constants.CH_SPACE) {
                            state = 41;
                        } else
                            state = 12;
                    } else if (c == 42 || c == 88 || c == 120) {//*Xx
                        map.setType(Constants.TY_ACC, Constants.TY_ACC);
                        map.append('X');
                        state = 11;
                    } else {
                        map.setType(Constants.TY_STR, Constants.TY_STR);
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 28:
                    if (Util.isNumber(c)) {
                        map.put(Constants.DT_D, c);
                        state = 29;
                    } else {
                        map.setType(Constants.TY_STR, Constants.TY_STR);
                        i = i - 2;
                        state = -1;
                    }
                    break;
                case 29:
                    if (Util.isNumber(c)) {
                        map.append(c);
                    } else
                        i = i - 1;
                    state = -1;
                    break;
                case 30:
                    if (c == Constants.CH_COMA || c == Constants.CH_SPACE)
                        state = 30;
                    else if (Util.isNumber(c)) {
                        map.put(Constants.DT_D, c);
                        state = 31;
                    } else {
                        map.setType(Constants.TY_DTE);
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 31:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        state = 32;
                    } else if ((p = Util.checkTypes(getRoot(), "FSA_MONTHS", str.substring(i))) != null) {
                        map.put(Constants.DT_MMM, p.getB());
                        i += p.getA();
                        state = 24;
                    } else if (c == Constants.CH_COMA || c == Constants.CH_SPACE)
                        state = 32;
                    else {
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 32:
                    if ((p = Util.checkTypes(getRoot(), "FSA_MONTHS", str.substring(i))) != null) {
                        map.put(Constants.DT_MMM, p.getB());
                        i += p.getA();
                        state = 24;
                    } else if (c == Constants.CH_COMA || c == Constants.CH_SPACE)
                        state = 32;
                    else if ((p = Util.checkTypes(getRoot(), "FSA_DAYSFFX", str.substring(i))) != null) {
                        i += p.getA();
                        state = 32;
                    } else {
                        int j = i;
                        while (!Util.isNumber(str.charAt(j)))
                            j--;
                        i = j;
                        state = -1;
                    }
                    break;
                case 33:
                    if (Util.isNumber(c)) {
                        map.put(Constants.DT_D, c);
                        state = 34;
                    } else if (c == Constants.CH_SPACE || c == Constants.CH_COMA || c == Constants.CH_HYPH)
                        state = 33;
                    else {
                        map.setType(Constants.TY_DTE);
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 34:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        state = 35;
                    } else if (c == Constants.CH_SPACE || c == Constants.CH_COMA)
                        state = 35;
                    else {
                        map.setType(Constants.TY_DTE);
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 35:
                    if (Util.isNumber(c)) {
                        if(i>1 && Util.isNumber(str.charAt(i-1))) {
                            map.convert(Constants.DT_D, Constants.DT_YYYY);
                            map.append(c);
                        }
                        else
                            map.put(Constants.DT_YY, c);
                        state = 20;
                    } else if (c == Constants.CH_SPACE || c == Constants.CH_COMA)
                        state = 40;
                    else {
                        map.setType(Constants.TY_DTE);
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 36:
                    if (Util.isNumber(c)) {
                        map.append(c);
                        counter++;
                    } else if (c == Constants.CH_FSTP && (i + 1) < str.length() && Util.isNumber(str.charAt(i + 1))) {
                        map.append(c);
                        state = 10;
                    } else if (c == Constants.CH_HYPH && (i + 1) < str.length() && Util.isNumber(str.charAt(i + 1))) {
                        delimiterStack.push(c);
                        map.append(c);
                        state = 16;
                    } else {
                        if (counter == 12 || Util.isNumber(str.substring(1, i)))
                            map.setType(Constants.TY_NUM, Constants.TY_NUM);
                        else
                            return null;
                        state = -1;
                    }
                    break;
                case 37:
                    if (Util.isNumber(c)) {
                        map.setType(Constants.TY_AMT, Constants.TY_AMT);
                        map.put(Constants.TY_AMT, '-');
                        map.append(c);
                        state = 12;
                    } else if (c == Constants.CH_FSTP) {
                        map.put(Constants.TY_AMT, '-');
                        map.append(c);
                        state = 10;
                    } else
                        state = -1;
                    break;
                case 38:
                    i = map.getIndex();
                    state = -1;
                    break;
                case 39://instrno
                    if (Util.isNumber(c))
                        map.append(c);
                    else {
                        map.setType(Constants.TY_ACC, Constants.TY_ACC);
                        state = -1;
                    }
                    break;
                case 40:
                    if (Util.isNumber(c)) {
                        map.put(Constants.DT_YY, c);
                        state = 20;
                    } else if (c == Constants.CH_SPACE || c == Constants.CH_COMA)
                        state = 40;
                    else {
                        map.setType(Constants.TY_DTE);
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 41://for phone numbers; same as 12 + space; coming from 27
                    if (Util.isNumber(c)) {
                        map.append(c);
                    } else if (c == Constants.CH_SPACE)
                        state = 41;
                    else {
                        if ((i - 1) > 0 && str.charAt(i - 1) == Constants.CH_SPACE)
                            i = i - 2;
                        else
                            i = i - 1;
                        state = -1;
                    }
                    break;
                case 42: //18=12 case, where 7-2209 was becoming amt as part of phn support
                    if (Util.isNumber(c)) {
                        map.append(c);
                    } else if (c == Constants.CH_HYPH && (i + 1) < str.length() && Util.isNumber(str.charAt(i + 1))) {
                        state = 39;
                    } else {
                        i = i - 1;
                        state = -1;
                    }
                    break;
                case 43: //1234567890@ybl
                    if (Util.isLowerAlpha(c) || Util.isNumber(c)) {
                        map.setType(Constants.TY_VPD, Constants.TY_VPD);
                        map.append(delimiterStack.pop());
                        map.append(c);
                        state = 44;
                    } else {
                        state = -1;
                    }
                    break;
                case 44:
                    if (Util.isLowerAlpha(c) || Util.isNumber(c) || c == Constants.CH_FSTP) {
                        map.append(c);
                        state = 44;
                    } else
                        state = -1;
                    break;
                case 45:
                    if (Util.isNumber(c)) {
                        map.append(c);
                    } else if (c == Constants.CH_HYPH && (i + 1) < str.length() && Util.isNumber(str.charAt(i + 1))) {
                        state = 39;
                    } else {
                        if (i - 1 > 0 && str.charAt(i - 1) == Constants.CH_COMA)
                            i = i - 2;
                        else
                            i = i - 1;
                        state = -1;
                    }
                    break;
            }
            i++;
            if (D_DEBUG) {
                L.msg("ch:" + c + " state:" + state + " map:" + map.print());
            }
        }
        if (map.getType() == null)
            return null;
        //sentence end cases
        if (state == 10) {
            map.pop();
            i = i - 1;
        } else if (state == 36) {
            if ((counter == 12 || Util.isNumber(str.substring(1, i))))
                map.setType(Constants.TY_NUM, Constants.TY_NUM);
            else
                return null;
        }

        if (map.getType().equals(Constants.TY_AMT)) {
            if (!map.contains(map.getType()) || ((map.get(map.getType()).contains(".") && map.get(map.getType()).split("\\.")[0].length() > 8) || (!map.get(map.getType()).contains(".") && map.get(map.getType()).length() > 8)))
                map.setType(Constants.TY_NUM, Constants.TY_NUM);
        }

        if (map.getType().equals(Constants.TY_NUM)) {
            if (i < str.length() && Character.isAlphabetic(str.charAt(i)) && !config.containsKey(Constants.YUGA_SOURCE_CONTEXT)) {
                int j = i;
                while (j < str.length() && str.charAt(j) != ' ')
                    j++;
                map.setType(Constants.TY_STR, Constants.TY_STR);
                i = j;
            } else if (map.get(Constants.TY_NUM) != null) {
                if (map.get(Constants.TY_NUM).length() == 10 && (map.get(Constants.TY_NUM).charAt(0) == '9' || map.get(Constants.TY_NUM).charAt(0) == '8' || map.get(Constants.TY_NUM).charAt(0) == '7'))
                    map.setVal("num_class", Constants.TY_PHN);
                else if (map.get(Constants.TY_NUM).length() == 12 && map.get(Constants.TY_NUM).startsWith("91"))
                    map.setVal("num_class", Constants.TY_PHN);
                else if (map.get(Constants.TY_NUM).length() == 11 && map.get(Constants.TY_NUM).startsWith("18"))
                    map.setVal("num_class", Constants.TY_PHN);
                else if (map.get(Constants.TY_NUM).length() == 11 && map.get(Constants.TY_NUM).charAt(0) == '0')
                    map.setVal("num_class", Constants.TY_PHN);
                else
                    map.setVal("num_class", Constants.TY_NUM);
            }
        } else if (map.getType().equals(Constants.TY_DTE) && (i + 1) < str.length()) {
            Pair<Integer, String> pTime;
            int in = i + skip(str.substring(i));
            String sub = str.substring(in);
            if (in < str.length()) {
                if (Util.isNumber(str.charAt(in)) || Util.checkTypes(getRoot(), "FSA_MONTHS", sub) != null || Util.checkTypes(getRoot(), "FSA_DAYS", sub) != null) {
                    Pair<Integer, FsaContextMap> p_ = parseInternal(sub, config);
                    if (p_ != null && p_.getB().getType().equals(Constants.TY_DTE)) {
                        map.putAll(p_.getB());
                        i = in + p_.getA();
                    }
                } else if ((pTime = Util.checkTypes(getRoot(), "FSA_TIMEPRFX", sub)) != null) {
                    int iTime = in + pTime.getA() + 1 + skip(str.substring(in + pTime.getA() + 1));
                    if (iTime < str.length() && (Util.isNumber(str.charAt(iTime)) || Util.checkTypes(getRoot(), "FSA_DAYS", str.substring(iTime)) != null)) {
                        Pair<Integer, FsaContextMap> p_ = parseInternal(str.substring(iTime), config);
                        if (p_ != null && p_.getB().getType().equals(Constants.TY_DTE)) {
                            map.putAll(p_.getB());
                            i = iTime + p_.getA();
                        }
                    }
                } else if ((pTime = Util.checkTypes(getRoot(), "FSA_TZ", sub)) != null) {
                    int j = skipForTZ(str.substring(in + pTime.getA() + 1), map);
                    i = in + pTime.getA() + 1 + j;
                } else if (sub.toLowerCase().startsWith("pm") || sub.toLowerCase().startsWith("am")) {
                    //todo handle appropriately for pm
                    i = in + 2;
                }
            }
        } else if (map.getType().equals(Constants.TY_TMS)) {
            String v = map.get(map.getType());
            if (v != null && v.length() == 8 && Util.isHour(v.charAt(0), v.charAt(1)) && Util.isHour(v.charAt(4), v.charAt(5))) {
                extractTime(v.substring(0, 4), map.getValMap(), "dept");
                extractTime(v.substring(4, 8), map.getValMap(), "arrv");
            }
        }
        return new Pair<Integer, FsaContextMap>(i, map);
    }

    private static int skipForTZ(String str, FsaContextMap map) {
        int state = 1, i = 0;
        char c;
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if (c == Constants.CH_SPACE || c == Constants.CH_PLUS || Util.isNumber(c))
                        state = 1;
                    else if (c == Constants.CH_COLN)
                        state = 2;
                    else {
                        String s_ = str.substring(0, i).trim();
                        if (s_.length() == 4 && Util.isNumber(s_)) {//we captured a year after IST Mon Sep 04 13:47:13 IST 2017
                            map.put(Constants.DT_YYYY, s_);
                            state = -2;
                        } else
                            state = -1;
                    }
                    break;
                case 2:
                    //todo re-adjust GMT time, current default +5:30 for IST
                    if (Util.isNumber(c))
                        state = 3;
                    else
                        state = -1;
                    break;
                case 3:
                    if (Util.isNumber(c))
                        state = 4;
                    else
                        state = -1;
                    break;
                case 4:
                    if (c == Constants.CH_SPACE)
                        state = 5;
                    else
                        state = -2;
                    break;
                case 5:
                    String sy = str.substring(i, i + 4);
                    if ((i + 3) < str.length() && Util.isNumber(sy)) {
                        map.put(Constants.DT_YYYY, sy);
                        i = i + 3;
                    }
                    state = -2;
                    break;
            }
            i++;
        }
        String s_ = str.substring(0, i).trim();
        if (state == 1 && s_.length() == 4 && Util.isNumber(s_))//we captured a year after IST Mon Sep 04 13:47:13 IST 2017
            map.put(Constants.DT_YYYY, s_);
        return (state == -1) ? 0 : i;
    }

    private static int skip(String str) {
        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == ' ' || str.charAt(i) == ',' || str.charAt(i) == '(' || str.charAt(i) == ':')
                i++;
            else
                break;
        }
        return i;
    }

    private static int nextSpace(String str) {
        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == ' ')
                return i;
            else
                i++;
        }
        return i;
    }

    private static int accAmtNumPct(String str, int i, FsaContextMap map, Map<String, String> config) {
        //acc num amt pct
        Pair<Integer, String> p;
        char c = str.charAt(i);
        String subStr = str.substring(i);
        if (c == Constants.CH_FSTP) { //dot
            if (i == 0 && config.containsKey(Constants.YUGA_SOURCE_CONTEXT) && config.get(Constants.YUGA_SOURCE_CONTEXT).equals(Constants.YUGA_SC_CURR))
                map.setType(Constants.TY_AMT, Constants.TY_AMT);
            map.append(c);
            return 10;
        } else if (c == 42 || c == 88 || c == 120) {//*Xx
            map.setType(Constants.TY_ACC, Constants.TY_ACC);
            map.append('X');
            return 11;
        } else if (c == Constants.CH_COMA) { //comma
            return 12;
        } else if (c == Constants.CH_PCT || (c == Constants.CH_SPACE && (i + 1) < str.length() && str.charAt(i + 1) == Constants.CH_PCT)) { //pct
            map.setType(Constants.TY_PCT, Constants.TY_PCT);
            return -1;
        } else if (c == Constants.CH_PLUS) {
            if (config.containsKey(Constants.YUGA_SOURCE_CONTEXT) && config.get(Constants.YUGA_SOURCE_CONTEXT).equals(Constants.YUGA_SC_CURR)) {
                return -1;
            }
            map.setType(Constants.TY_STR, Constants.TY_STR);
            return 36;
        } else if (i > 0 && (p = Util.checkTypes(getRoot(), "FSA_AMT", subStr)) != null) {
            map.setIndex(p.getA());
            map.setType(Constants.TY_AMT, Constants.TY_AMT);
            map.append(getAmt(p.getB()));
            return 38;
        } else if (i > 0 && (p = Util.checkTypes(getRoot(), "FSA_TIMES", subStr)) != null) {
            int ind = i + p.getA();
            map.setIndex(ind);
            map.setType(Constants.TY_TME, null);
            String s = str.substring(0, i);
            if (p.getB().equals("mins"))
                s = "00" + s;
            extractTime(s, map.getValMap());
            return 38;
        } else
            return -1;
    }

    private static String getAmt(String type) {
        switch (type) {
            case "lakh":
            case "lac":
                return "00000";
            case "k":
                return "000";
            default:
                return "";
        }
    }

    private static void extractTime(String str, Map<String, String> valMap, String... prefix) {
        String pre = "";
        if (prefix != null && prefix.length > 0)
            pre = prefix[0] + "_";
        Pattern pattern = Pattern.compile("([0-9]{2})([0-9]{2})?([0-9]{2})?");
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            valMap.put(pre + "time", m.group(1) + ((m.groupCount() > 1 && m.group(2) != null) ? ":" + m.group(2) : ":00"));
        }
    }

    private static int lookAheadForInstr(String str, int index) {
        char c;
        for (int i = index; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == Constants.CH_FSTP) {
            }
            else if (c == 42 || c == 88 || c == 120 || Util.isNumber(c))
                return i;
            else
                return -1;
        }
        return -1;
    }

    static class DelimiterStack {
        final ArrayList<Character> stack;

        DelimiterStack() {
            stack = new ArrayList<Character>();
        }

        void push(char ch) {
            stack.add(ch);
        }

        char pop() {
            if (stack.size() > 0)
                return stack.get(stack.size() - 1);
            return '~';
        }
    }

}
