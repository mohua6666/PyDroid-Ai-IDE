package com.example.pydroidide;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pydroidide.ai.ChatAdapter;
import com.example.pydroidide.ai.ChatMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI 助手 Activity — 代码生成、解释、调试建议。
 * 使用 activity_ai.xml 布局，兼容现有 ChatAdapter。
 */
public class AIAssistantActivity extends AppCompatActivity {

    private RecyclerView chatRecycler;
    private EditText inputEditText;
    private ImageButton sendButton;
    private ImageButton clearButton;

    private final List<ChatMessage> messages = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private final ExecutorService aiExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private String codeSnippet = "";
    private String errorInfo = "";
    private String action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        codeSnippet = getIntent().getStringExtra("code_snippet");
        if (codeSnippet == null) codeSnippet = "";
        errorInfo = getIntent().getStringExtra("error_info");
        if (errorInfo == null) errorInfo = "";
        action = getIntent().getStringExtra("action");
        if (action == null) action = "";

        initViews();
        setupChat();
        handleAction();
    }

    private void initViews() {
        chatRecycler = findViewById(R.id.rv_chat);
        inputEditText = findViewById(R.id.et_ai_input);
        sendButton = findViewById(R.id.btn_send);
        clearButton = findViewById(R.id.btn_clear_chat);

        if (chatRecycler == null) {
            finish();
            return;
        }
    }

    private void setupChat() {
        chatAdapter = new ChatAdapter(messages);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(chatAdapter);

        if (sendButton != null) sendButton.setOnClickListener(v -> sendMessage());

        if (clearButton != null) clearButton.setOnClickListener(v -> {
            messages.clear();
            chatAdapter.notifyDataSetChanged();
        });

        if (inputEditText != null) inputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void handleAction() {
        String prompt = null;

        if ("suggest".equals(action) && !codeSnippet.isEmpty()) {
            prompt = "请对以下 Python 代码给出改进建议和优化方案：\n\n```python\n" +
                    codeSnippet + "\n```";
            addMessage(prompt, true);

            mainHandler.postDelayed(() -> {
                String analysis = analyzeCodeSnippet(codeSnippet);
                addMessage(analysis, false);
            }, 1200);

        } else if ("fix".equals(action) && !errorInfo.isEmpty()) {
            prompt = "请帮我修复以下 Python 代码错误：\n\n```\n" + errorInfo + "\n```\n\n" +
                    "相关代码：\n```python\n" + codeSnippet + "\n```";
            addMessage(prompt, true);

            mainHandler.postDelayed(() -> {
                String analysis = "🔍 错误分析：\n\n" +
                        com.example.pydroidide.python.PythonRunner.analyzeError(errorInfo) +
                        "\n\n💡 建议：\n1. 仔细检查错误提示中的行号\n" +
                        "2. 确认变量是否正确定义\n3. 验证缩进是否一致（建议4空格）";
                addMessage(analysis, false);
            }, 1200);

        } else {
            StringBuilder welcome = new StringBuilder();
            welcome.append("👋 你好！我是 PyDroid AI 助手。\n\n");
            welcome.append("我可以帮你：\n");
            welcome.append("• 📖 解释 Python 代码\n");
            welcome.append("• ⚡ 优化你的脚本\n");
            welcome.append("• 🐛 分析错误和调试\n");
            welcome.append("• ✨ 生成 Python 代码\n\n");
            welcome.append("请描述你的需求吧！");
            addMessage(welcome.toString(), false);
        }
    }

    private void sendMessage() {
        if (inputEditText == null) return;
        String text = inputEditText.getText().toString().trim();
        if (text.isEmpty()) return;

        addMessage(text, true);
        inputEditText.setText("");

        aiExecutor.execute(() -> {
            String response = generateAIResponse(text);
            mainHandler.post(() -> addMessage(response, false));
        });
    }

    private void addMessage(String content, boolean isUser) {
        messages.add(new ChatMessage(content, isUser));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatRecycler.smoothScrollToPosition(messages.size() - 1);
    }

    private String analyzeCodeSnippet(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 代码分析：\n\n");

        int lineCount = code.split("\n").length;
        sb.append("• 代码行数: ").append(lineCount).append("\n");

        if (code.contains("print")) sb.append("• 包含输出语句 (print)\n");
        if (code.contains("def ")) sb.append("• 定义了函数\n");
        if (code.contains("class ")) sb.append("• 定义了类\n");
        if (code.contains("import ")) sb.append("• 包含模块导入\n");
        if (code.contains("for ")) sb.append("• 使用了循环 (for)\n");
        if (code.contains("while ")) sb.append("• 使用了循环 (while)\n");
        if (code.contains("if ")) sb.append("• 使用了条件判断 (if)\n");

        sb.append("\n💡 通用建议：\n");
        sb.append("• 添加适当的注释以提高可读性\n");
        sb.append("• 使用有意义的变量名\n");
        sb.append("• 保持一致的缩进风格（4空格）\n");
        sb.append("• 考虑添加异常处理机制\n");

        return sb.toString();
    }

    private String generateAIResponse(String query) {
        String lower = query.toLowerCase();

        if (lower.contains("错误") || lower.contains("error") || lower.contains("debug") ||
            lower.contains("报错") || lower.contains("异常") || lower.contains("修复")) {
            return "🔍 关于错误排查，请提供完整的错误信息。\n\n" +
                   "常见 Python 错误及解决方案：\n" +
                   "• `SyntaxError` — 检查冒号、括号匹配\n" +
                   "• `NameError` — 检查变量名是否正确\n" +
                   "• `TypeError` — 检查操作是否支持该类型\n" +
                   "• `IndentationError` — 保持缩进一致";
        }

        if (lower.contains("生成") || lower.contains("写") || lower.contains("创建")) {
            return "根据你的需求，这里是一个代码模板：\n\n```python\n" +
                   "def main():\n    # 在这里编写你的代码\n    print('Hello!')\n\n" +
                   "if __name__ == '__main__':\n    main()\n```\n\n" +
                   "请告诉我更具体的功能需求！";
        }

        if (lower.contains("print") || lower.contains("输出")) {
            return "`print()` 是 Python 中最基础的输出函数。\n\n```python\n" +
                   "print('Hello World')\nprint(f'变量值: {value}')\n```";
        }

        if (lower.contains("for") || lower.contains("循环")) {
            return "Python for 循环示例：\n\n```python\nfor item in my_list:\n    " +
                   "print(item)\n\nfor i in range(10):\n    print(i)\n```";
        }

        if (lower.contains("你好") || lower.contains("hello") || lower.contains("hi")) {
            return "你好！有什么 Python 相关的问题我可以帮你解答？";
        }

        return "感谢你的提问！请提供更多上下文信息，我来帮你更准确地分析。\n\n" +
               "你可以：\n• 粘贴代码让我分析\n• 描述你遇到的问题\n• 告诉我你想实现的功能";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aiExecutor.shutdownNow();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
