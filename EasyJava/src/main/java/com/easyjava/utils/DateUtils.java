package com.easyjava.utils;

import com.easyjava.logger.EasyJavaLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils implements EasyJavaLogger {

    public static final String YYYY_MM_DD="yyyy-MM-dd";
    public static final String _YYYYMMDD="yyyy/MM/dd";
    public static final String YYYYMMDD="yyyyMMdd";
    public static String format(Date date, String pattern){
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String date,String pattern){
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            logger.error(String.valueOf(e));
        }
        throw new RuntimeException("Date parse error");
    }
}
