package com.ljk.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowManager;
import com.ljk.MyToolWindowFactory;

/**
 * @Author: liujiankun
 * @Date: 2023/3/14 10:48
 */
public class ToolAction extends AnAction {
     @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        //Messages.showMessageDialog(project, "say hello world ~", "Info", Messages.getInformationIcon());
        myMethod(project);
     }

    public void myMethod(Project project) {
        // 打开自定义控制台
        ToolWindowManager.getInstance(project).getToolWindow("JavaHelper").activate(null);
        // 获取文本域
        MyToolWindowFactory.print("欢迎使用！\n");

    }
}
