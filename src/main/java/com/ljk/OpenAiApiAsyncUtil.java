package com.ljk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljk.common.SettingConstant;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.theokanning.openai.service.OpenAiService.defaultRetrofit;

/**
 * openai异步接口
 */
@Deprecated
public class OpenAiApiAsyncUtil  {
    public static boolean isRun = false;


    /**
     * 消息记录
     */
    private static List<ChatMessage> messagesList = new ArrayList<>(); ;
    static {
        ChatMessage defaultMsg = new ChatMessage();
        defaultMsg.setRole("system");
        defaultMsg.setContent("你是一个Java开发工程师");
        messagesList.add(defaultMsg);
    }

    public static String getAnswerByChat35(ChatMessage chatMessage){
        ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
        OkHttpClient client = OpenAiService.defaultClient(SettingConstant.TOKEN, Duration.ofSeconds(600))
                .newBuilder()
                .proxy(proxy)
                .build();
        Retrofit retrofit = defaultRetrofit(client, mapper);
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        OpenAiService service = new OpenAiService(api);
        messagesList.add(chatMessage);
        StringBuilder sb = new StringBuilder();
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messagesList)
                .maxTokens(2048)
                .temperature(0D)
              //  .stream(true)
                // .topP(1D)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .build();
        try{
            ChatCompletionResult completion = service.createChatCompletion(request);
            for (ChatCompletionChoice choice : completion.getChoices()) {
                messagesList.add(choice.getMessage());
                sb.append(choice.getMessage().getRole()).append(":\n").append(choice.getMessage().getContent()).append("\n");
            }


        }catch (Exception e){
            sb.append("出现错误，请稍候再试；\n");
            sb.append(e.getMessage() + "\n");
        }

        return sb.toString();
    }



}
