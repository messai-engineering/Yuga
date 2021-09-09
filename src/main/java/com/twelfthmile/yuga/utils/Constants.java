package com.twelfthmile.yuga.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by johnjoseph on 19/03/17.
 */

@SuppressWarnings("unused")
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
    public static final String TY_WGT = "WGT";
    public static final String TY_ACC = "INSTRNO";
    public static final String TY_TYP = "TYP";
    public static final String TY_DTE = "DATE";
    public static final String TY_TME = "TIME";
    public static final String TY_STR = "STR";
    public static final String TY_PHN = "PHN";
    public static final String TY_TMS = "TIMES";
    public static final String TY_OTP = "OTP";
    public static final String TY_DTA = "DATA";
    public static final String TY_MLT = "MLTPL";
    public static final String TY_VPD = "VPD"; //VPA-ID
    public static final String TY_USSD = "USSD";
    //public static final String TY_DCT = "DCT"; //date context like sunday,today,tomorrow

    public static final String FSA_MONTHS = "jan;uary,feb;r;uary,mar;ch,apr;il,may,jun;e,jul;y,aug;ust,sep;t;ember,oct;ober,nov;ember,dec;ember";
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
    public static final int CH_HASH = 35;
    public static final int CH_SCLN = 59;
    public static final int CH_PLUS = 43;
    public static final int CH_ATRT = 64;
    public static final int CH_RBKT = 41;
    public static final int CH_STAR = 42;

    public static final int CH_LSBT = 91;

    public static final String INDEX = "INDEX";

    public static final String YUGA_CONF_DATE = "YUGA_CONF_DATE";
    public static final String YUGA_SOURCE_CONTEXT = "YUGA_SOURCE_CONTEXT";
    public static final String YUGA_SC_CURR = "YUGA_SC_CURR";
    public static final String YUGA_SC_ON = "YUGA_SC_ON";
    public static final String YUGA_SC_TRANSID = "YUGA_SC_TRANSID";
    private static final String DATE_TIME_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_STR = "yyyy-MM-dd";

    public static final HashMap<Set<String>,String> month = mapMonths();
    public static final HashMap<Set<String>,String> day = mapDays();

    public static String formatDateTimeToDate(String date, String inputFormat) throws ParseException {
        return dateFormatter().format(new SimpleDateFormat(inputFormat).parse(date));
    }

    public static SimpleDateFormat dateTimeFormatter() {
        return new SimpleDateFormat(DATE_TIME_FORMAT_STR, Locale.ENGLISH);
    }

    public static SimpleDateFormat dateFormatter() {
        return new SimpleDateFormat(DATE_FORMAT_STR, Locale.ENGLISH);
    }

    private static HashMap<Set<String>,String> mapMonths(){
        HashMap<Set<String>,String> months = new HashMap()
        {{
            put(Stream.of("januari").collect(Collectors.toCollection(HashSet::new)),"january");
            put(Stream.of("februari").collect(Collectors.toCollection(HashSet::new)),"february");
            put(Stream.of("mars").collect(Collectors.toCollection(HashSet::new)),"march");
          //  put(Stream.of("").collect(Collectors.toCollection(HashSet::new)),"april");
            put(Stream.of("maj").collect(Collectors.toCollection(HashSet::new)),"may");
            put(Stream.of("juni").collect(Collectors.toCollection(HashSet::new)),"june");
            put(Stream.of("juli").collect(Collectors.toCollection(HashSet::new)),"july");
            put(Stream.of("augusti").collect(Collectors.toCollection(HashSet::new)),"august");
          //  put(Stream.of("").collect(Collectors.toCollection(HashSet::new)),"september");
            put(Stream.of("okt").collect(Collectors.toCollection(HashSet::new)),"october");
          //  put(Stream.of("").collect(Collectors.toCollection(HashSet::new)),"november");
          //  put(Stream.of("").collect(Collectors.toCollection(HashSet::new)),"december");
        }};

        return months;
    }

    private static HashMap<Set<String>,String> mapDays(){
        HashMap<Set<String>,String> months = new HashMap()
        {{
            put(Stream.of("måndag").collect(Collectors.toCollection(HashSet::new)),"monday");
            put(Stream.of("tisdag").collect(Collectors.toCollection(HashSet::new)),"tuesday");
            put(Stream.of("onsdag").collect(Collectors.toCollection(HashSet::new)),"wednesday");
            put(Stream.of("torsdag").collect(Collectors.toCollection(HashSet::new)),"thursday");
            put(Stream.of("fredag").collect(Collectors.toCollection(HashSet::new)),"friday");
            put(Stream.of("lördag").collect(Collectors.toCollection(HashSet::new)),"saturday");
            put(Stream.of("söndag").collect(Collectors.toCollection(HashSet::new)),"sunday");
        }};

        return months;
    }

}
