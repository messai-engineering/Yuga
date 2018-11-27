package com.twelfthmile.yuga.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by johnjoseph on 15/03/17.
 */

public class FsaContextMap {

    //todo change to private
    private HashMap<String, String> map;
    private HashMap<String, String> valMap;
    private String prevKey;
    private ArrayList<String> keys;

    public FsaContextMap() {
        keys = new ArrayList<String>();
        map = new HashMap<String, String>();
        valMap = new HashMap<String, String>();
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public int size() {
        return map.keySet().size();
    }

    //normal put method
    public void put(String key, char value) {
        if (!keys.contains(key))
            keys.add(key);
        map.put(key, Character.toString(value));
        prevKey = key;
    }

    public void put(String key, int value) {
        if (!keys.contains(key))
            keys.add(key);
        map.put(key, String.valueOf(value));
        prevKey = key;
    }

    public void put(String key, String value) {
        if (!keys.contains(key))
            keys.add(key);
        map.put(key, value);
        prevKey = key;
    }

    public String getType() {
        return map.get(Constants.TY_TYP);
    }

    public void setType(String type) {
        map.put(Constants.TY_TYP, type);
    }

    public void setType(String type, String convertType) {
        map.put(Constants.TY_TYP, type);
        if (convertType != null)
            convert(convertType);
    }

    public String getVal(String name) {
        return valMap.get(name);
    }

    public void setVal(String name, String val) {
        valMap.put(name, val);
    }

    public HashMap<String, String> getValMap() {
        return valMap;
    }

    public int getIndex() {
        return Integer.parseInt(map.get(Constants.INDEX));
    }

    public void setIndex(int index) {
        map.put(Constants.INDEX, String.valueOf(index));
    }

    //appending to prev value
    public void append(char value) {
        String preVal = map.get(prevKey);
        put(prevKey, preVal + value);
    }

    public void append(String value) {
        String preVal = map.get(prevKey);
        put(prevKey, preVal + value);
    }

    //removing last appended value
    public void pop() {
        String preVal = map.get(prevKey);
        put(prevKey, preVal.substring(0, preVal.length() - 1));
    }

    public void convert(String kOld, String kNew) {
        if (map.containsKey(kOld)) {
            if (!map.containsKey(kNew))
                put(kNew, map.remove(kOld));
            else
                put(kNew, map.get(kNew) + map.remove(kOld));
            prevKey = kNew;
        }
    }

    private void convert(String k) {
        StringBuilder sb = new StringBuilder("");
        for (String key : keys) {
            sb.append(map.remove(key));
        }
        keys = new ArrayList<String>();
        put(k, sb.toString());
    }

    public void remove(String key) {
        map.remove(key);
    }

    //upgrade for eg from yy to yyy
    public void upgrade(char value) {
        if (prevKey.equals(Constants.DT_HH)) {
            put(Constants.DT_mm, value);
            prevKey = Constants.DT_mm;
        } else if (prevKey.equals(Constants.DT_mm)) {
            put(Constants.DT_ss, value);
            prevKey = Constants.DT_ss;
        } else if (prevKey.equals(Constants.DT_D)) {
            put(Constants.DT_MM, value);
            prevKey = Constants.DT_MM;
        } else if (prevKey.equals(Constants.DT_MM) || prevKey.equals(Constants.DT_MMM)) {
            put(Constants.DT_YY, value);
            prevKey = Constants.DT_YY;
        } else if (prevKey.equals(Constants.DT_YY)) {
            put(Constants.DT_YYYY, map.remove(Constants.DT_YY) + value);
            prevKey = Constants.DT_YYYY;
        }
    }

    public String get(String key) {
        return map.get(key);
    }

    public String print(String... str) {
        if (str != null && str.length > 0)
            return (str[0] + " " + map.toString());
        else
            return map.toString();
    }

    public void putAll(FsaContextMap fsaContextMap) {
        map.putAll(fsaContextMap.map);
    }

    public Date getDate(HashMap<String, String> config) {
        StringBuilder sbf = new StringBuilder("");
        StringBuilder sbs = new StringBuilder("");
        String key;
        boolean ifYear = false;
        boolean ifMonth = false;
        boolean ifDay = false;
        ArrayList<String> invalidDateContributors = new ArrayList<String>();
        //when year is not provided then we assume message year; we check for that when we have both day and month
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                key = entry.getKey();
                if (allow(key)) {
                    sbf.append(key).append(" ");
                    sbs.append(entry.getValue()).append(" ");
                    if (key.equals(Constants.DT_YY) || key.equals(Constants.DT_YYYY))
                        ifYear = true;
                    else if (key.equals(Constants.DT_D))
                        ifDay = true;
                    else if (key.equals(Constants.DT_MM) || key.equals(Constants.DT_MMM))
                        ifMonth = true;
                }
            }
            //date year defaulting
            if (!ifYear && config.containsKey(Constants.YUGA_CONF_DATE)) {
                sbf.append("yyyy ");
                sbs.append(config.get(Constants.YUGA_CONF_DATE).split("-")[0] + " ");//assuming yyyy-MM-dd HH:mm:ss format
            } else {
                int maxDate = Calendar.getInstance().get(Calendar.YEAR);
                if (map.containsKey(Constants.DT_YY)) {
                    int y = Integer.parseInt(map.get(Constants.DT_YY));
                    if (!(y > 0 && y < ((maxDate % 1000) + 3)))
                        invalidDateContributors.add(Constants.DT_YY);
                } else {
                    int y = Integer.parseInt(map.get(Constants.DT_YYYY));
                    if (!(y > 2000 && y < (maxDate + 3)))
                        invalidDateContributors.add(Constants.DT_YYYY);
                }

            }

            if (!ifMonth && config.containsKey(Constants.YUGA_CONF_DATE)) {
                sbf.append("MM ");
                sbs.append(config.get(Constants.YUGA_CONF_DATE).split("-")[1] + " ");//assuming yyyy-MM-dd HH:mm:ss format
            } else {
                if (map.containsKey(Constants.DT_MM)) {
                    int m = Integer.valueOf(map.get(Constants.DT_MM));
                    if (!(m >= 0 && m <= 12))
                        invalidDateContributors.add(Constants.DT_MM);
                }
            }

            if (!ifDay && config.containsKey(Constants.YUGA_CONF_DATE)) {
                sbf.append("dd ");
                sbs.append(config.get(Constants.YUGA_CONF_DATE).split("-")[2].split(" ")[0] + " ");//assuming yyyy-MM-dd HH:mm:ss format
            } else {
                if (map.containsKey(Constants.DT_D)) {
                    int d = Integer.valueOf(map.get(Constants.DT_D));
                    if (!(d >= 0 && d <= 31))
                        invalidDateContributors.add(Constants.DT_D);
                }
            }

            if (invalidDateContributors.size() > 0) {
                if (invalidDateContributors.size() == 1 && invalidDateContributors.get(0).equals(Constants.DT_MM) && ifDay && ifYear) {
                    DateFormat format = new SimpleDateFormat(Constants.DT_D + "/" + Constants.DT_MM + "/" + (map.containsKey(Constants.DT_YY) ? Constants.DT_YY : Constants.DT_YYYY));
                    return format.parse(map.get(Constants.DT_MM) + "/" + map.get(Constants.DT_D) + "/" + (map.containsKey(Constants.DT_YY) ? map.get(Constants.DT_YY) : map.get(Constants.DT_YYYY)));
                } else
                    return null;
            }
            DateFormat format = new SimpleDateFormat(sbf.toString(), Locale.ENGLISH);
            return format.parse(sbs.toString());
        } catch (Exception e) {
            //swallow
            return null;
        }
    }

    private boolean allow(String key) {
        return (key.equals(Constants.DT_D) || key.equals(Constants.DT_MM) || key.equals(Constants.DT_MMM) || key.equals(Constants.DT_YY) || key.equals(Constants.DT_YYYY) || key.equals(Constants.DT_HH) || key.equals(Constants.DT_mm) || key.equals(Constants.DT_ss));
    }

}
