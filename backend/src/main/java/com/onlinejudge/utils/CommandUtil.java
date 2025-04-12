package com.onlinejudge.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

// 为了能执行javac编译命令，和java运行指令。
public class CommandUtil {
    public static int run(String cmd, String stdoutFile,
                          String stderrFile) throws IOException, InterruptedException {
        // 下边表示创建一个进程，用process表示。
        // Process process = Runtime.getRuntime().exec(cmd);
        ProcessBuilder processBuilder = new ProcessBuilder(cmd.split(" "));
        Process process = processBuilder.start();

        // 标准输出中有内容，也就是下边的文件的内容不是空的，给标准输出进行重定向。
        if (stdoutFile != null) {
            InputStream stdoutFrom = process.getInputStream();
            FileOutputStream stdoutTo = new FileOutputStream(stdoutFile);
            while (true) {
                int ch = stdoutFrom.read();
                if (ch == -1) {
                    break;
                }
                stdoutTo.write(ch);
            }

            stdoutFrom.close();
            stdoutTo.close();
        }

        // 同上，再针对标准错误进行重定向
        if (stderrFile != null) {
            InputStream stderrFrom = process.getErrorStream();
            FileOutputStream stderrTo = new FileOutputStream(stderrFile);
            while (true) {
                int ch = stderrFrom.read();
                if (ch == -1) {
                    break;
                }
                stderrTo.write(ch);
            }

            stderrFrom.close();
            stderrTo.close();
        }

        int exitCode = process.waitFor();// 等待一下，等新的进程运行结束
        return exitCode;
    }
    public static void main(String[] args) {
        try {
            int ret = CommandUtil.run("javac", "./stdout.txt", "./stderr.txt");
            System.out.println(ret);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
