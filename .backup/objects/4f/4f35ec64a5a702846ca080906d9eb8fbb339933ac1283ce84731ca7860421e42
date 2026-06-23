package com.example.pydroidide.python;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Python 脚本执行器：通过内置/外部 Python 解释器运行 .py 文件。
 * 当前实现使用系统 python3 命令行（需设备已安装 Termux 或类似环境）。
 */
public class PythonRunner {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Context context;

    public PythonRunner(Context context) {
        this.context = context;
    }

    /**
     * 执行 Python 脚本，并通过回调返回输出。
     */
    public void execute(File scriptFile, RunCallback callback) {
        executor.execute(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    "python3", scriptFile.getAbsolutePath()
                );
                pb.directory(scriptFile.getParentFile());
                pb.redirectErrorStream(false);
                Process process = pb.start();

                // stdout
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (callback != null) callback.onOutput(line + "\n");
                    }
                }

                // stderr
                StringBuilder errBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errBuilder.append(line).append("\n");
                    }
                }

                int exitCode = process.waitFor();

                if (errBuilder.length() > 0 && callback != null) {
                    callback.onError(errBuilder.toString());
                }

                if (callback != null) callback.onComplete(exitCode);

            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e.getMessage() != null ? e.getMessage() : e.toString());
                    callback.onComplete(-1);
                }
            }
        });
    }

    /**
     * 智能分析常见 Python 错误并提供建议。
     */
    public static String analyzeError(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) return "无错误信息。";

        StringBuilder analysis = new StringBuilder();

        if (errorMessage.contains("SyntaxError") || errorMessage.contains("SyntaxError:")) {
            analysis.append("⚠️ 语法错误：请检查代码中是否缺少冒号(:)、括号是否匹配、缩进是否正确。\n");
            analysis.append("常见原因：\n");
            analysis.append("  - 忘记在 if/for/while/def/class 语句末尾加冒号\n");
            analysis.append("  - 括号或引号未正确闭合\n");
            analysis.append("  - 使用了中文标点符号（如 ：代替:）\n");
        } else if (errorMessage.contains("IndentationError")) {
            analysis.append("⚠️ 缩进错误：Python 严格要求缩进一致，请确保使用统一的 Tab 或空格。\n");
            analysis.append("建议：使用 4 个空格进行缩进。\n");
        } else if (errorMessage.contains("NameError")) {
            analysis.append("⚠️ 名称错误：使用了未定义的变量或函数。\n");
            analysis.append("请检查变量名是否拼写正确，是否在调用前已定义。\n");
        } else if (errorMessage.contains("TypeError")) {
            analysis.append("⚠️ 类型错误：对变量执行了不支持的操作。\n");
            analysis.append("检查是否把字符串和数字直接拼接，或传递了错误类型的参数。\n");
        } else if (errorMessage.contains("ImportError") || errorMessage.contains("ModuleNotFoundError")) {
            analysis.append("⚠️ 导入错误：无法找到指定的模块。\n");
            analysis.append("检查模块名是否正确，或者是否需要先安装该模块（pip install）。\n");
        } else if (errorMessage.contains("FileNotFoundError")) {
            analysis.append("⚠️ 文件未找到：指定的文件路径不存在。\n");
            analysis.append("请确认文件路径是否正确，文件是否已被移动或删除。\n");
        } else if (errorMessage.contains("IndexError")) {
            analysis.append("⚠️ 索引越界：访问了列表/字符串等超出范围的索引。\n");
            analysis.append("检查索引值是否超过了容器长度减一。\n");
        } else if (errorMessage.contains("KeyError")) {
            analysis.append("⚠️ 键错误：字典中不存在指定的键。\n");
            analysis.append("可以使用 dict.get(key, default) 来安全访问字典。\n");
        } else if (errorMessage.contains("AttributeError")) {
            analysis.append("⚠️ 属性错误：对象没有指定的属性或方法。\n");
            analysis.append("检查对象类型是否正确，方法名是否拼写正确。\n");
        } else if (errorMessage.contains("ZeroDivisionError")) {
            analysis.append("⚠️ 除零错误：不能以 0 作为除数。\n");
            analysis.append("请在除法运算前检查除数是否为零。\n");
        } else {
            analysis.append("📋 请仔细阅读错误堆栈信息，追踪出错位置和原因。\n");
            analysis.append("如果是运行时错误，可以尝试添加打印语句进行调试。\n");
        }

        return analysis.toString();
    }

    public interface RunCallback {
        void onOutput(String output);
        void onError(String error);
        void onComplete(int exitCode);
    }

    /**
     * 释放线程资源。
     */
    public void shutdown() {
        executor.shutdown();
    }
}