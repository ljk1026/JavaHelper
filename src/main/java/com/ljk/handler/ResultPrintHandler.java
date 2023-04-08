package com.ljk.handler;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import io.reactivex.Flowable;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;

/**
 * 结果打印处理
 * @Author: liujiankun
 * @Date: 2023/4/2 15:17
 */
public class ResultPrintHandler implements ResultHandler{
    /**
     * 自定义的组件
     */
    private RSyntaxTextArea customTextArea;

    public ResultPrintHandler(RSyntaxTextArea customTextArea) {
        this.customTextArea = customTextArea;
    }

    private StringBuilder sb = new StringBuilder();

    public StringBuilder getResultText() {
        return sb;
    }

    @Override
    public void doHandle(Flowable<ChatCompletionChunk> flowable) {
        flowable.doOnError(e -> {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    customTextArea.append(e.getLocalizedMessage());
                }
            });
        }).blockingForEach(m -> {
            for (ChatCompletionChoice choice : m.getChoices()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        customTextArea.append(choice.getMessage().getContent());
                    }
                });
                sb.append(choice.getMessage().getContent());
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                customTextArea.append("\n");
            }
        });
    }

}
