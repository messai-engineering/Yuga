package com.twelfthmile.yuga.types;

import java.util.HashMap;
import java.util.Map;

public class RootTrie {
    public final Map<String, GenTrie> next = new HashMap<>();
}
