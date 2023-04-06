
package com.ljk;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.ljk.handler.ResultHandler;
import com.ljk.handler.ResultPrintHandler;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.reactivex.Flowable;
import org.apache.commons.lang.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 插件主界面
 */
public class MyToolWindowFactory implements ToolWindowFactory {

    private static JTextArea questionTextArea;
    private static RSyntaxTextArea answerTextArea;
    private static JButton sendButton;
    private static JButton clearTokenButton;
    private final static Lock lock = new ReentrantLock();


    public static RSyntaxTextArea getAnswerTextArea() {
        return answerTextArea;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel content = new JPanel(new GridBagLayout());
        //组件初始化
        initializeUIComponents(content, project);
        //添加发送按钮的事件监听器
        sendButton.addActionListener(e -> {
            String question = questionTextArea.getText();
            new Thread(() -> {
                ResultHandler resultHandler = new ResultPrintHandler(answerTextArea);
                String answerText = requestApi(question, false, resultHandler);
                OpenAiApiUtil.addAssistantMsg(answerText);
            }).start();
        });
        //添加清理按钮的事件监听器
        clearTokenButton.addActionListener(e -> {
            //清除会话记录
            OpenAiApiUtil.messagesListInit();
            //清除文本框
            clearAnswerTextArea();
        });
        toolWindow.getContentManager().addContent(
                ContentFactory.SERVICE.getInstance().createContent(content, "", false)
        );
    }

    public static String requestApi(String question, Boolean isShortcuts, ResultHandler resultHandler) {
        if (isShortcuts) {
            appendAskText("user:" + question + ("\n"));
        }
        return requestApi(question, resultHandler);
    }

    /**
     * 执行请求
     * @param question 问题
     * @param resultHandler 结果处理器
     * @return 处理结果文本
     */
    public static String requestApi(String question, ResultHandler resultHandler) {
        if (!lock.tryLock()) {
            appendAnswerText("正在处理....\n");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(question);
            chatMessage.setRole("user");
            appendAskText(chatMessage.getRole() + ":" + (chatMessage.getContent()) + ("\n"));
            Flowable<ChatCompletionChunk> flowable = OpenAiApiUtil.getAnswerByChat35(chatMessage);
            resultHandler.doHandle(flowable);
            sb = resultHandler.getResultText();

        } catch (Exception ex) {
            appendAnswerText(ex.getLocalizedMessage());
        } finally {
            lock.unlock();
        }
        return sb.toString();
    }


    public static void requestApi(String question) {
        if (!lock.tryLock()) {
            answerTextArea.setText("正在处理....\n");
            return;
        }
        try {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(question);
            chatMessage.setRole("user");
            appendAskText(chatMessage.getRole() + ":" + (chatMessage.getContent()) + ("\n"));

            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return OpenAiApiAsyncUtil.getAnswerByChat35(chatMessage);
                } catch (Exception ex) {
                    return ex.getLocalizedMessage();
                }
            });

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    appendAnswerText(ex.getLocalizedMessage());
                } else {
                    appendAnswerText(result);
                }
            });

        } catch (Exception ex) {
            appendAnswerText(ex.getLocalizedMessage());
        } finally {
            lock.unlock();
        }

    }

    public static void print(String message) {
        if (StringUtils.isEmpty(message))
            return;
        answerTextArea.setText(message);
    }

    public static void appendAskText(String text) {
        if (StringUtils.isEmpty(text))
            return;
        SwingUtilities.invokeLater(() -> questionTextArea.append(text));
    }

    public static void appendAnswerText(String text) {
        if (StringUtils.isEmpty(text))
            return;
        SwingUtilities.invokeLater(() -> answerTextArea.append(text));
    }

    public static void clearAnswerTextArea() {
        SwingUtilities.invokeLater(() -> {
            answerTextArea.setText("");
            questionTextArea.setText("");
        });
    }

    public static void appendAnswerText(String text, Project project) {
        answerTextArea.append(text);
    }

    /**
     * 初始化UI组件
     * @param content
     * @param project
     */
    private void initializeUIComponents(JPanel content, Project project) {
        GridBagConstraints gbcQuestion = new GridBagConstraints();
        gbcQuestion.gridx = 0;
        gbcQuestion.gridy = 0;
        gbcQuestion.weightx = 0.5;
        gbcQuestion.weighty = 1;
        gbcQuestion.fill = GridBagConstraints.BOTH;
        gbcQuestion.insets = new Insets(10, 10, 10, 0);
        gbcQuestion.gridheight = 20;
        gbcQuestion.gridwidth = 1;
        questionTextArea = new JTextArea();
        JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
        content.add(questionScrollPane, gbcQuestion);

        GridBagConstraints askButton = new GridBagConstraints();
        askButton.gridx = 1;
        askButton.gridy = 0;
        askButton.weightx = 0;
        askButton.weighty = 0;
        askButton.gridheight = 2;
        askButton.gridwidth = 1;
        askButton.fill = GridBagConstraints.BOTH;
        askButton.anchor = GridBagConstraints.CENTER;
        askButton.insets = new Insets(10, 10, 0, 10);
        sendButton = new JButton("Ask");
        content.add(sendButton, askButton);

        GridBagConstraints cleanButton = new GridBagConstraints();
        cleanButton.gridx = 1;
        cleanButton.gridy = 4;
        cleanButton.weightx = 0;
        cleanButton.weighty = 0;
        cleanButton.gridheight = 2;
        cleanButton.gridwidth = 1;
        cleanButton.fill = GridBagConstraints.BOTH;
        cleanButton.anchor = GridBagConstraints.CENTER;
        cleanButton.insets = new Insets(0, 10, 10, 10);
        clearTokenButton = new JButton("Clean");
        content.add(clearTokenButton, cleanButton);

        GridBagConstraints gbcAnswer = new GridBagConstraints();
        gbcAnswer.gridx = 2;
        gbcAnswer.gridy = 0;
        gbcAnswer.weightx = 1;
        gbcAnswer.weighty = 1;
        gbcAnswer.fill = GridBagConstraints.BOTH;
        gbcAnswer.gridheight = 20;
        gbcAnswer.gridwidth = 1;
        gbcAnswer.insets = new Insets(10, 10, 10, 10);
        answerTextArea = new RSyntaxTextArea(20, 60);
        answerTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        answerTextArea.setCodeFoldingEnabled(true);

        EditorColorsManager colorsManager = EditorColorsManager.getInstance();
        EditorColorsScheme scheme = colorsManager.getGlobalScheme();
        EditorFontType fontType = EditorFontType.PLAIN;

        // 获取当前编辑器使用的Java文件编辑框的字体和字体大小
        Font editorFont = scheme.getFont(fontType);
        int editorFontSize = scheme.getEditorFontSize();

        // 获取当前编辑器使用的Java文件编辑框的背景颜色和文本颜色
        Color editorBackgroundColor = scheme.getDefaultBackground();
        Color editorForegroundColor = scheme.getDefaultForeground();
        // 设置背景颜色和文本颜色

        answerTextArea.setBackground(editorBackgroundColor);
        answerTextArea.setForeground(editorForegroundColor);
        // 设置字体和字体大小
        //  answerTextArea.setFont(editorFont.deriveFont((float)editorFontSize));
        // 设置代码折叠
        answerTextArea.setCodeFoldingEnabled(true);
        JScrollPane answerScrollPane = new JScrollPane(answerTextArea);
        answerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JScrollBar verticalBar = answerScrollPane.getVerticalScrollBar();
        verticalBar.setValue(verticalBar.getMaximum());
        DefaultCaret caret = (DefaultCaret) answerTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        content.add(answerScrollPane, gbcAnswer);
    }

}
