package com.twelfthmile.yuga.types;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by johnjoseph on 15/03/17.
 */

public class GenTrie implements Serializable {
    public boolean leaf = false;
    public boolean child = false;
    public HashMap<Character, GenTrie> next;
    public String token;

    public GenTrie() {
        this.next = new HashMap<Character, GenTrie>();
    }
}
