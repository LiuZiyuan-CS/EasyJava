package com.easyjava.builder;

import com.easyjava.utils.DateUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

public class BuildComment {

    public static void createClassComment(BufferedWriter bufferedWriter, String classComment) throws IOException {
        bufferedWriter.write("/**");
        bufferedWriter.newLine();
        bufferedWriter.write(" * @Description: " + classComment);
        bufferedWriter.newLine();
        bufferedWriter.write(" * @date: " + DateUtils.format(new Date(), DateUtils._YYYYMMDD));
        bufferedWriter.newLine();

        bufferedWriter.write(" */");
    }

    public static void createFieldComment(BufferedWriter bufferedWriter, String fieldComment) throws IOException {
        bufferedWriter.write("\t/*");
        bufferedWriter.newLine();
        bufferedWriter.write("\t * " + (fieldComment == null ? "" : fieldComment));
        bufferedWriter.newLine();
        bufferedWriter.write("\t */");
    }

    public static void createMethodComment() {

    }
}
