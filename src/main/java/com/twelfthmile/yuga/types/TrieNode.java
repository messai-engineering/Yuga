package com.twelfthmile.yuga.types;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    private Map<Character, TrieNode> children;
    private boolean leaf;

    private String label;

    public TrieNode() {
        children = new HashMap<>();
    }

    public boolean hasNext(char ch) {
        return children.containsKey(ch);
    }
    public TrieNode get(char ch) {
        return children.get(ch);
    }
    public void put(char ch, TrieNode node) {
        children.put(ch, new TrieNode());
    }
    public void setEnd() {
        leaf = true;
    }
    public boolean isEnd() {
        return leaf;
    }
    public void setLabel(String str) {
        this.label = str;
    }
    public String getLabel() {
        return label;
    }
}
