package com.ljk.handler;

import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import io.reactivex.Flowable;

/**
 * 处理api结果
 * @Author: liujiankun
 * @Date: 2023/4/2 15:26
 */
public interface ResultHandler {
    /**
     * 处理
     * @param flowable 由api接口返回的封装流对象
     */
    void doHandle(Flowable<ChatCompletionChunk> flowable);

    /**
     * 获取结果文本
     * @return
     */
    StringBuilder getResultText();
}
