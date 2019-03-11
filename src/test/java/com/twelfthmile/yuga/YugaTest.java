package com.twelfthmile.yuga;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twelfthmile.yuga.types.ParseException;
import com.twelfthmile.yuga.types.Response;
import com.twelfthmile.yuga.utils.Constants;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class YugaTest {
    private final Map<String, String> configMap = new HashMap<>();
    {
        //noinspection deprecation
        configMap.put(Constants.YUGA_CONF_DATE, Constants.dateTimeFormatter().format(new Date(118, 1, 1)));
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
        try {
            Response r = Yuga.parse(input, configMap);
            assertEquals(response, r, response.toString());
        } catch (ParseException e) {
            if (accepted) {
                fail("Failed parsing", e);
            }
        }
        if (!accepted) {
            fail("Was indicated to fail parsing, but succeeded");
        }
    }
}
