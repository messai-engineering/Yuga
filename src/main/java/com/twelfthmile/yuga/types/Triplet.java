package com.twelfthmile.yuga.types;

public class Triplet<A, B, C> {
    A a;
    B b;
    C c;

    public Triplet(A a_, B b_, C c_) {
        a = a_;
        b = b_;
        c = c_;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }
}
