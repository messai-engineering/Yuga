package com.twelfthmile.yuga.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by johnjoseph on 15/03/17.
 */

public class GenTrie implements Serializable {
    public boolean leaf = false;
    public boolean child = false;
    public final Map<Character, GenTrie> next=new HashMap<>();
    public String token;
}
