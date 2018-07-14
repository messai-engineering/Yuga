package com.twelfthmile.yuga.types;

/**
 * Created by johnjoseph on 19/03/17.
 */

public class Pair<A, B> {

    A a;
    B b;

    public Pair(A a_, B b_) {
        a = a_;
        b = b_;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }
}
