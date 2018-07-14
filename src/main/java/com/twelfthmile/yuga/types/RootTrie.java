package com.twelfthmile.yuga.types;

import java.util.HashMap;

public class RootTrie {
    public HashMap<String, GenTrie> next;

    public RootTrie() {
        next = new HashMap<>();
    }
}
