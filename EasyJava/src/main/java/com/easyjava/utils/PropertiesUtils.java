package com.easyjava.utils;


import com.easyjava.logger.EasyJavaLogger;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesUtils implements EasyJavaLogger {
    private static final String PROPERTY_FILE_PATH = "application.properties";
    private static final Properties props = new Properties();
    private static final Map<String, String> PROPER_MAP = new ConcurrentHashMap<>();

    static {
        InputStream inputStream = null;
        try {
            File propertyFile = new ClassPathResource(PROPERTY_FILE_PATH).getFile();
            if (propertyFile.exists()) {
                inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_PATH);
                if (inputStream != null) {
                    props.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    for (Object o : props.keySet()) {
                        String key = String.valueOf(o);
                        PROPER_MAP.put(key, props.getProperty(key));
                    }
                } else {
                    throw new IOException();
                }
            } else {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            logger.error("配置文件未找到", e);
        } catch (IOException e) {
            logger.error(String.valueOf(e));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(String.valueOf(e));
                }
            }
        }
    }

    /**
     * @param key PROP_MAP的key
     * @return 返回PROP_MAP的值
     */
    public static String getString(String key) {
        try {
            if (PROPER_MAP.containsKey(key)) {
                return PROPER_MAP.get(key);
            } else {
                throw new RuntimeException("属性文件中不存在键{" + key + "}");
            }
        } catch (RuntimeException e) {
            logger.error(String.valueOf(e));
        }
        throw new RuntimeException("属性文件中未找到key为" + key + "的属性");
    }

}
