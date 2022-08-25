package com.twelfthmile.yuga.classifierYuga;

import com.twelfthmile.yuga.Yuga;
import com.twelfthmile.yuga.types.*;
import com.twelfthmile.yuga.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import javax.naming.InsufficientResourcesException;
import java.util.*;

import static com.twelfthmile.yuga.utils.Util.isAlpha;
import static com.twelfthmile.yuga.utils.Util.isNumber;



public class ClassifierYuga {
    public static List<String> yugaList = new ArrayList<>();
    public static JSONObject getYugaTokensNew(String sentence, HashMap<String, String> configMap, IndexTrack indexTrack) throws JSONException {
        Trie prefixTrie = new Trie();
        prefixTrie.loadTrie();
        LinkedList<String> prevTokens = new LinkedList<>();
        Set<String> unmaskTokenSet = Constants.unmaskTokenSet;
        Map<String, Integer> tokenCount = new HashMap<>();
        int start = 0;
        for(String key:unmaskTokenSet){
            tokenCount.put(key,0);
        }
        StringBuilder sb = new StringBuilder("");
        Map<String,JSONObject> metaData = new HashMap();
        while(indexTrack.next < sentence.length()) {
            if (skipCharacter(sentence, indexTrack.next, sentence.charAt(indexTrack.next))) {
                indexTrack.next++;
                continue;
            }
            char ch = sentence.charAt(indexTrack.next);
            Pair<Integer, Pair> res = getTokenEndIndex(sentence, indexTrack.next, prefixTrie);
            int tokenEndIndex = res.getA();
            start = indexTrack.next;
            Pair<String, Integer> p = classifyTokens(sentence.substring(indexTrack.next), sentence.substring(indexTrack.next, tokenEndIndex).toLowerCase(), indexTrack, configMap, prevTokens, res.getB());
            if (p != null) {
                prevTokens.add(p.getA());
                sb.append(sentence.substring(start, p.getB()) + " ");

                sb.append(p.getA());
                if(!p.getA().equals("DATE") && tokenCount.containsKey(p.getA())) {
                    JSONObject metValForToken = new JSONObject();
                    metValForToken.put("INDEX", String.valueOf(p.getB()));
                    if (tokenCount.get(p.getA()) == 0)
                        metaData.put(p.getA(), metValForToken);
                    else
                        metaData.put(p.getA() + "_" + tokenCount.get(p.getA()), metValForToken);
                    tokenCount.put(p.getA(),tokenCount.get(p.getA())+1);
                }
                yugaList.add(p.getA());
                sb.append(" ");
            }
            else {
                if(!prevTokens.contains(sentence.substring(indexTrack.next, tokenEndIndex)))
                    prevTokens.add("NotAvail");
                sb.append(sentence.substring(indexTrack.next, tokenEndIndex));
                sb.append(" ");
                indexTrack.next = tokenEndIndex + 1;
            }
            if(Constants.possiblePrevTokens.containsKey(sentence.substring(start, tokenEndIndex).toLowerCase())) {
                prevTokens.add(Constants.possiblePrevTokens.get(sentence.substring(start, tokenEndIndex).toLowerCase()));
            }
            sb.append("");
        }
        return generateOutput(sb, metaData);
    }
    public static JSONObject generateOutput(StringBuilder sb, Map<String, JSONObject> metaData) throws JSONException {
        JSONObject jsonData = new JSONObject();
        jsonData.put("message", sb.toString().trim());
        jsonData.put("METADATA", new JSONObject(metaData));
        return jsonData;
    }

    public static List<String> getTokensFromYuga() {
        List<String> dummy = new ArrayList<>(yugaList);
        yugaList = new ArrayList<>();
        return dummy;
    }
    public static boolean skipCharacter(String sentence, int index, char ch) {
        return (goodEndings(sentence.charAt(index)) || ch == Constants.CH_PLUS) || ch == Constants.CH_BKSLSH;
    }
    private static Pair<Integer, Pair> getTokenEndIndex(String sentence, int index, Trie prefixTrie) {
        String subSentence = sentence.substring(index);
        Pair<Integer, Pair> nextSpaceIndex = nextDelimeterImmediate(subSentence, prefixTrie);
        int idx = nextSpaceIndex.getA() + index;
        nextSpaceIndex.setA(idx);
        return nextSpaceIndex;
    }
    private static Pair<Integer, Pair> nextDelimeterImmediate(String str, Trie prefixTrie) {
        //while traversing the string find any prefix if present
        int i;
        int len = 0;
        String label = null;
        TrieNode root = prefixTrie.root;
        String sentence = str.toLowerCase();
        for (i = 0; i < sentence.length(); i++) {
            char ch = sentence.charAt(i);
            if(root.hasNext(ch)) {
                root = root.get(ch);
                len += 1;
                if(root.isEnd()) {
                    label = root.getLabel();
                }
            }
            if (goodEndings(sentence.charAt(i))) {
                if(label != null) {
                    Pair<Integer, String> p = new Pair(len, label);
                    return new Pair<Integer, Pair>(i, p);
                }
                else
                    return new Pair<Integer, Pair>(i, null);
            }
        }
        return new Pair<Integer, Pair>(i, null);
    }
    static boolean goodEndings(char ch) {
        return ch == Constants.CH_SPACE || ch == Constants.CH_COMA || ch == Constants.CH_COLN || ch == Constants.CH_FSTP || ch == Constants.CH_RBKT || ch == Constants.CH_HYPH || ch == Constants.CH_LBKT || ch == Constants.CH_DQOT || ch == Constants.CH_EQLS || ch == Constants.CH_LSTN || ch == Constants.CH_GTTN || ch == '\r' || ch == '\n' || ch == Constants.CH_EXCL;
    }

    static Pair<String, Integer> classifyTokens(String sentence, String word, IndexTrack indexTrack, HashMap<String, String> configMap, LinkedList<String> prevTokens, Pair<Integer, String> prefix) {
        Triplet<Integer, String, String> t;
        if(prefix != null && prefix.getB().equals("CRNCY")) {
            Pair<Integer, Boolean> p = lookAheadIntegerForAmt(sentence.substring(prefix.getA()));
            if(p.getA() >= 0 && p.getB()) {
                int start = indexTrack.next;
                setNextindex(p.getA() + prefix.getA(), indexTrack);
                return new Pair<>("AMT", start);
            }
        }
        else if(Constants.tokens.containsKey(word) && Constants.tokens.get(word).equals("TRANSFER")) {
            String refId = null;
            String userId = null;
            HashMap<String, String> userMap = new HashMap<>();
            StateMachines.checkForUPI(word, refId, userId, userMap, 0);
            if(refId != null) {
                int start = indexTrack.next;
                switch(word) {
                    case "upi" :
                        return new Pair<>("UPI", start);
                    case "mmt" :
                        return new Pair<>("IMPS", start);
                    case "neft" :
                        return new Pair<>("NEFT", start);
                }
            }
        }
        else if(prefix != null && prefix.getB().equals("FLTID")) {
            int i = lookAheadInteger(sentence.substring(word.length()));
            boolean nextIsNumber = (i + 1 + word.length()) < sentence.length() && isNumber(sentence.charAt(i + 1 + word.length()));
            Boolean currencyPosible = false;
            if (i >= 0 && i <= 5 && nextIsNumber && (t = StateMachines.numberParse(sentence.substring(i + word.length()))) != null) {
                int start = indexTrack.next;
                setNextindex(word.length() + i + t.getA(), indexTrack);
                return new Pair<>("FLTIDVAL", start);
            }
        }
        else if (Constants.tokens.containsKey(word) && Constants.tokens.get(word).equals("HTTP")) {
            Pair<Integer, String> p = StateMachines.linkParse(sentence + " ", word.length());
            if(p != null) {
                int start = indexTrack.next;
                setNextindex(p.getA(), indexTrack);
                return new Pair<>("URL", start);
            }
        }
        else if(Constants.tokens.containsKey(word) && Constants.tokens.get(word).equals("SMS")) {
            int i = indexTrack.next + 1;
            StringBuilder smsCode = new StringBuilder("");
            Boolean legit = false;
            StateMachines.smsCode(word, indexTrack.next, i, smsCode, legit);
            if(legit) {
                int start = indexTrack.next;
                return new Pair<>("SMSCODE", start);
            }
        }
        else if(Constants.tokens.containsKey(word) && Constants.tokens.get(word).equals("WWW")) {
            Integer val = StateMachines.wwwParse(sentence + " ", word.length());
            if(val != null) {
                int start = indexTrack.next;
                setNextindex(val, indexTrack);
                return new Pair<>("URL", start);
            }
        }
        else if(Constants.tokens.containsKey(word) && Constants.tokens.get(word).equals("IDPRX")) {
            int i = ClassifierYuga.lookAheadNumberForIdPrx(sentence.substring(indexTrack.next));
            StringBuilder num = new StringBuilder();
            int idx = 0;
            boolean currencyPossible = false;
            if (i >= 0 && i <= 2) {
                t = StateMachines.numberParse(sentence.substring(i + indexTrack.next));
                if (t != null) {
                    int start = indexTrack.next;
                    return new Pair<>("NUM", start);
                }
            }
        }
        return classifyTokensUsingNext(sentence, word, indexTrack.next, configMap, indexTrack, prevTokens,prefix);
    }
    static Pair<String, Integer> classifyTokensUsingNext(String sentence, String word, int index, HashMap<String, String> configMap, IndexTrack indexTrack, LinkedList<String> prevTokens, Pair<Integer, String> prefix) {
        if(sentence.charAt(0) == Constants.CH_HASH) {
            int i = lookAheadHash(sentence);
            setNextindex(i, indexTrack);
            Response t = Yuga.parse(sentence.substring(i), configMap);
            if(t != null) {
                int start = indexTrack.next;
                setNextindex(t.getIndex(), indexTrack);
                return new Pair<>(t.getType(), start);
            }
        }
        return classifyInGeneral(sentence, configMap, indexTrack, prevTokens, prefix);
    }
    static Pair<String, Integer> classifyInGeneral(String sentence, HashMap<String, String> configMap, IndexTrack indexTrack, LinkedList<String> prevTokens, Pair<Integer, String> prefix) {
        //to check for non delimeter based instruments
        if(prefix != null && prefix.getB().equals("INSTR")) {
            Response t = Yuga.parse(sentence.substring(prefix.getA()), configMap);
            if(t != null) {
                int start = indexTrack.next;
                setNextindex(t.getIndex() + prefix.getA(), indexTrack);
                return new Pair<>("INSTRNO", start + prefix.getA());
            }
        }
        else {
            Response t = Yuga.parse(sentence, configMap);
            if (t != null) {
                if (t.getType().equals("NUM")) {
                    Integer idx = classifyUpiWrapper(sentence, t.getIndex());
                    if (idx != null) {
                        int start = indexTrack.next;
                        setNextindex(idx + 1, indexTrack);
                        return new Pair<>("UPI", start);
                    }
                }
                if (t.getType() != "STR") {
                    int start = indexTrack.next;
                    setNextindex(t.getIndex(), indexTrack);
                    return new Pair<>(t.getType(), start);
                }
            }
        }
        // to check for account numbers once again based on previous token
        if(prevTokens.size() > 0) {
            String prevTok = prevTokens.getLast();
            if(prevTok.equals("INS") || (prevTokens.size() >= 2 && prevTokens.get(prevTokens.size() - 2).equals("INS"))) {
                Pair<Integer, String> p = StateMachines.reCheckForAccount(sentence);
                if(p != null) {
                    int start = indexTrack.next;
                    setNextindex(p.getA(), indexTrack);
                    return new Pair<>("INSTRNO", start);
                }
            }
            else if(prefix != null && prefix.getB().equals("INSTR")) {
                Pair<Integer, String> p = StateMachines.reCheckForAccount(sentence.substring(prefix.getA()));
                if(p != null) {
                    int start = indexTrack.next;
                    setNextindex(p.getA(), indexTrack);
                    return new Pair<>("INSTRNO", start);
                }
            }
        }
        Pair<Integer, String> p;
        Integer i;
        p = StateMachines.checkForURL(sentence + " ");
        if(p != null) {
            int start = indexTrack.next;
            setNextindex(p.getA(), indexTrack);
            return new Pair<>("URL", start);
        }
        i = StateMachines.mailIdParse(sentence);
        if(i != null) {
            int start = indexTrack.next;
            setNextindex(i, indexTrack);
            return new Pair<>("EMAILADDRESS", start);
        }
        p = StateMachines.checkForId(sentence, 0);
        if(prevTokens.size() > 0) {
            String prevTok = prevTokens.getLast();
            if (p != null && (prevTok.equals("ID"))) {
                int start = indexTrack.next;
                char ch = sentence.charAt(p.getA());
                setNextindex(p.getA() + 1, indexTrack);
                return new Pair<>("IDVAL", start);
            }
            else if(p != null && prevTokens.size() >= 2 && prevTokens.get(prevTokens.size() - 2).equals("ID") && prevTok.equals("NotAvail")) {
                int start = indexTrack.next;
                setNextindex(p.getA() + 1, indexTrack);
                return new Pair<>("IDVAL", start);
            }
        }
        return null;
    }
    private static Integer classifyUpiWrapper(String sentence, int index) {
        if (index >= sentence.length())
            return null;
        if (sentence.charAt(index) == Constants.CH_LBKT)//skip ( in (yaser09mujtaba-1@okhdfcbank)
            index++;
        for (int i = index; i < sentence.length(); i++) {
            if (sentence.charAt(i) == '@' || sentence.charAt(i) == '¡') { // '¡' added for fixing IL-46
                index = i;
                break;
            } else if (isNumber(sentence.charAt(i)) || isAlpha(sentence.charAt(i)) || sentence.charAt(i) == '.' || sentence.charAt(i) == '-')
                continue;
            else
                return null;
        }
        int ind = classifyUpi(sentence.substring(index + 1));
        if(ind > 0)
            return index + ind + 1;
        return null;
    }
    private static int classifyUpi(String handle) {
        Trie upiTrie = new Trie();
        upiTrie.insertUpis();
        TrieNode t = upiTrie.root;
        char c;
        int x = -1;
        for (int i = 0; i < handle.length(); i++) {
            c = handle.charAt(i);
            if(t.hasNext(c)) {
                t = t.get(c);
                if(t.isEnd() && (i + 1) < handle.length() && (handle.charAt(i + 1) == '.' || handle.charAt(i + 1) == ' ' || handle.charAt(i + 1) == ')' || handle.charAt(i + 1) == '(' || (x = StateMachines.checkifsc(handle.substring(i + 1))) > 0)) {
                    if (x > 0)
                        return i + x;
                    return i;
                }
            }
            else {
                int d = lookAheadDotForUPI(handle.substring(i));
                if(d>0 && (i+d+10)<handle.length() && handle.substring(i+d+1, i+d + 10).equals("ifsc.npci")){
                    return i+d+10;
                } else
                    break;
            }
        }
        return -1;
    }
    private static int lookAheadDotForUPI(String sentence) {
        int index;
        char c;
        for (index = 0; index < sentence.length(); index++) {
            c = sentence.charAt(index);
            if (isAlpha(c)||isNumber(c))
                continue;
            else if (c == '.')
                return index;
            else
                return -1;
        }
        return -1;
    }
    private static boolean isNextId(int nextInd, String sentence) {
        StringBuilder sb = new StringBuilder("");
        int i = nextInd;
        char ch = sentence.charAt(i);
        while(!goodEndings(ch)) {
            sb.append(ch);
            i++;
            ch = sentence.charAt(i);
        }
        if(sb.toString().equals("id"))
            return true;
        return false;
    }
    private static int lookAheadInteger(String sentence) {
        int index;
        char c;
        for (index = 0; index < sentence.length(); index++) {
            c=sentence.charAt(index);
            if (c == ' ' || c == '.' || c == ':' || c == '-' || c == ',')
                continue;
            else if(isNumber(c))
                break;
            else
                return -1;
        }
        return index;
    }

    private static Pair<Integer, Boolean> lookAheadIntegerForAmt(String sentence) {
        int index;
        char c;
        Boolean flag = false;
        for (index = 0; index < sentence.length(); index++) {
            c = sentence.charAt(index);
            if (c == ' ' || c == ':' || c == '<' || c == '?' || c == '|')
                continue;
            else if(c == '.' && index == sentence.length() - 1)
                return new Pair<>(index, flag);
            else if (c == '.' || c == ',') {
                if(index>0 && sentence.charAt(index-1)==' ' && (index+1)<sentence.length() && isNumber(sentence.charAt(index+1)))
                    break;
                else
                    continue;
            } else if ((isNumber(c) && index + 1 < sentence.length()) || c == '-' || c == '*') {
                flag = true;
                continue;
            }
            else if((isNumber(c)) && index + 1 == sentence.length()) {
                flag = true;
                return new Pair<>(index + 1, flag);
            }
            else {
                return new Pair<>(index, flag);
            }
        }
        return new Pair<>(-1, false);
    }
    public static int lookAheadNumberForIdPrx(String sentence) {
        int i;
        char c;
        for (i = 0; i < sentence.length(); i++) {
            c = sentence.charAt(i);
            if (c == ' ' || c == '-')
                continue;
            else if (isNumber(c))
                break;
            else if (i > 3)
                break;
        }
        return i;
    }
    public static void setNextindex(int idx, IndexTrack indexTrack) {
        indexTrack.next += idx;
    }
    private static int lookAheadHash(String sentence) {
        int index;
        char c;
        for (index = 1; index < sentence.length(); index++) {
            c = sentence.charAt(index);
            if (c != ' ')
                break;
        }
        return index;
    }
}
