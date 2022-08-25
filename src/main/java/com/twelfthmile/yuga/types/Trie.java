package com.twelfthmile.yuga.types;


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
        String[] arr = {"airtel", "airtelpaymentsbank", "albk", "allahabadbank", "allbank", "andb", "apb", "apl", "axis", "axisb", "axisbank",
                "axisgo", "bandhan", "barodampay", "barodapay", "birla", "boi", "cbin", "cboi", "centralbank", "cmsidfc", "cnrb", "csbcash", "csbpay",
                "cub", "dbs", "dcb", "dcbbank", "denabank", "dlb", "eazypay", "equitas", "ezeepay", "fbl", "federal", "finobank",
                "hdfcbank", "hdfcbankjd", "hsbc", "icici", "icicibank", "idbi", "idbibank", "idfc", "idfcbank", "idfcnetc", "ikwik", "imobile",
                "indbank", "indianbank", "indianbk", "icicipay", "indus", "iob", "jkb", "jsbp", "karb", "karurvysyabank", "kaypay", "kbl",
                "kbl052", "kmb", "kmbl", "kotak", "kvb", "kvbank", "lime", "lvb", "lvbank", "mahb", "myicici", "obc", "okbizaxis", "okaxis",
                "okhdfcbank", "okicici", "oksbi", "paytm", "payzapp", "pingpay", "pockets", "pnb", "psb", "purz", "rajgovhdfcbank", "sbi",
                "sc", "scb", "scbl", "scmobile", "sib", "srcb", "synd", "syndbank", "syndicate", "tjsb", "ubi", "uboi",
                "uco", "unionbank", "unionbankofindia", "united", "utbi", "vijayabank", "vijb", "vjb", "ybl", "yesbank", "yesbankltd", "upi", "ibl"};
        String label = "UPI";
        for(int i = 0; i < arr.length; i++) {
            insert(arr[i], label);
        }
    }
    private void insertCurr() {
        String[] arr = {"rs", "inr", "cny", "amt", "amount", "ngn", "usd", "cad", "eur", "gbp", "aed", "jpy", "aud", "s$", "lkr", "ksh", "egp"};
        String label = "CRNCY";
        for(int i = 0; i < arr.length; i++) {
            insert(arr[i], label);
        }
    }
    private void insertInstr() {
        String[] arr = {"card", "no", "a/c"};
        String label = "INSTR";
        for(int i = 0; i < arr.length; i++) {
            insert(arr[i], label);
        }
    }
    private void insertFltId() {
        String[] arr = {"6e", "indigo", "ai", "airindia", "sg", "spicejet", "g8", "goair", "uk", "vistara", "ix",
                          "airindiaexpress", "2t", "trujet", "9w", "jetairways"};
        String label = "FLTID";
        for(int i = 0; i < arr.length; i++) {
            insert(arr[i], label);
        }
    }
    public void loadTrie() {
        insertCurr();
        insertInstr();
        insertFltId();
    }
}
