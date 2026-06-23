package com.example.pydroidide.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pydroidide.R;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 项目列表 RecyclerView 适配器。
 * 用于在主页面展示 PyDroidProjects 工作区中的项目。
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private List<File> projects;
    private final OnProjectClickListener clickListener;
    private final OnProjectLongClickListener longClickListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public interface OnProjectClickListener {
        void onClick(File project);
    }
    public interface OnProjectLongClickListener {
        void onLongClick(File project);
    }

    public ProjectAdapter(List<File> projects,
                          OnProjectClickListener clickListener,
                          OnProjectLongClickListener longClickListener) {
        this.projects = projects;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void updateProjects(List<File> newProjects) {
        this.projects = newProjects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File project = projects.get(position);
        holder.nameText.setText(project.getName());
        holder.pathText.setText(project.getAbsolutePath());
        holder.dateText.setText(dateFormat.format(new Date(project.lastModified())));

        // 统计 .py 文件数量
        File[] pyFiles = project.listFiles((d, name) -> name.endsWith(".py"));
        int count = pyFiles != null ? pyFiles.length : 0;
        holder.infoText.setText(count + " 个 Python 文件");

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onClick(project);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(project);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return projects != null ? projects.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, pathText, dateText, infoText;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.projectName);
            pathText = itemView.findViewById(R.id.projectPath);
            dateText = itemView.findViewById(R.id.projectDate);
            infoText = itemView.findViewById(R.id.projectInfo);
        }
    }
}
