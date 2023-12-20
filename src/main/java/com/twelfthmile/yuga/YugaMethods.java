package com.twelfthmile.yuga;

import com.twelfthmile.yuga.utils.Constants;
import com.twelfthmile.yuga.utils.Util;

import java.util.Arrays;


public class YugaMethods {
    // for cases like 18th Jun, 12 pm
    static boolean lookAheadForMerid(String str, int index) {
        if ( index+4>=str.length())
            return false;
        for(int i =index+1;i<index+4;i++){
            if(Util.meridienTimeAhead(str,i)==true)
                return true;
        }
        return false;
    }
    static int lookAheadForNum(String str, int index) {
        char c;
        for (int i = index+1; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == Constants.CH_SPACE) {
            }
            else if (Util.isNumber(c))
                return i-1; //Assuming the index will get incremented by the loop to get to the num
            else
                return -1;
        }
        return -1;
    }
    static int lookAheadForInstr(String str, int index) {
        char c;
        for (int i = index; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == Constants.CH_FSTP) {
            }
            else if (c == 42 || c == 88 || c == 120 || Util.isNumber(c))
                return i;
            else
                return -1;
        }
        return -1;
    }
    static boolean isInstrNumStart(char c) {
        return (c == 42 || c == 88 || c == 120);
        //*xX
    }
    static String getAmt(String type) {
        switch (type) {
            case "lakh":
            case "lac":
                return "00000";
            case "k":
                return "000";
            default:
                return "";
        }
    }

    static boolean isCurrencyAhead(String type) {
        type = getPotentialCurrString(type);
        if(Arrays.asList(Constants.currAct).contains(type.toLowerCase()))
            return true;
        return false;
    }

    static String getPotentialCurrString(String type) {
        int next = nextSpace(type);
        type = type.substring(0,next);
        return type;
    }

    static int nextSpace(String str) {
        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == ' ')
                return i;
            else
                i++;
        }
        return i;
    }
}

