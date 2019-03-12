package com.twelfthmile.yuga.utils;

public class L {

    private L() {

    }

    public static void msg(Object str) {
        System.out.println(str);
    }

    @SuppressWarnings("unused")
    public static void error(Exception e) {
        e.printStackTrace();
    }
}