package com.twelfthmile.yuga.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by johnjoseph on 19/03/17.
 */

@SuppressWarnings("unused")
public class Constants {

    public static final String DT_D = "d";

    public static final String INSTRNO = "INSTRNO";

    public static final String AMT = "AMT";

    public static final String IDVAL = "IDVAL";

    public static final String NUM = "NUM";

    public static final String URL = "URL";

    public static final String DT_DD = "dd";
    public static final String DT_MM = "MM";
    public static final String DT_MMM = "MMM";
    public static final String DT_YY = "yy";
    public static final String DT_YYYY = "yyyy";
    public static final String DT_HH = "HH";
    public static final String DT_mm = "mm";
    public static final String DT_ss = "ss";

    public static final String TY_NUM = "NUM";
    public static final String TY_NUMRANGE = "NUMRANGE";
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

    public static final String[] curr = {"rs", "inr", "cny", "amt", "amount", "ngn", "usd", "cad", "eur", "gbp", "aed", "jpy", "aud", "s$", "lkr", "ksh", "egp"};

    public static final String[] instr = {"card", "no", "a/c"};

    public static final String[] fltid = {"6e", "indigo", "ai", "airindia", "sg", "spicejet", "g8", "goair", "uk", "vistara", "ix",
            "airindiaexpress", "2t", "trujet", "9w", "jetairways", "ek", "emirates"};

    public static final String[] upi = {"airtel", "airtelpaymentsbank", "albk", "allahabadbank", "allbank", "andb", "apb", "apl", "axis", "axisb", "axisbank",
            "axisgo", "bandhan", "barodampay", "barodapay", "birla", "boi", "cbin", "cboi", "centralbank", "cmsidfc", "cnrb", "csbcash", "csbpay",
            "cub", "dbs", "dcb", "dcbbank", "denabank", "dlb", "eazypay", "equitas", "ezeepay", "fbl", "federal", "finobank",
            "hdfcbank", "hdfcbankjd", "hsbc", "icici", "icicibank", "idbi", "idbibank", "idfc", "idfcbank", "idfcnetc", "ikwik", "imobile",
            "indbank", "indianbank", "indianbk", "icicipay", "indus", "iob", "jkb", "jsbp", "karb", "karurvysyabank", "kaypay", "kbl",
            "kbl052", "kmb", "kmbl", "kotak", "kvb", "kvbank", "lime", "lvb", "lvbank", "mahb", "myicici", "obc", "okbizaxis", "okaxis",
            "okhdfcbank", "okicici", "oksbi", "paytm", "payzapp", "pingpay", "pockets", "pnb", "psb", "purz", "rajgovhdfcbank", "sbi",
            "sc", "scb", "scbl", "scmobile", "sib", "srcb", "synd", "syndbank", "syndicate", "tjsb", "ubi", "uboi",
            "uco", "unionbank", "unionbankofindia", "united", "utbi", "vijayabank", "vijb", "vjb", "ybl", "yesbank", "yesbankltd", "upi", "ibl"};

    public static final int CH_NLINE = 10;

    public static final int CH_SPACE = 32;
    public static final int CH_EXCL = 33;
    public static final int CH_DQOT = 34;
    public static final int CH_HASH = 35;
    public static final int CH_PCT = 37;
    public static final int CH_SQOT = 39;
    public static final int CH_LBKT = 40;
    public static final int CH_RBKT = 41;
    public static final int CH_STAR = 42;
    public static final int CH_PLUS = 43;
    public static final int CH_COMA = 44;
    public static final int CH_HYPH = 45;
    public static final int CH_FSTP = 46;
    public static final int CH_SLSH = 47;
    public static final int CH_COLN = 58;
    public static final int CH_SCLN = 59;
    public static final int CH_LSTN = 60;
    public static final int CH_EQLS = 61;
    public static final int CH_GTTN = 62;
    public static final int CH_QUEST = 63;
    public static final int CH_ATRT = 64;


    public static final int CH_LSBT = 91;
    public static final int CH_BKSLSH = 92;
    public static final int CH_LSBTE = 93;
    public static final int CH_UNSC = 95;

    public static final String INDEX = "INDEX";

    public static final String YUGA_CONF_DATE = "YUGA_CONF_DATE";
    public static final String YUGA_SOURCE_CONTEXT = "YUGA_SOURCE_CONTEXT";
    public static final String YUGA_SC_CURR = "YUGA_SC_CURR";
    public static final String YUGA_SC_ON = "YUGA_SC_ON";
    public static final String YUGA_SC_TRANSID = "YUGA_SC_TRANSID";
    private static final String DATE_TIME_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_STR = "yyyy-MM-dd";

    public static final HashMap<Set<String>, String> month = mapMonths();
    public static final HashMap<Set<String>, String> day = mapDays();

    public static final Map<String, String> tokens = mapTokens();
    public static final Map<String, String> possiblePrevTokens = setPrevTokenPossibleList();
    public static Set<String> unmaskTokenSet = new HashSet<>(Arrays.asList(INSTRNO,NUM,IDVAL,AMT,URL));


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
        // Add months in non-english language to these String arrays for support
        String[] jan = {"januari"};
        String[] feb = {"februari"};
        String[] mar = {"mars"};
        //String[] apr = {};
        String[] may = {"maj"};
        String[] jun = {"juni"};
        String[] jul = {"juli"};
        String[] aug = {"augusti"};
        //String[] sep = {};
        String[] oct = {"okt"};
        //String[] nov = {};
        //String[] dec = {};


        HashMap<Set<String>,String> months = new HashMap<>();
        months.put(new HashSet<>(Arrays.asList(jan)),"january");
        months.put(new HashSet<>(Arrays.asList(feb)),"february");
        months.put(new HashSet<>(Arrays.asList(mar)),"march");
        months.put(new HashSet<>(Arrays.asList(may)),"may");
        months.put(new HashSet<>(Arrays.asList(jun)),"june");
        months.put(new HashSet<>(Arrays.asList(jul)),"july");
        months.put(new HashSet<>(Arrays.asList(aug)),"august");
        months.put(new HashSet<>(Arrays.asList(oct)),"october");

        return months;
    }

    private static HashMap<Set<String>,String> mapDays(){
        // Add days in non-english language to these String arrays for support
        String[] mond = {"måndag","må"};
        String[] tues = {"tisdag","ti"};
        String[] wedn = {"onsdag"};
        String[] thur = {"torsdag"};
        String[] frid = {"fredag","fr"};
        String[] satu = {"lördag","lö"};
        String[] sund = {"söndag","sö"};

        HashMap<Set<String>,String> days = new HashMap<>();
        days.put(new HashSet<>(Arrays.asList(mond)),"monday");
        days.put(new HashSet<>(Arrays.asList(tues)),"tuesday");
        days.put(new HashSet<>(Arrays.asList(wedn)),"wednesday");
        days.put(new HashSet<>(Arrays.asList(thur)),"thursday");
        days.put(new HashSet<>(Arrays.asList(frid)),"friday");
        days.put(new HashSet<>(Arrays.asList(satu)),"saturday");
        days.put(new HashSet<>(Arrays.asList(sund)),"sunday");

        return days;
    }
    private static Map<String, String>  mapTokens() {
        HashMap<String, String> getTokens = new HashMap<>();
        getTokens.put("upi", "TRANSFER");
        getTokens.put("neft", "TRANSFER");
        getTokens.put("mmt", "TRANSFER");
        getTokens.put("sms", "SMS");
        getTokens.put("www", "WWW");
        getTokens.put("crn", "IDPRX");
        getTokens.put("krn", "IDPRX");
        getTokens.put("http", "HTTP");
        getTokens.put("https", "HTTP");
        return getTokens;
    }
    private static Map<String, String> setPrevTokenPossibleList() {
        HashMap<String, String> getTokenList = new HashMap<>();
        getTokenList.put("no", "ID");
        getTokenList.put("pnr", "ID");
        getTokenList.put("id", "ID");
        getTokenList.put("promo", "ID");
        getTokenList.put("awb", "ID");
        getTokenList.put("utr", "ID");
        getTokenList.put("code", "ID");
        getTokenList.put("number", "ID");
        getTokenList.put("order", "ID");
        getTokenList.put("a/c", "INS");
        getTokenList.put("account", "INS");
        getTokenList.put("loan", "INS");
        return getTokenList;
    }
}
