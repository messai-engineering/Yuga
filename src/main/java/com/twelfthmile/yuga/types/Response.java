package com.twelfthmile.yuga.types;

import com.twelfthmile.yuga.utils.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private String type;
    private HashMap<String, String> valMap;
    private String str;
    private int index;
    private Date date;

    public Response(String type, HashMap<String, String> valMap, Object str, int index) {
        this.type = type;
        this.valMap = valMap;
        if (str instanceof String)
            this.str = (String) str;
        else {
            this.date = (Date) str;
            this.str = Constants.dateTimeFormatter().format(this.date);
        }
        this.index = index;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public HashMap<String, String> getValMap() {
        return valMap;
    }

    public void setValMap(HashMap<String, String> valMap) {
        this.valMap = valMap;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String print() {
        return "{\"type\":\"" + type + "\", \"str\":\"" + str + "\", \"index\":\"" + index + "\", \"valMap\":" + printValMap() + "}";
    }

    private String printValMap() {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, String> entry : valMap.entrySet()) {
            sb.append("\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",");
        }
        if (sb.length() > 1)
            sb.setLength(sb.length() - 1);
        return sb.append("}").toString();
    }
}
