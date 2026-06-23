package com.example.pydroidide.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;
import android.view.View;

/**
 * 行号显示 View。
 * 与 CodeEditor 配合使用，显示每一行的行号。
 */
public class LineNumberView extends View {

    private Paint paint;
    private int textColor = 0xFF999999;
    private float textSize = 36f;
    private int lineCount = 1;
    private float lineHeight = 0;
    private int paddingTop = 8;
    private int paddingRight = 8;
    private int paddingLeft = 8;
    private EditText editor;

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
        lineHeight = textSize + 8f;
    }

    /**
     * 关联编辑器，用于同步行号。
     */
    public void setEditor(EditText editor) {
        this.editor = editor;
    }

    /**
     * 设置行数并重绘。
     */
    public void setLineCount(int count) {
        this.lineCount = Math.max(1, count);
        requestLayout();
        invalidate();
    }

    /**
     * 从关联的编辑器获取行数并更新。
     */
    public void updateLineCount() {
        if (editor != null && editor.getText() != null) {
            int count = 1;
            CharSequence text = editor.getText();
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == '\n') count++;
            }
            setLineCount(count);
        }
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
