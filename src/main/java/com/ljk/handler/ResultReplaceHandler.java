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

        //完整的语句
        final StringBuilder wholeSb = new StringBuilder();
        //文本输出标志位
        AtomicReference<Integer> flag = new AtomicReference<>(0);
        flowable.blockingForEach(m -> {
            for (ChatCompletionChoice choice : m.getChoices()) {
                //当前是否完成的词句
                boolean wholeCode = false;
                String content = choice.getMessage().getContent();
                if (StringUtils.isEmpty(content)) {
                    return;
                }
                String answerText = content;
                if(content.contains("\n")){
                    wholeCode = true;
                }
                wholeSb.append(content);
                MyToolWindowFactory.appendAnswerText(answerText);
                if(wholeCode){
                    //如果是完整的一句，就情况，并且判断是否为```,如果不是才输出
                   String wholeStr =  wholeSb.toString();
                   wholeSb.delete(0,wholeSb.length());
                   wholeStr = appendStr(flag,wholeStr);
                   appendText(wholeStr);

                }
            }
        });
        appendText("\n");
    }

    /**
     *
     * @param flag 0 开始；1 注释开始 2代码开始
     * @param wholeStr
     * @return
     */
    private String appendStr(AtomicReference<Integer> flag,String wholeStr){
        String str = null;
        switch (flag.get()){
            case 0:
                if(wholeStr.trim().equals("```") || wholeStr.trim().equals("```\n")){
                    //一开始就是代码块，
                    str = null;
                    flag.set(2);
                }else {
                    //非代码，注释准备
                    str ="/**";
                    flag.set(1);
                }
                break;
            case 1:
                if(wholeStr.trim().equals("```") || wholeStr.trim().equals("```\n")){
                    //结束注释，代码块开始；原文本增加 **/
                    str = wholeStr + "**/";
                    flag.set(0);
                }else {
                    //注释继续，返回原文本
                    str = wholeStr;
                }
            case 2:
                if(wholeStr.trim().equals("```") || wholeStr.trim().equals("```\n")){
                    //代码结束，重置
                    str = null;
                    flag.set(0);
                }else {
                    //代码继续，返回原文本
                    str = wholeStr;
                }
            default:
                break;
        }
        return str;
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
