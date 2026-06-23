package com.pydroid.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pydroidide.R;
import com.example.pydroidide.python.PythonRunner;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * 终端独立 Activity — 独立终端窗口，支持运行脚本和输入命令。
 */
public class TerminalActivity extends AppCompatActivity {

    private TextView outputText;
    private EditText commandEdit;
    private ImageButton execButton;
    private ImageButton clearButton;
    private ScrollView scrollView;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final StringBuilder outputBuffer = new StringBuilder();

    private String scriptPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        scriptPath = getIntent().getStringExtra("script_path");

        outputText = findViewById(R.id.tv_output);
        commandEdit = findViewById(R.id.et_command);
        execButton = findViewById(R.id.btn_exec);
        clearButton = findViewById(R.id.btn_clear_term);
        scrollView = findViewById(R.id.scroll_output);

        if (outputText == null || commandEdit == null) {
            // 布局加载失败，退出
            finish();
            return;
        }

        // 初始消息
        appendOutput("PyDroid AI 终端 v1.0\n");
        appendOutput("========================\n");

        if (!TextUtils.isEmpty(scriptPath)) {
            appendOutput("脚本: " + scriptPath + "\n");
            appendOutput("输入命令并点击 ▶ 执行...\n\n");
        } else {
            appendOutput("输入 Python 命令并点击 ▶ 执行...\n");
            appendOutput("输入 :help 获取帮助\n\n");
        }

        execButton.setOnClickListener(v -> execute());
        clearButton.setOnClickListener(v -> {
            outputBuffer.setLength(0);
            outputText.setText("");
            appendOutput(">>> 终端已清屏\n\n");
        });

        // 回车键执行
        commandEdit.setOnEditorActionListener((v, actionId, event) -> {
            execute();
            return true;
        });
    }

    private void execute() {
        String cmd = commandEdit.getText().toString().trim();
        if (TextUtils.isEmpty(cmd)) return;

        appendOutput(">>> " + cmd + "\n");
        commandEdit.setText("");

        // 处理元命令
        if (cmd.startsWith(":")) {
            handleMetaCommand(cmd);
            return;
        }

        // 如果有传入脚本路径且输入为空，运行脚本
        if (!TextUtils.isEmpty(scriptPath) && (cmd.equals("run") || cmd.equals("r"))) {
            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                appendOutput("错误: 脚本文件不存在: " + scriptPath + "\n\n");
                return;
            }
            new PythonRunner(this).execute(scriptFile, new PythonRunner.RunCallback() {
                @Override
                public void onOutput(String output) {
                    mainHandler.post(() -> appendOutput(output));
                }
                @Override
                public void onError(String error) {
                    mainHandler.post(() -> appendOutput("错误: " + error + "\n"));
                }
                @Override
                public void onComplete(int exitCode) {
                    mainHandler.post(() ->
                            appendOutput("\n[完成, 退出码: " + exitCode + "]\n\n"));
                }
            });
            return;
        }

        // 执行任意 Python 语句
        new Thread(() -> {
            try {
                File tmpDir = new File(getCacheDir(), "pyscripts");
                if (!tmpDir.exists()) tmpDir.mkdirs();
                File script = new File(tmpDir, "_term_exec.py");
                String wrapped = "try:\n" +
                        "    " + cmd.replace("\n", "\n    ") + "\n" +
                        "except Exception as __e:\n" +
                        "    print(f'Error: {type(__e).__name__}: {__e}')\n";
                java.io.FileOutputStream fos = new java.io.FileOutputStream(script);
                fos.write(wrapped.getBytes("UTF-8"));
                fos.close();

                ProcessBuilder pb = new ProcessBuilder("python3", script.getAbsolutePath());
                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String finalLine = line;
                        mainHandler.post(() -> appendOutput(finalLine + "\n"));
                    }
                }

                process.waitFor();
                script.delete();
            } catch (Exception e) {
                mainHandler.post(() -> appendOutput("执行失败: " + e.getMessage() + "\n\n"));
            }
        }).start();
    }

    private void handleMetaCommand(String cmd) {
        switch (cmd) {
            case ":help":
                appendOutput("=== 终端帮助 ===\n");
                appendOutput("  :help    - 显示帮助\n");
                appendOutput("  :clear   - 清屏\n");
                appendOutput("  :version - 版本信息\n");
                appendOutput("  run      - 运行脚本（需先传入脚本路径）\n\n");
                break;
            case ":clear":
                outputBuffer.setLength(0);
                outputText.setText("");
                break;
            case ":version":
                appendOutput("PyDroid AI Terminal v1.0\n\n");
                break;
            default:
                appendOutput("未知命令: " + cmd + " (输入 :help 获取帮助)\n\n");
                break;
        }
    }

    private void appendOutput(String text) {
        outputBuffer.append(text);
        outputText.setText(outputBuffer.toString());
        if (scrollView != null) {
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        }
    }
}