package com.example.pydroidide.editor;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Python 语法高亮器。
 * 对 CodeEditor（EditText 子类）的文本进行实时着色。
 *
 * 重要：调用方应使用 isHighlighting 标志防止递归触发。
 * 本类内部也会检测并跳过递归调用。
 */
public class PythonSyntaxHighlighter {

    // 颜色常量（RGB 值 → int）
    private static final int COLOR_KEYWORD = Color.rgb(86, 156, 214);   // 关键字 - 蓝色
    private static final int COLOR_STRING  = Color.rgb(206, 145, 120);  // 字符串 - 橙色
    private static final int COLOR_COMMENT = Color.rgb(106, 153, 85);   // 注释 - 绿色
    private static final int COLOR_NUMBER  = Color.rgb(181, 206, 168);  // 数字 - 浅绿
    private static final int COLOR_DECORATOR = Color.rgb(218, 218, 86); // 装饰器 - 黄色
    private static final int COLOR_BUILTIN = Color.rgb(86, 156, 214);   // 内置函数 - 蓝色

    // Python 关键字
    private static final String KEYWORDS =
        "\\b(False|None|True|and|as|assert|async|await|break|class|continue|" +
        "def|del|elif|else|except|finally|for|from|global|if|import|in|is|" +
        "lambda|nonlocal|not|or|pass|raise|return|try|while|with|yield)\\b";

    // 内置函数
    private static final String BUILTINS =
        "\\b(print|len|range|int|str|float|list|dict|set|tuple|bool|type|" +
        "input|open|enumerate|zip|map|filter|sorted|reversed|abs|max|min|" +
        "sum|round|isinstance|hasattr|getattr|setattr|super|dir|vars)\\b";

    // 数字
    private static final String NUMBER = "\\b\\d+\\.?\\d*\\b";

    // 字符串 - 匹配单/双引号字符串（简化版，不处理转义引号）
    private static final String STRING =
        "(\"(?:[^\"\\\\]|\\\\.)*\")|('(?:[^'\\\\]|\\\\.)*')";

    // 注释
    private static final String COMMENT = "#[^\n]*";

    // 装饰器
    private static final String DECORATOR = "@\\w+";

    // 复合模式（按照优先级从低到高排列）
    private static final Pattern[] PATTERNS = new Pattern[]{
        Pattern.compile(COMMENT),
        Pattern.compile(STRING),
        Pattern.compile(DECORATOR),
        Pattern.compile(KEYWORDS),
        Pattern.compile(BUILTINS),
        Pattern.compile(NUMBER),
    };

    private static final int[] COLORS = new int[]{
        COLOR_COMMENT,
        COLOR_STRING,
        COLOR_DECORATOR,
        COLOR_KEYWORD,
        COLOR_BUILTIN,
        COLOR_NUMBER,
    };

    /** 递归保护标记 */
    private static boolean inHighlight = false;

    /**
     * 对 EditText 的文本进行语法高亮。
     * 内置递归保护：如果已经在高亮过程中，跳过本次调用。
     */
    public static void highlight(android.widget.EditText editor) {
        // 递归保护
        if (inHighlight) return;
        inHighlight = true;
        try {
            doHighlight(editor);
        } finally {
            inHighlight = false;
        }
    }

    private static void doHighlight(android.widget.EditText editor) {
        Editable editable = editor.getText();
        if (editable == null || editable.length() == 0) return;

        // 先清除所有前景色 span
        ForegroundColorSpan[] spans = editable.getSpans(0, editable.length(), ForegroundColorSpan.class);
        for (ForegroundColorSpan span : spans) {
            editable.removeSpan(span);
        }

        String text = editable.toString();

        // 对每种模式进行匹配着色
        for (int i = 0; i < PATTERNS.length; i++) {
            Matcher m = PATTERNS[i].matcher(text);
            int color = COLORS[i];
            while (m.find()) {
                // 检查该位置是否已被更高优先级的 span 覆盖
                ForegroundColorSpan[] existing = editable.getSpans(m.start(), m.end(), ForegroundColorSpan.class);
                if (existing.length == 0) {
                    editable.setSpan(
                        new ForegroundColorSpan(color),
                        m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
            }
        }
    }
}
