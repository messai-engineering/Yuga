package com.twelfthmile.yuga.classifierYuga;

import com.twelfthmile.yuga.Yuga;
import com.twelfthmile.yuga.types.Pair;
import com.twelfthmile.yuga.types.Triplet;
import com.twelfthmile.yuga.utils.Constants;
import com.twelfthmile.yuga.utils.Util;

import java.util.HashMap;

import static com.twelfthmile.yuga.utils.Constants.CH_SPACE;
import static com.twelfthmile.yuga.utils.Util.*;
import static com.twelfthmile.yuga.utils.Util.isLowerAlpha;

public class StateMachines {
    public static int checkifsc(String str) {
        char ch;
        int state = 1, i = 0;
        while (state > 0 && i < str.length()) {
            ch = str.charAt(i);
            switch (state) {
                case 1:
                    if (isNumber(ch))
                        state = 1;
                    else if (ch == '.')
                        state = 2;
                    else
                        state = -1;
                    break;
                case 2:
                    if ((i + 9) < str.length() && str.substring(i, i + 9).equals("ifsc.npci"))
                        return i + 9;
                    else
                        state = -1;
                    break;
            }
            i++;
        }
        return -1;
    }
    public static int cdrParse(String str) {
        int state = 1, i = 0;
        char c;
        int ret = -1;//0-> dr 1->cr
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if (c == 'C') {
                        ret = 1;
                        state = 2;
                    } else if (c == 'D') {
                        ret = 0;
                        state = 2;
                    } else
                        state = -1;
                    break;
                case 2:
                    if (c == 'R' || c == 'r')
                        state = 3;
                    else
                        state = -1;
                    break;
                case 3:
                    if (c == Constants.CH_SPACE || c == Constants.CH_FSTP || c == Constants.CH_COMA || c == Constants.CH_SCLN)
                        return ret;
                    else
                        return -1;
            }
            i++;
        }
        return -1;
    }
    public static Integer wwwParse(String str, int indRead) {
        int state = 1, i = indRead - 1;
        char c;
        int dotcounter = 0;
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if (c == Constants.CH_SPACE ) {
                        int x = i;
                        if (i > 0 && (str.charAt(i - 1) == Constants.CH_FSTP || str.charAt(i - 1) == Constants.CH_RBKT)) {
                            if (i > 1 && str.charAt(i - 2) == Constants.CH_RBKT)
                                x = i - 2;
                            else
                                x = i - 1;
                        }
                        if (i != indRead && dotcounter > 1){ // to ensure its not like "WWW Olacab"
                            return x;
                        }else {
                            return null;
                        }
                    } else if (c == Constants.CH_FSTP) {
                        dotcounter++;
                        if (i>3 && (str.substring(i-3,i).equals("com") || str.substring(i-2,i).equals("in"))) {
                            return i;
                        }
                    } else if(c==Constants.CH_SLSH || c==Constants.CH_EQLS || c==Constants.CH_QUEST || c==Constants.CH_LSTN|| c==Constants.CH_GTTN){
                        ;
                    }else if (!isAlpha(c) && !isNumber(c) && c!='-')
                        state = -1;
                    break;
            }
            i++;
        }
        return null;
    }
    public static Pair<Integer, String> linkParse(String str, int indRead) {
        int state = 1, i = indRead;
        char c;
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if (c == Constants.CH_COLN && (i + 1) < str.length() && str.charAt(i + 1) == Constants.CH_SLSH)
                        state = 2;
                    else
                        state = -1;
                    break;
                case 2:
                    // Added newline '\n' lookahead check for IL-145
                    if (c == Constants.CH_SPACE || c== Constants.CH_NLINE || (c ==Constants.CH_BKSLSH && (i + 1) < str.length() && str.charAt(i + 1) == 110)) {
                        int x = i;
                        if (i>3 && i<str.length() && (str.substring(i-3,i).equals("www") || str.substring(i-3,i).equals("ww.")) ){ // ex: 'https:/www mathem.se/u/XOr2T'
                            Pair<Integer, String> p  = checkForURL(str.substring(i+1));
                            if (p != null){
                                p.setA( i+1+ p.getA());
                                //p.getB().str= "https:/www." + p.getB().str;
                                return p;
                            } else{
                                return null;
                            }
                        }// Click http://skraft in/K39gFxw
                        else if(c == Constants.CH_SPACE && (i + 5) < str.length() && (str.substring(i+1,i+4).equals("in/") || str.substring(i+1,i+5).equals("com/"))){
                            i++;
                            continue;
                        }
                        else if (i>0 && (str.charAt(i-1) == Constants.CH_FSTP || str.charAt(i-1) == Constants.CH_RBKT)) {
                            if(i>1 && str.charAt(i-2) == Constants.CH_RBKT)
                                x = i-2;
                            else
                                x = i-1;
                        }
                        return new Pair<>(x, str.substring(0, x));
                    }
                    break;
            }
            i++;
        }
        return null;
    }

    public static Pair<Integer, String> checkForURL(String str) {
        int state = 1, i = 0;
        char c;
        int dotCounter = 0,slashCounter=0;
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if (c == Constants.CH_FSTP && (i + 1) < str.length() && !Util.isDelimiter(str.charAt(i + 1) )){
                        dotCounter++;
                        if(dotCounter>=2 && i>5) {
                            // To prevent cases like B.R. Ambedkar, S.R.S Travels
                            // But need to capture u.airtel.in/24dswf
                            state = 2;
                        }
                    }
                    else if(c==Constants.CH_SLSH && i>=5 && dotCounter>0) {
                        slashCounter++;
                        state = 2;
                    }
                    else if(!Util.isAlpha(c) && !Util.isNumber(c)) {
                        state = -1;
                    }
                    else {
                        ;
                    }
                    break;
                case 2:
                    if(c==Constants.CH_SLSH ) {
                        slashCounter++;
                    } else  if(c==Constants.CH_FSTP ) {
                        dotCounter++;
                    } else if(c==Constants.CH_COMA ){
                        return null;
                    } else if (c == Constants.CH_SPACE) {
                        if(dotCounter>=2 && slashCounter<1) {
                            return null;
                        }else {
                            return new Pair<>(i, str.substring(0, i));
                        }
                    }
                    break;
            }
            i++;
        }
        return null;
    }
    public static void checkForUPI(String str, String refId, String userId, HashMap<String, String> userMap, int i) {
        int state = 1, j;
        Pair<Integer, String> p;
        char c;
        StringBuilder sb = new StringBuilder("");
        int wordCount = -1;
        Yuga.init();
        String prefix = "";
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if ((p = Yuga.checkTypes("FSA_UPI", str.substring(i))) != null) {
                        i += p.getA();
                        state = 2;
                    } else
                        state = -1;
                    break;
                case 2:
                    if ((j = lookAheadIntegerForUPI(str.substring(i))) != -1 && j < 6) {
                        i += j;
                        sb.append(str.charAt(i));
                        state = 3;
                    } else
                        state = -1;
                    break;
                case 3:
                    if (isNumber(c)) {
                        sb.append(c);
                        state = 3;
                    } else if (isUPIDelimeter(c)) {
                        refId = sb.toString();
                        sb = new StringBuilder("");
                        state = 4;
                    } else
                        state = -1;
                    break;
                case 4:
                    if (isNumber(c) || isUpperAlpha(c) || isLowerAlpha(c)) {
                        if (wordCount == -1)
                            wordCount = 0;
                        sb.append(c);
                        state = 4;
                        if (i + 1 == str.length()) {
                            userId = sb.toString();
                            sb = new StringBuilder("");
                        }
                    } else if (wordCount > -1 && wordCount < 2 && c == Constants.CH_SPACE) {
                        wordCount++;
                        sb.append(c);
                        state = 4;
                        if (i + 1 == str.length()) {
                            userId = sb.toString();
                            sb = new StringBuilder("");
                        }
                    } else if (c == Constants.CH_ATRT) {
                        if (!prefix.equals(""))
                            userMap.put(prefix, sb.toString());
                        userId = sb.toString();
                        sb = new StringBuilder("");
                        state = 5;
                    } else {
                        String u = sb.toString();
                        if ("from".equalsIgnoreCase(u)) {
                            prefix = "from";
                            sb = new StringBuilder("");
                            state = 4;
                        } else {
                            userId = u;
                            sb = new StringBuilder("");
                            i -= 1;
                            state = -1;
                        }
                    }
                    break;
                case 5:
                    if (isUpperAlpha(c) || isLowerAlpha(c) || c == Constants.CH_FSTP || isUPIDelimeter(c))
                        state = 5;
                    else if (isNumber(c)) {
                        if (isUPIDelimeter(str.charAt(i - 1))) {
                            sb.append(c);
                            state = 6;
                        } else
                            state = 5;
                    } else {
                        if (i >= 2 && "to".equalsIgnoreCase(str.substring(i - 2, i))) {
                            prefix = "to";
                            sb = new StringBuilder("");
                            state = 4;
                        } else {
                            i -= 1;
                            state = -1;
                        }
                    }
                    break;
                case 6:
                    if (isNumber(c)) {
                        sb.append(c);
                        state = 6;
                    } else {
                        if (sb.toString().length() > 6)
                            refId = sb.toString();
                        sb = new StringBuilder("");
                        i -= 1;
                        state = -1;
                    }
                    break;
            }
            i++;
        }
    }
    public static Triplet<Integer, String, String> numberParse(String str) {
        int state = 1, i = 0;
        boolean currencyPossible = false;
        StringBuilder num = new StringBuilder();
        char c;
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if (isNumber(c) || c == '-') {
                        state = 2;
                        num.append(c);
                    } else
                        state = -1;
                    break;
                case 2:
                    if (isNumber(c)) //state is still the same
                        num.append(c);
                    else if (c == 44) //comma
                        break;
                    else if (c == 46) { //dot
                        num.append(c);
                        state = 3;
                    }else if(c == '\n')
                        break;
                    else
                        state = -1;
                    break;
                case 3:
                    if (isNumber(c)) { //state is still the same
                        currencyPossible = true;
                        num.append(c);
                    } else {
                        if (!currencyPossible && num.length() > 0) //remove last dot
                            num.deleteCharAt(num.length() - 1);
                        state = -1;
                    }
                    break;
            }
            i++;
        }
        if (i == str.length())
            i++;
        if (state == -1 && num.length() == 0)
            return null;
        else if (currencyPossible)
            return new Triplet<Integer, String, String>(i - 1, num.toString(), Constants.AMT);
        else {
            return new Triplet<Integer, String, String>(i - 1, num.toString(), Constants.NUM);
        }
    }
    public static void smsCode(String str, int indRead, int i, StringBuilder smsCode, boolean legit) {
        int state = 1;
        char c;
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if ((c == ':' || c == '-') && i<indRead+3)
                        state = 1;
                    else if (isUpperAlpha(c) || c == CH_SPACE) {
                        smsCode.append(c);
                        state = 1;
                    }
                    else if (c=='<' || c== '(') {
                        smsCode.append(c);
                        state = 2;
                    } else {
                        if(i>indRead+4)
                            legit  = true;
                        state = -1;
                    }
                    break;
                case 2:
                    if (isAlpha(c) || c == CH_SPACE || isNumber(c)) {
                        smsCode.append(c);
                        state = 2;
                    } else if (c=='>' || c == ')') {
                        smsCode.append(c);
                        state = 1;
                    } else
                        state = -1;
                    break;
            }
            i++;
        }
    }
    public static Integer mailIdParse(String str) {
        int state = 1, i = 0;
        char c;
        int countBefAtrt = 0,labelCounter=0,atrtIdx=0,dotCountAfterAtrt=0;
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if (c == Constants.CH_ATRT && (i-countBefAtrt > 1)  ) {
                        state = 2;
                        atrtIdx=i;
                    } else if (c== Constants.CH_FSTP){
                        if((i-1>0 && str.charAt(i-1)==Constants.CH_FSTP) || (i+1<str.length() && str.charAt(i+1)==Constants.CH_ATRT))
                            state=-1;
                    } else if (c == Constants.CH_SPACE){
                        state =-1;
                    }
                    break;
                case 2:
                    if (c == Constants.CH_SPACE || i==str.length()-1 || c == Constants.CH_LSBTE || c == Constants.CH_GTTN) {
                        if((str.charAt(i-1)==Constants.CH_HYPH || dotCountAfterAtrt==0) || labelCounter<=2) {
                            state = -1;
                            break;
                        }
                        return i;
                    }
                    else if(c==Constants.CH_SLSH || c==Constants.CH_EQLS || c==Constants.CH_QUEST || c==Constants.CH_LSTN|| c==Constants.CH_GTTN || c == Constants.CH_ATRT){
                        state =-1;
                        break;
                    }
                    else if (c== Constants.CH_HYPH){
                        if(i-1==atrtIdx)
                            state=-1;
                    }else if (c== Constants.CH_FSTP){
                        labelCounter=1;
                        if(i+1<str.length() && str.charAt(i+1)!=Constants.CH_SPACE)
                            dotCountAfterAtrt++;
                        if(i+1<str.length() && str.charAt(i+1)==Constants.CH_SPACE && dotCountAfterAtrt>0)
                            labelCounter=3; // fullstop at end of email. Make it pass
                    }
                    else if (!isAlpha(c) && !isNumber(c) && c!=Constants.CH_FSTP) {
                        state = -1;
                    }
                    else if (labelCounter>63) {
                        state = -1;
                    }
                    labelCounter++;
                    break;
            }
            i++;
        }
        return null;
    }
    public static Pair<Integer, String> reCheckForAccount(String str) {
        int state = 1, i = 0;
        int num = 0;
        char c;
        boolean account = false;
        StringBuilder sb = new StringBuilder("");
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if (isAlpha(c)) {
                        state = 2;
                    } else if (isNumber(c)) {
                        sb.append(c);
                        num++;
                        state = 2;
                    } else
                        state = -1;
                    break;
                case 2:
                    if (isAlpha(c) || c == Constants.CH_SLSH || c == Constants.CH_HYPH) {
                        num = 0;
                        state = 2;
                    } else if (isNumber(c)) {
                        sb.append(c);
                        num++;
                        state = 2;
                    } else {
                        num = 0;
                        state = -1;
                    }
                    break;
            }
            if (num >= 4)
                account = true;
            i++;
        }
        if (account)
            return new Pair<>(i - 1, instrnoValidate(sb.toString()));
        return null;
    }
    private static String instrnoValidate(String numString) {
        if (numString.length() < 4)
            return null;
        else
            numString = numString.substring(numString.length() - 4);
        return numString;
    }
    public static Pair<Integer, String> checkForId(String str, int startIndex) {
        //(c == ' ' || c == '.' || c == ',' || c == ')' || c == '-')
        int state = 1, i = 0;
        char c;
        boolean haveSeenUpper = false;
        boolean haveSeenNumber = false;
        boolean haveSeenLower = false;
        boolean validID = false;
        StringBuilder sb = new StringBuilder("");
        if (i < str.length() && str.charAt(i) == Constants.CH_SQOT)
            i++;
        while (state > 0 && i < str.length()) {
            c = str.charAt(i);
            switch (state) {
                case 1:
                    if (isNumber(c) || isAlpha(c)) {
                        if(isUpperAlpha(c))
                            haveSeenUpper=true;
                        else if(isLowerAlpha(c))
                            haveSeenLower = true;
                        else
                            haveSeenNumber = true;
                        sb.append(c);
                        state = 2;
                    } else
                        state = -1;
                    break;
                case 2:
                    if (isNumber(c) || isAlpha(c) || c == Constants.CH_UNSC) {
                        if(isUpperAlpha(c))
                            haveSeenUpper=true;
                        else if(isLowerAlpha(c))
                            haveSeenLower = true;
                        else if(isNumber(c))
                            haveSeenNumber = true;
                        sb.append(c);
                        state = 2;
                    }
                    // PNR is MHYIGH- 6E 334
                    else if (c == Constants.CH_SQOT || (c == Constants.CH_HYPH && !(i+1<str.length() && str.charAt(i+1)==Constants.CH_SPACE) )) { // RYG-8FFH3
                        state = 2;
                    }
                    // IL-248    "RYG - 8FFH3"
                    else if( i+3<str.length() && str.substring(i,i+3).equalsIgnoreCase(" - ")  &&  Character.isLetterOrDigit((str.charAt(i + 3)))){
                        Pair<Integer, String> id = null;
                        if( (id = checkForId(str.substring(i+3),startIndex+i))!=null){
                            sb.append(id.getB());
                            state = -1;
                        }
                        else
                            state = -1;
                    }
                    else{
                        state = -1;
                    }
                    break;
            }
            i++;
        }

        if(haveSeenUpper){
            if(!haveSeenNumber){
                validID = !haveSeenLower;
            }else
                validID = true;
        }else {
            if (!haveSeenNumber)
                validID = false;
            else {
                validID = haveSeenLower;
            }
        }

        if (sb.length() > 0 && validID)
            return new Pair<>(i - 1, sb.toString());
        return null;
    }
    private static int lookAheadIntegerForUPI(String sentence) {
        int index;
        char c;
        for (index = 0; index < sentence.length(); index++) {
            c = sentence.charAt(index);
            if (isUPIDelimeter(c) || isLowerAlpha(c) || isUpperAlpha(c))
                continue;
            else if (isNumber(c)) { //for P2A/P2P
                if ((index + 1) < sentence.length() && isNumber(sentence.charAt(index + 1)))
                    break;
                else continue;
            } else
                return -1;
        }
        return index;
    }
    private static boolean isUPIDelimeter(char c) {
        return (c == Constants.CH_HYPH || c == Constants.CH_SLSH || c == Constants.CH_STAR);
    }
}

