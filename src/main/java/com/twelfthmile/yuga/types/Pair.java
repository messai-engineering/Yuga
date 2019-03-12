package com.twelfthmile.yuga.types;

import java.util.Objects;

/**
 * Created by johnjoseph on 19/03/17.
 */

public class Pair<A, B> {

    private final A a;
    private final B b;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return getA().equals(pair.getA()) &&
                getB().equals(pair.getB());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getA(), getB());
    }
}
