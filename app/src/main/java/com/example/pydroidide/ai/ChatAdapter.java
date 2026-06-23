package com.example.pydroidide.ai;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pydroidide.R;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 聊天消息 RecyclerView 适配器。
 * 支持用户消息（右对齐、绿色背景）和 AI 消息（左对齐、暗色背景），
 * 并对代码块（```...```）做简单的 Markdown 风格着色。
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final List<ChatMessage> messages;
    private static final Pattern CODE_BLOCK = Pattern.compile("```(\\w*)\\n([\\s\\S]*?)```");

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        holder.bind(msg);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRole;
        private final TextView tvMessage;
        private final TextView tvCodeBlock;
        private final CardView cardMessage;
        private final LinearLayout rootLayout;

        MessageViewHolder(View itemView) {
            super(itemView);
            tvRole = itemView.findViewById(R.id.tv_role);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvCodeBlock = itemView.findViewById(R.id.tv_code_block);
            cardMessage = itemView.findViewById(R.id.card_message);
            rootLayout = (LinearLayout) itemView;
        }

        void bind(ChatMessage msg) {
            if (msg.isUser()) {
                tvRole.setText("你");
                tvRole.setTextColor(0xFF00D4AA);
                cardMessage.setCardBackgroundColor(0xFF1A3A2A);
                rootLayout.setGravity(Gravity.END);
            } else {
                tvRole.setText("AI");
                tvRole.setTextColor(0xFF7B68EE);
                cardMessage.setCardBackgroundColor(0xFF16163A);
                rootLayout.setGravity(Gravity.START);
            }

            String content = msg.getContent();
            // 提取代码块
            Matcher m = CODE_BLOCK.matcher(content);
            if (m.find()) {
                // 分离文本和代码
                int codeStart = m.start();
                int codeEnd = m.end();
                String beforeCode = content.substring(0, codeStart).trim();
                String codeContent = m.group(2).trim();

                if (!beforeCode.isEmpty()) {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(beforeCode);
                } else {
                    tvMessage.setVisibility(View.GONE);
                }

                tvCodeBlock.setVisibility(View.VISIBLE);
                tvCodeBlock.setText(codeContent);
            } else {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(formatContent(content));
                tvCodeBlock.setVisibility(View.GONE);
            }
        }

        private CharSequence formatContent(String raw) {
            SpannableString spannable = new SpannableString(raw);

            // 对 `` 内的行内代码着色
            Pattern inlineCode = Pattern.compile("`([^`]+)`");
            Matcher m = inlineCode.matcher(raw);
            while (m.find()) {
                spannable.setSpan(
                    new ForegroundColorSpan(Color.rgb(86, 156, 214)),
                    m.start(), m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
            return spannable;
        }
    }
}