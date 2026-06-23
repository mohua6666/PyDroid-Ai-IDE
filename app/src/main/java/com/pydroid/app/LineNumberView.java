package com.pydroid.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 行号显示 View。
 * 与 CodeEditorView 配合使用，显示每一行的行号。
 */
public class LineNumberView extends View {

    private Paint paint;
    private int textColor = 0xFF999999;
    private float textSize = 36f; // px at 13sp ~ 36px on mdpi
    private int lineCount = 1;
    private float lineHeight = 0;
    private int paddingTop = 8;
    private int paddingRight = 8;
    private int paddingLeft = 8;

    public LineNumberView(Context context) {
        super(context);
        init();
    }

    public LineNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.RIGHT);
        setBackgroundColor(0xFF1E1E3A);

        // 默认行高与 13sp 文本匹配
        lineHeight = textSize + 8f; // 行间距
    }

    public void setLineCount(int count) {
        this.lineCount = Math.max(1, count);
        invalidate();
    }

    /**
     * 关联编辑器，用于同步行号（EditorFragment 会调用此方法）。
     */
    public void setEditor(CodeEditorView editor) {
        // 目前仅需要行数同步，由 EditorFragment 在 TextWatcher 中调用 setLineCount()
        // 保留此方法以满足接口兼容
    }

    public void setTextColor(int color) {
        this.textColor = color;
        paint.setColor(color);
        invalidate();
    }

    public void setTextSizePx(float px) {
        this.textSize = px;
        paint.setTextSize(px);
        this.lineHeight = px + 8f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lineCount <= 0) return;

        float y = paddingTop + lineHeight;
        for (int i = 1; i <= lineCount; i++) {
            canvas.drawText(
                String.valueOf(i),
                getWidth() - paddingRight,
                y,
                paint
            );
            y += lineHeight;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveSize((int) (paddingLeft + paddingRight + paint.measureText("9999")), widthMeasureSpec);
        int minHeight = (int) (paddingTop * 2 + lineHeight * lineCount);
        int height = resolveSize(Math.max(minHeight, MeasureSpec.getSize(heightMeasureSpec)), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}