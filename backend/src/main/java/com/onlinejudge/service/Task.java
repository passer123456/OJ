package com.onlinejudge.service;

import com.onlinejudge.entity.Answer;
import com.onlinejudge.entity.Request;
import com.onlinejudge.utils.CommandUtil;
import com.onlinejudge.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
// 这个类表示一个完整的编译运行的过程
public class Task {
    // 此处罗列出需要的临时文件(用于进程间通信)文件名约定
    // 所有的临时文件要放到这个目录中
    private String WORK_DIR;
    // 要编译执行的类的名字, 影响到源代码的文件名
    private String CLASS = "Solution";
    // 要编译执行的文件名
    private String CODE;
    // 程序标准输出放置的文件
    private String STDOUT;
    // 程序标准错误放置的文件
    private String STDERR;
    // 程序编译出错的详细信息放置的文件
    private String COMPILE_ERROR;

    private int userID;
    private int ID;


    private int extractScore(String stdout) {
        // 定义正则表达式来查找以 "score=" 开头的分数
        Pattern pattern = Pattern.compile("score=(\\d+)");
        Matcher matcher = pattern.matcher(stdout);

        if (matcher.find()) {
            // 如果匹配成功，提取分数
            return Integer.parseInt(matcher.group(1));
        }
        // 如果没有找到分数，返回默认值 0
        return 0;
    }

    public static String formatTime(long time) {
        long seconds = time / 1000;       // 计算秒数
        long milliseconds = time % 1000;  // 计算剩余的毫秒数
        return seconds + "秒 " + milliseconds + "毫秒";
    }

    public static String formatMemory(long memory) {
        if (memory < 1024) {
            return memory + " 字节";
        } else if (memory < 1024 * 1024) {
            return String.format("%.2f KB", memory / 1024.0);
        } else if (memory < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", memory / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", memory / (1024.0 * 1024 * 1024));
        }
    }

    // Answer 表示代码的编译运行结果
    public Answer compileAndRun(Request question) throws IOException, InterruptedException {
        // 先生成唯一的id, 根据这个 id 来拼装出目录的名字
        userID = question.getUserId();
        ID = question.getId();
        WORK_DIR = "./tmp/" + userID + "/" + ID + "/";
        // 然后再生成后续的这些文件名
        CODE = WORK_DIR + CLASS + ".java";
        STDOUT = WORK_DIR + "stdout.txt";
        STDERR = WORK_DIR + "stderr.txt";
        COMPILE_ERROR = WORK_DIR + "compile_error.txt";

        Answer answer = new Answer();
        File file = new File(WORK_DIR);
        if (!file.exists()) {
            // 创建对应的目录
            file.mkdirs();
        }

        FileUtil.writeFile(CODE, question.getCode());

        // 构造编译指令
        String compileCmd = String.format("javac -encoding utf-8 %s -d %s",
                CODE, WORK_DIR);
        System.out.println("编译命令: " + compileCmd);
        CommandUtil.run(compileCmd, null, COMPILE_ERROR);
        String compileError = FileUtil.readFile(COMPILE_ERROR);
        if (!compileError.equals("")) {
            // 编译错误的文件不为空, 说明编译出错了
            answer.setErrno(1);// 将退出码设置成1.
            answer.setCompile_error(compileError);
            return answer;
        }

        // 构造运行指令
        String runCmd = String.format("java -classpath %s %s", WORK_DIR, CLASS);
        System.out.println("runCmd: " + runCmd);

        Runtime runtime = Runtime.getRuntime();
        // 在开始时记录内存和时间
        long startTime = System.currentTimeMillis();
        long startMemory = runtime.totalMemory() - runtime.freeMemory();

        CommandUtil.run(runCmd, STDOUT, STDERR);

        long endTime = System.currentTimeMillis();
        long endMemory = runtime.totalMemory() - runtime.freeMemory();

        // 计算运行时间和内存使用量
        long elapsedTime = endTime - startTime; // 运行时间（毫秒）
        long memoryUsed = endMemory - startMemory; // 使用的内存（字节）

        String runError = FileUtil.readFile(STDERR);
        if (!runError.equals("")) {
            // 运行出错
            answer.setErrno(2);// 将退出码设置成2
            answer.setStderr(runError);
            return answer;
        }

        // 把最终结果构造成 Answer 对象, 并返回
        answer.setErrno(0);
        String runStdout = FileUtil.readFile(STDOUT);
        answer.setStdout(runStdout);
        answer.setScore(extractScore(runStdout));
        answer.setRuntime(formatTime(elapsedTime));
        answer.setMemory(formatMemory(memoryUsed));
        return answer;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Task task = new Task();
        Request question = new Request();
        question.setCode("public class Solution {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"score=80\");\n" +
                "    }\n" +
                "}\n");
        question.setId(1);
        question.setUserId(666);
        Answer answer = task.compileAndRun(question);
        System.out.println(answer);
    }
}