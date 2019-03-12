package com.twelfthmile.yuga;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twelfthmile.yuga.types.Response;
import com.twelfthmile.yuga.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

class GenerateTestData {
    public static void main(String[] args) throws IOException {
        Map<String, String> configMap = new HashMap<>();

        //noinspection deprecation
        configMap.put(Constants.YUGA_CONF_DATE, Constants.dateTimeFormatter().format(new Date(2019, 1, 1)));

        TypeReference<String[]> arrayRef = new TypeReference<String[]>() {
        };

        String[] dates;
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = GenerateTestData.class.getResourceAsStream("/yuga_test.json")) {
            dates = mapper.readValue(is, arrayRef);
        }

        List<TestDataPacket> testData = new ArrayList<>();
        Stream.of(dates).
                forEach(date -> {
                    Response response = Yuga.parse(date,configMap);
                    if(response!=null) {
                        TestDataPacket packet = new TestDataPacket(date, response, true);
                        testData.add(packet);
                    }
                });
        System.out.println(mapper.writerWithDefaultPrettyPrinter().

                writeValueAsString(testData));
    }
}
