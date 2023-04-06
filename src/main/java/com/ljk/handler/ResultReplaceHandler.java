package com.ljk.handler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.ljk.MyToolWindowFactory;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import io.reactivex.Flowable;
import org.apache.commons.lang.StringUtils;
import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 结果替换并打印处理
 * @Author: liujiankun
 * @Date: 2023/4/2 15:17
 */
public class ResultReplaceHandler implements ResultHandler{

    private Editor sourceEditor;

    private Document document ;
    /**
     * 开始位置
     */
    private int startOffset;
    /**
     * 结束位置
     */
    private int endOffset;

    private StringBuilder sb = new StringBuilder();

    public StringBuilder getResultText() {
        return sb;
    }


    public ResultReplaceHandler(Editor sourceEditor, int startOffset, int endOffset) {
        this.sourceEditor = sourceEditor;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        document = sourceEditor.getDocument();
    }

    /**
     * 删除内容
     */
    private void deleteText(){
        SwingUtilities.invokeLater(() -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                WriteCommandAction.runWriteCommandAction(sourceEditor.getProject(), () -> {
                    document.deleteString(startOffset, endOffset);
                });
            }, ModalityState.NON_MODAL);
        });





    }
    private void appendText(Flowable<ChatCompletionChunk> flowable) {
        flowable.doOnError(e -> {
            SwingUtilities.invokeLater(() -> {
                ApplicationManager.getApplication().invokeLater(() -> {
                    WriteCommandAction.runWriteCommandAction(sourceEditor.getProject(), () -> {
                        document.insertString(startOffset, e.getLocalizedMessage());
                    });
                }, ModalityState.NON_MODAL);
            });
        });

        final AtomicReference<Integer> codeSwitch = new AtomicReference<>(0);
        flowable.blockingForEach(m -> {
            for (ChatCompletionChoice choice : m.getChoices()) {
                String content = choice.getMessage().getContent();
                if (StringUtils.isEmpty(content)) {
                    return;
                }
                String answerText = content;
                MyToolWindowFactory.appendAnswerText(answerText);
                // 0 初始化状态，1注释临界，2注释结尾，3Java代码输出中
                switch (codeSwitch.get()) {
                    case 0:
                        // 注释代码中
                        codeSwitch.set(1);
                        if (!"```".equals(content)) {
                            content = "/**" + content;
                        }else {
                            content = "/**";
                        }
                        break;
                    case 1:
                        if ("```".equals(content)) {
                            codeSwitch.set(2);
                        }
                        if ("java".equals(content)) {
                            codeSwitch.set(3);
                            content = "";
                        }
                        break;
                    case 2:
                        if ("java".equals(content)) {
                            codeSwitch.set(3);
                            content = "";
                        }
                        break;
                    case 3:
                        if ("```".equals(content) || "```\n".equals(content)) {
                            codeSwitch.set(1);
                            content = "/**";
                        }
                        break;
                    default:
                        break;
                }
                // 写入文本
                if(3 == codeSwitch.get()){
                    appendText(content);
                }

            }
        });
    }

    private void appendText(String content) {
        if (StringUtils.isEmpty(content)) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                WriteCommandAction.runWriteCommandAction(sourceEditor.getProject(), () -> {
                    document.insertString(startOffset, content);
                    startOffset += content.length();
                });
            }, ModalityState.NON_MODAL);
        });

    }


    /**
     * 替换内容
     */
    public void doHandle(Flowable<ChatCompletionChunk> flowable){
        deleteText();
        appendText(flowable);
    }

}
