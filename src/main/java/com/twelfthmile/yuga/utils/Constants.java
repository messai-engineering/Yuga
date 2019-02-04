package com.twelfthmile.yuga.utils;

import java.text.SimpleDateFormat;

/**
 * Created by johnjoseph on 19/03/17.
 */

public class Constants {

    public static final String DT_D = "d";
    public static final String DT_DD = "dd";
    public static final String DT_MM = "MM";
    public static final String DT_MMM = "MMM";
    public static final String DT_YY = "yy";
    public static final String DT_YYYY = "yyyy";
    public static final String DT_HH = "HH";
    public static final String DT_mm = "mm";
    public static final String DT_ss = "ss";

    public static final String TY_NUM = "NUM";
    public static final String TY_AMT = "AMT";
    public static final String TY_PCT = "PCT";
    public static final String TY_DST = "DST";
    public static final String TY_ACC = "INSTRNO";
    public static final String TY_TYP = "TYP";
    public static final String TY_DTE = "DATE";
    public static final String TY_TME = "TIME";
    public static final String TY_STR = "STR";
    public static final String TY_PHN = "PHN";
    public static final String TY_TMS = "TIMES";
    public static final String TY_OTP = "OTP";
    public static final String TY_VPD = "VPD"; //VPA-ID
    //public static final String TY_DCT = "DCT"; //date context like sunday,today,tomorrow

    public static final String FSA_MONTHS = "jan;uary,feb;ruary,mar;ch,apr;il,may,jun;e,jul;y,aug;ust,sep;t;ember,oct;ober,nov;ember,dec;ember";
    public static final String FSA_DAYS = "sun;day,mon;day,tue;sday,wed;nesday,thu;rsday,thur;sday,fri;day,sat;urday";
    public static final String FSA_TIMEPRFX = "at,on,before,by";
    public static final String FSA_AMT = "lac,lakh,k";
    public static final String FSA_TIMES = "hours,hrs,hr,mins,minutes";
    public static final String FSA_TZ = "gmt,ist";
    public static final String FSA_DAYSFFX = "st,nd,rd,th";
    public static final String FSA_UPI = "UPI,MMT,NEFT";

    public static final int CH_SPACE = 32;
    public static final int CH_PCT = 37;
    public static final int CH_SQOT = 39;
    public static final int CH_COMA = 44;
    public static final int CH_HYPH = 45;
    public static final int CH_FSTP = 46;
    public static final int CH_SLSH = 47;
    public static final int CH_COLN = 58;
    public static final int CH_SCLN = 59;
    public static final int CH_PLUS = 43;
    public static final int CH_ATRT = 64;
    public static final int CH_RBKT = 41;
    public static final int CH_STAR = 42;

    public static final String INDEX = "INDEX";

    public static final String YUGA_CONF_DATE = "YUGA_CONF_DATE";
    public static final String YUGA_SOURCE_CONTEXT = "YUGA_SOURCE_CONTEXT";
    public static final String YUGA_SC_CURR = "YUGA_SC_CURR";
    public static final String DATE_TIME_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    public static SimpleDateFormat dateTimeFormatter() {
        return new SimpleDateFormat(DATE_TIME_FORMAT_STR);
    }

}
