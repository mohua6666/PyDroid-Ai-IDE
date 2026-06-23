package com.pydroid.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pydroidide.R;
import com.example.pydroidide.ai.ChatAdapter;
import com.example.pydroidide.ai.ChatMessage;
import com.example.pydroidide.python.PythonRunner;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 助手独立 Activity — 从编辑器跳转过来的 AI 辅助页面。
 * 根据传入的 action 类型（suggest/fix）预填充提示。
 */
public class AIAssistantActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText inputEditText;
    private ImageButton sendButton;
    private ImageButton clearButton;

    private final List<ChatMessage> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private String codeSnippet;
    private String errorInfo;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        // 获取 Intent 数据
        codeSnippet = getIntent().getStringExtra("code_snippet");
        errorInfo = getIntent().getStringExtra("error_info");
        action = getIntent().getStringExtra("action");

        initViews();
        handleAction();
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.rv_chat);
        inputEditText = findViewById(R.id.et_ai_input);
        sendButton = findViewById(R.id.btn_send);
        clearButton = findViewById(R.id.btn_clear_chat);

        if (chatRecyclerView == null) {
            finish();
            return;
        }

        adapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(adapter);

        if (sendButton != null) sendButton.setOnClickListener(v -> sendMessage());
        if (clearButton != null) clearButton.setOnClickListener(v -> {
            messages.clear();
            adapter.notifyDataSetChanged();
        });

        // 返回按钮（如果布局有 btn_menu）
        View backBtn = findViewById(R.id.btn_menu);
        if (backBtn == null) {
            // 可能用的是 activity_ai 布局，没有返回按钮，设置 ActionBar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void handleAction() {
        String prompt = null;

        if ("suggest".equals(action) && !TextUtils.isEmpty(codeSnippet)) {
            prompt = "请对以下 Python 代码给出改进建议和优化方案：\n\n```python\n" +
                    codeSnippet + "\n```";
            addMessage(prompt, true);
            addMessage("正在分析代码并生成建议...", false);

            mainHandler.postDelayed(() -> {
                // 模拟 AI 分析
                String analysis = analyzeCodeSnippet(codeSnippet);
                // 替换最后一条消息
                if (!messages.isEmpty()) {
                    messages.remove(messages.size() - 1);
                }
                addMessage(analysis, false);
            }, 1200);

        } else if ("fix".equals(action) && !TextUtils.isEmpty(errorInfo)) {
            prompt = "请帮我修复以下 Python 代码错误：\n\n```\n" + errorInfo + "\n```\n\n" +
                    "相关代码：\n```python\n" + codeSnippet + "\n```";
            addMessage(prompt, true);
            addMessage("正在分析错误...", false);

            mainHandler.postDelayed(() -> {
                String analysis = "错误分析：\n\n" + PythonRunner.analyzeError(errorInfo) +
                        "\n\n建议：请仔细检查代码中对应的位置，按照上述提示修改后重新运行。\n" +
                        "如果问题持续，可以尝试以下步骤：\n" +
                        "1. 检查变量是否正确定义\n" +
                        "2. 确认导入的模块是否已安装\n" +
                        "3. 验证缩进是否一致（建议使用 4 个空格）";
                if (!messages.isEmpty()) {
                    messages.remove(messages.size() - 1);
                }
                addMessage(analysis, false);
            }, 1200);

        } else {
            // 默认模式
            addMessage("👋 你好！我是 PyDroid AI 助手。\n请描述你的需求，我会尽力帮助你。", false);
        }
    }

    private String analyzeCodeSnippet(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 代码分析：\n\n");

        int lineCount = code.split("\n").length;
        sb.append("• 代码行数: ").append(lineCount).append("\n");

        if (code.contains("print")) {
            sb.append("• 包含输出语句 (print)\n");
        }
        if (code.contains("def ")) {
            sb.append("• 定义了函数\n");
        }
        if (code.contains("class ")) {
            sb.append("• 定义了类\n");
        }
        if (code.contains("import ")) {
            sb.append("• 包含模块导入\n");
        }
        if (code.contains("for ")) {
            sb.append("• 使用了循环 (for)\n");
        }
        if (code.contains("while ")) {
            sb.append("• 使用了循环 (while)\n");
        }
        if (code.contains("if ")) {
            sb.append("• 使用了条件判断 (if)\n");
        }

        sb.append("\n💡 通用建议：\n");
        sb.append("• 添加适当的注释以提高可读性\n");
        sb.append("• 使用有意义的变量名\n");
        sb.append("• 保持一致的缩进风格（4空格）\n");
        sb.append("• 考虑添加异常处理机制\n");

        return sb.toString();
    }

    private void sendMessage() {
        if (inputEditText == null) return;
        String text = inputEditText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        addMessage(text, true);
        inputEditText.setText("");

        // 模拟回复
        mainHandler.postDelayed(() -> {
            addMessage("收到你的问题，我会尽力帮你分析。请提供更多上下文信息以获得更准确的回答。", false);
        }, 800);
    }

    private void addMessage(String content, boolean isUser) {
        messages.add(new ChatMessage(content, isUser));
        adapter.notifyItemInserted(messages.size() - 1);
        chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}