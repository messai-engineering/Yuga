package com.twelfthmile.yuga;


import com.twelfthmile.yuga.types.Response;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;



public class GetOutputTest {
    private final Map<String, String> configMap = new HashMap<>();
    {
        //configMap.put(Constants.YUGA_CONF_DATE, Constants.dateTimeFormatter().format(new Date(1527811200000L)));//as tests are based on year 2018, giving current date as 06/01/2018 @ 12:00am (UTC)
        //configMap.put(Constants.YUGA_SOURCE_CONTEXT,Constants.YUGA_SC_ON);
        //configMap.put(Constants.YUGA_SOURCE_CONTEXT, Constants.YUGA_SC_CURR);
    }

    @Test
    public void testYuga() {
        String str = "12-451";
        Response r = Yuga.parse( str, configMap);
        if(r!=null) {
            System.out.println(r.toString());
            System.out.println(str.substring(0,r.getIndex()));
        }
        else
            System.out.println("Fail");
    }
//

}
