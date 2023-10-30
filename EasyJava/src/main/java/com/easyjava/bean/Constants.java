package com.easyjava.bean;

import com.easyjava.utils.PropertiesUtils;

public class Constants {
    public static Boolean IGNORE_TABLE_PREFIX;
    public static String SUFFIX_BEAN_PARAM;

    public static String PATH_BASE;
    public static String PACKAGE_BASE;
    public static String PATH_PO;
    public static String PACKAGE_PO;
    public static String PACKAGE_PARAM;

    private static final String PATH_JAVA = "java/";

    private static final String PATH_RESOURCE = "resource/";


    //TODO： 类型对应不完全，后期要补充，考虑优化记录SQL type的数据类型
    public static final String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
    public static final String[] SQL_DATE_TYPES = new String[]{"date"};
    public static final String[] SQL_DECIMAL_TYPES = new String[]{"decimal", "float", "double"};
    public static final String[] SQL_STRING_TYPES = new String[]{"char", "varchar", "text", "mediumtext", "longtext"};
    public static final String[] SQL_INTEGER_TYPES = new String[]{"int", "tinyint"};
    public static final String[] SQL_LONG_TYPES = new String[]{"bigint"};

    static {
        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_PARAM = PropertiesUtils.getString("suffix.bean.param");
        //文件根路径
        PATH_BASE = PropertiesUtils.getString("path.base") + PATH_JAVA + PropertiesUtils.getString("package.base").replace(".", "/");
        //bean文件路径
        PATH_PO = PATH_BASE + "/" + PropertiesUtils.getString("package.po").replace('.', '/');

        //基础包
        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        //bean 包
        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.po");
        // param 包
        PACKAGE_PARAM = PACKAGE_BASE + "." + PropertiesUtils.getString("package.param");
    }

}
