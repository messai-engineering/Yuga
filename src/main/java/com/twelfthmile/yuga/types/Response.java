package com.twelfthmile.yuga.types;

import com.twelfthmile.yuga.utils.Constants;

import java.util.*;

public class Response {
    private String type;
    private Map<String, String> valMap;
    private String str;
    private int index;
    private Date date;

    private Response() {
    }

    public Response(String type, Map<String, String> valMap, Object str, int index) {
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


    private String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    private int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private Map<String, String> getValMap() {
        return valMap;
    }

    public void setValMap(Map<String, String> valMap) {
        this.valMap = valMap;
    }

    private Date getDate() {
        return date;
    }

    @SuppressWarnings("unused")
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Response.class.getSimpleName() + "[", "]")
                .add("type='" + type + "'")
                .add("valMap=" + valMap)
                .add("str='" + str + "'")
                .add("index=" + index)
                .add("date=" + date)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response)) return false;
        Response response = (Response) o;
        return getIndex() == response.getIndex() &&
                getType().equals(response.getType()) &&
                getValMap().equals(response.getValMap()) &&
                getStr().equals(response.getStr());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getValMap(), getStr(), getIndex());
    }

    @Deprecated()
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
