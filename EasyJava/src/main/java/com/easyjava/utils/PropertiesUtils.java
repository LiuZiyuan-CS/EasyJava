package com.easyjava.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesUtils {
    private static final String PROPERTY_FILE = "application.properties";
    private static final Properties props = new Properties();
    private static final Map<String, String> PROPER_MAP = new ConcurrentHashMap<>();

    static {
        InputStream inputStream = null;
        try {
            inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(PROPERTY_FILE);
            props.load(inputStream);
            for (Object o : props.keySet()) {
                String key = String.valueOf(o);
                PROPER_MAP.put(key, props.getProperty(key));
            }
        } catch (Exception ignored) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getString(String property) {
        return PROPER_MAP.get(property);
    }

}
