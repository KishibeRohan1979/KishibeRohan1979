package com.tzp.myTestDemo.testPieBigdata;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import java.util.List;

public class RcodeForJavaRun {

    public static void main(String[] args) {

        String command = "v<-LETTERS[1:4]; for (i in v){ print(i) }";

        List<String> input = new LinkedList<String>();
        input.add("R");
        input.add("--vanilla");
        input.add("-e");
        input.add(command);
        input.add("--slave");
        String result = null;
        try {
            result = execute(input);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String execute(List<String> input) throws Exception {
        // 定义ProcessBuilder对象
        ProcessBuilder pb = new ProcessBuilder(input);
        // 设置工作目录
        pb.directory(null);
        // 启动进程
        Process p = pb.start();
        // 读取进程输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        StringBuilder result = new StringBuilder();
        StringBuilder errorResult = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        while ((line = errorReader.readLine()) != null) {
            errorResult.append(line).append("\n");
        }
        // 等待进程执行完毕
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            errorResult.insert(0, "R程序抛出异常，进程以非零状态退出：" + exitCode + "\n");
            return errorResult.toString();
//            throw new Exception("R程序抛出异常，进程以非零状态退出：" + exitCode + "\n" + errorResult.toString());
        }
        // 返回结果
        return result.toString();
    }

}
