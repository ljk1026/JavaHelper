package com.ljk.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.ljk.MyToolWindowFactory;
import com.ljk.OpenAiApiUtil;
import com.ljk.handler.ResultHandler;
import com.ljk.handler.ResultPrintHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 代码优化普通模式
 * 通过选中的代码进行请求chatGPT进行输出优化结果
 * @Author: liujiankun
 * @Date: 2023/3/14 12:19
 */
public class CodeOptimizationAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        // 获取当前编辑器中选中的文本
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            return;
        }
        final String selectedText = "优化以下代码，输出java代码，代码开头和结尾加上```   \n" + editor.getSelectionModel().getSelectedText();
        if (selectedText == null) {
            return;
        }
        new Thread(() -> {
            //代码解析初始化messagesList
            OpenAiApiUtil.messagesListInit();
            ResultHandler resultHandler = new ResultPrintHandler(MyToolWindowFactory.getAnswerTextArea());
            MyToolWindowFactory.requestApi(selectedText, true,resultHandler);
        }).start();

    }


}
