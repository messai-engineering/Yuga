package com.twelfthmile.yuga;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twelfthmile.yuga.types.Response;
import com.twelfthmile.yuga.utils.Constants;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class YugaTest {
    private final Map<String, String> configMap = new HashMap<>();
    {
        //noinspection deprecation
        configMap.put(Constants.YUGA_CONF_DATE, Constants.dateTimeFormatter().format(new Date(1527811200000L)));//as tests are based on year 2018, giving current date as 06/01/2018 @ 12:00am (UTC)
        configMap.put(Constants.YUGA_SOURCE_CONTEXT,Constants.YUGA_SC_ON);
    }

    @DataProvider
    Object[][] getTestData() throws IOException {
        TypeReference<TestDataPacket[]> arrayRef = new TypeReference<TestDataPacket[]>() {
        };
        TestDataPacket[] data;
        try (InputStream inputStream = this.getClass().getResourceAsStream("/yuga_tests.json")) {
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.readValue(inputStream, arrayRef);
        }
        Object[][] testData = new Object[data.length][];
        for (int i = -0; i < data.length; i++) {
            testData[i] = new Object[]{data[i].input, data[i].response, data[i].accepted};
        }
        return testData;
    }

    @Test(dataProvider = "getTestData")
    public void testDateParsing(String input, Response response, boolean accepted) {
            Response r = Yuga.parse(input, configMap);
            if(r!=null) {
                assertEquals(response, r, response.toString());
                if (!accepted)
                    fail("Was indicated to fail parsing, but succeeded");
            }
            else if (accepted)
                fail("Failed parsing");
    }

    @Test
    public void testYuga() {
        Response r = Yuga.parse("3,6,9,12,18,24 ", configMap);//130,79 150000,60
        if(r!=null)
            System.out.println(r.toString());
        else
            System.out.println("Fail");
    }

    @Test
    public void testFormatDateTimeToDate() throws ParseException {
        String output = Constants.formatDateTimeToDate("2019-08-21 12:58:22",
                "yyyy-MM-dd HH:mm:ss");
        assertEquals(10, output.length());
        assertEquals("2019-08-21", output);
    }

    @Test
    public void testFormatDateTimeToDateThrowsException() {
        try {
            Constants.formatDateTimeToDate("2019-08 12:58:22",
                    "yyyy-MM-dd HH:mm:ss");
            Assert.fail("Should have thrown parse exception");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
