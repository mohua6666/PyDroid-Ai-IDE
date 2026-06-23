package com.example.pydroidide.utils;

import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 项目管理器：管理工作区目录、创建/删除/列出项目。
 */
public class ProjectManager {

    private static final String WORKSPACE_DIR = "PyDroidProjects";

    /**
     * 无参构造（供 com.pydroid.app 使用）。
     */
    public ProjectManager() {}

    /**
     * 带 Context 的构造（供 com.example.pydroidide 新版使用）。
     */
    public ProjectManager(android.content.Context context) {
        // Context 预留，供后续功能扩展（如 SharedPreferences）
    }

    public File getWorkspaceDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), WORKSPACE_DIR);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public List<File> getProjects() {
        File workspace = getWorkspaceDir();
        File[] dirs = workspace.listFiles(File::isDirectory);
        if (dirs == null) return Collections.emptyList();
        List<File> list = new ArrayList<>();
        for (File d : dirs) list.add(d);
        Collections.sort(list, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        return list;
    }

    public File createProject(String name) {
        File proj = new File(getWorkspaceDir(), name);
        if (!proj.exists()) proj.mkdirs();
        // 创建默认 main.py
        File mainPy = new File(proj, "main.py");
        FileManager fm = new FileManager();
        fm.createDefaultMainPy(mainPy);
        return proj;
    }

    public void deleteProject(File project) {
        new FileManager().deleteRecursive(project);
    }
}