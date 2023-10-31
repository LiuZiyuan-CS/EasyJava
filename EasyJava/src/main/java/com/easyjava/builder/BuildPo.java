package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.logger.EasyJavaLogger;

import java.io.*;
import java.nio.file.Files;

public class BuildPo implements EasyJavaLogger {
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_PO);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File poFile = new File(folder, tableInfo.getBeanName() + ".java");

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;

        try {
            outputStream = Files.newOutputStream(poFile.toPath());
            outputStreamWriter = new OutputStreamWriter(outputStream, "utf8");
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            //Package 引入
            bufferedWriter.write("package " + Constants.PACKAGE_PO + ";");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            //Serializable 接口引入
            bufferedWriter.write("import java.io.Serializable;");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            //Date 是否存在
            if(tableInfo.getHaveDate()|| tableInfo.getHaveDateTime()){
                bufferedWriter.write("import java.util.Date;");
                bufferedWriter.newLine();
            }
            //BigDecimal 是否存在
            if(tableInfo.getHaveBigDecimal()){
                bufferedWriter.write("import java.math.BigDecimal;");
                bufferedWriter.newLine();
            }
            //类注解
            BuildComment.createClassComment(bufferedWriter,tableInfo.getComment());
            bufferedWriter.newLine();
            //bean类开头
            bufferedWriter.write("public class " + tableInfo.getBeanName() + " implements Serializable{");
            bufferedWriter.newLine();
            //bean属性 写入
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                BuildComment.createFieldComment(bufferedWriter, fieldInfo.getComment());
                bufferedWriter.newLine();
                bufferedWriter.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
            }

            //bean类结尾
            bufferedWriter.write("}");
            bufferedWriter.flush();
        } catch (Exception e) {
            logger.error("创建PO失败", e);
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    logger.error("bufferedWriter关闭失败", e);
                }
            }

            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (IOException e) {
                    logger.error("outputStreamWriter关闭失败", e);
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("outputStream关闭失败", e);
                }
            }
        }
    }
}
