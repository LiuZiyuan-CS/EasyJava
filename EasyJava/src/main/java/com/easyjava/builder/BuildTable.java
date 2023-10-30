package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.logger.EasyJavaLogger;
import com.easyjava.utils.JsonUtils;
import com.easyjava.utils.PropertiesUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildTable implements EasyJavaLogger {
    private static Connection conn = null;
    //    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    private static final String SQL_SHOW_TABLE_STATUS = "show table STATUS";

    private static final String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";

    private static final String SQL_SHOW_TABLE_INDEX = "show index from %s";

    static {
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String user = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            logger.error("类未被找到", e);
        } catch (SQLException e) {
            logger.error("数据库连接失败", e);
        }
    }

    /**
     * 获取表格
     */
    public static List<TableInfo> getTables() {
        PreparedStatement ps = null;
        ResultSet tableResult = null;
        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            tableResult = ps.executeQuery();
            while (tableResult.next()) {
                String tableName = tableResult.getString("name");
                String comment = tableResult.getString("comment");
                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PREFIX) {
                    beanName = tableName.substring(beanName.indexOf("_") + 1);
                }
                beanName = processField(beanName, true);
                TableInfo tableInfo = new TableInfo();

                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_PARAM);
                //设置table的字段信息
                readFieldInfo(tableInfo);
                //设置table key信息
                keyIndexInfo(tableInfo);

                tableInfoList.add(tableInfo);
                logger.info("table :{}", JsonUtils.convertObj2Json(tableInfo));
                logger.info("filedList:{}", JsonUtils.convertObj2Json(tableInfo.getFieldList()));
                logger.info("keyIndexMap:{}", JsonUtils.convertObj2Json(tableInfo.getKeyIndexMap()));
            }
        } catch (Exception e) {
            logger.error("获取表名失败", e);
        } finally {
            if (tableResult != null) {
                try {
                    tableResult.close();
                } catch (SQLException e) {
                    logger.error("tableResult关闭失败", e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("preparedStatement关闭失败", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("数据库connection关闭失败", e);
                }
            }
        }
        return tableInfoList;
    }


    private static void readFieldInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS, tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            while (fieldResult.next()) {
                String field = fieldResult.getString("field");
                String type = fieldResult.getString("type");
                String extra = fieldResult.getString("extra");
                String comment = fieldResult.getString("comment");
                //不需要记录数据库中数据类型的定义的长度
                if (type.indexOf("(") > 0) {
                    type = type.substring(0, type.indexOf("("));
                }
                //处理字段的下划线，方便转化成驼峰命名
                String propertyName = processField(field, false);
                //将字段信息添加到fieldInfoList中
                FieldInfo fieldInfo = new FieldInfo();

                fieldInfoList.add(fieldInfo);

                //记录表中各字段的值
                fieldInfo.setFieldName(field);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(type);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra));
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(processJavaType(type));

                //TODO: if 太多了 处理不够优雅，可以提取函数优化。
                if (!tableInfo.getHaveDateTime()) {
                    tableInfo.setHaveDateTime(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type));
                }
                if (!tableInfo.getHaveDate()) {
                    tableInfo.setHaveDate(ArrayUtils.contains(Constants.SQL_DATE_TYPES, type));
                }
                if (!tableInfo.getHaveBigDecimal()) {
                    tableInfo.setHaveBigDecimal(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type));
                }
            }
            tableInfo.setFieldList(fieldInfoList);
        } catch (Exception e) {
            logger.error("获取表的字段信息失败", e);
        } finally {
            //TODO: 重复代码优化
            try {
                if (fieldResult != null) {
                    fieldResult.close();
                }
            } catch (SQLException e) {
                logger.error("fieldResult关闭失败", e);
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("preparedStatement关闭失败", e);
                }
            }

        }
    }

    /**
     * 设置表的key信息，包括主键，联合主键等
     *
     * @param tableInfo SQL table
     */
    private static void keyIndexInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX, tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            // 使用缓存记录fieldName和fieldInfo，减少循环
            Map<String, FieldInfo> cacheFieldMap = new HashMap<>();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                cacheFieldMap.put(fieldInfo.getFieldName(), fieldInfo);
            }
            while (fieldResult.next()) {
                String keyName = fieldResult.getString("key_name");
                int nonUnique = fieldResult.getInt("non_unique");
                String columnName = fieldResult.getString("column_name");
                if (nonUnique == 1) {
                    continue;
                }
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);

                if (keyFieldList == null) {
                    keyFieldList = new ArrayList<>();
                    tableInfo.getKeyIndexMap().put(keyName, keyFieldList);
                }

//                for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
//                    if (fieldInfo.getFieldName().equals(columnName)) {
//                        keyFieldList.add(fieldInfo);
//                    }
//                }
                keyFieldList.add(cacheFieldMap.get(columnName));
            }
        } catch (Exception e) {
            logger.error("读取索引失败", e);
        } finally {
            try {
                if (fieldResult != null) {
                    fieldResult.close();
                }
            } catch (SQLException e) {
                logger.error("fieldResult关闭失败", e);
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("preparedStatement关闭失败", e);
                }
            }

        }
    }


    /**
     * 将SQL字段转化为 驼峰命名字段
     *
     * @param field                SQL字段
     * @param upperCaseFirstLetter 第一个字母是否大写
     * @return 转化后的驼峰命名字段
     */
    private static String processField(String field, Boolean upperCaseFirstLetter) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] fields = field.split("_");
        stringBuilder.append(upperCaseFirstLetter ? StringUtils.upperCaseFirstLetter(fields[0]) : fields[0]);
        for (int i = 1; i < fields.length; i++) {
            stringBuilder.append(StringUtils.upperCaseFirstLetter(fields[i]));
        }
        return stringBuilder.toString();
    }

    /**
     * 将SQL type 对应 Java type 转化
     *
     * @param type SQLtype
     * @return JavaType
     */
    //TODO: if-else 并列太多了 不够优雅，后期优化（可以用map）
    private static String processJavaType(String type) {
        if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPES, type)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPES, type)) {
            return "Long";
        } else if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)) {
            return "String";
        } else if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type) || ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
            return "Date";
        } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
            return "BigDecimal";
        } else {
            throw new RuntimeException("无法识别的type:" + type);
        }
    }
}
