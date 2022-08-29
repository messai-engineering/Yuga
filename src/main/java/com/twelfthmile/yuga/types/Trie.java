package com.twelfthmile.yuga.types;


import com.twelfthmile.yuga.utils.Constants;

public class Trie {
    public TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Inserts a word into the trie.
    public void insert(String word, String label) {
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            if (!node.hasNext(currentChar)) {
                node.put(currentChar, new TrieNode());
            }
            node = node.get(currentChar);
        }
        node.setEnd();
        node.setLabel(label);
    }
    public void insertUpis() {
        String label = "UPI";
        for(int i = 0; i < Constants.upi.length; i++) {
            insert(Constants.upi[i], label);
        }
    }
    private void insertCurr() {
        String label = "CRNCY";
        for(int i = 0; i < Constants.curr.length; i++) {
            insert(Constants.curr[i], label);
        }
    }
    private void insertInstr() {
        String label = "INSTR";
        for(int i = 0; i < Constants.instr.length; i++) {
            insert(Constants.instr[i], label);
        }
    }
    private void insertFltId() {
        String label = "FLTID";
        for(int i = 0; i < Constants.fltid.length; i++) {
            insert(Constants.fltid[i], label);
        }
    }
    public void loadTrie() {
        insertCurr();
        insertInstr();
        insertFltId();
    }
}
