package com.twelfthmile.yuga;
import com.twelfthmile.yuga.classifierYuga.ClassifierYuga;
import com.twelfthmile.yuga.utils.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

import com.twelfthmile.yuga.types.IndexTrack;

public class YugaTokens {
    @Test
    public void main() throws Exception {
        File testInput = new File("src/test/resources/in/test.json");
        if (testInput.exists()) {
            JSONArray jsonArr = new JSONArray(readFile(testInput));
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                HashMap<String, String> configMap = new HashMap<String, String>();
                configMap.put(Constants.YUGA_CONF_DATE,
                        Constants.dateTimeFormatter().format(new Date()));
                String reNew = ClassifierYuga.getYugaTokensNew(jsonObj.getString("body"), configMap, new IndexTrack(0)).toString();
                System.out.println();
                out.append(reNew).append("\n");
            }
            System.out.println(out);
        }
    }
    public static String readFile(File f){
        String result = "";
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = new FileInputStream(f);
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (br != null)
                    br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}

