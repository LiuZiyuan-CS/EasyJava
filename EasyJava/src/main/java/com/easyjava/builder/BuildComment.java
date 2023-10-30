package com.easyjava.builder;

import java.io.BufferedWriter;
import java.io.IOException;

public class BuildComment {

    public static void createClassComment(BufferedWriter bufferedWriter, String classComment) throws IOException {
        bufferedWriter.write("/**");
        bufferedWriter.newLine();
        bufferedWriter.write(" * @Description: " + classComment);
        bufferedWriter.newLine();
        bufferedWriter.write(" * @date: ");
        bufferedWriter.newLine();

        bufferedWriter.write(" */");
    }

    public static void createFieldComment() {

    }

    public static void createMethodComment() {

    }
}
