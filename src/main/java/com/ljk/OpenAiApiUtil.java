package com.ljk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljk.common.SettingConstant;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.theokanning.openai.service.OpenAiService.defaultRetrofit;

/**
 * openAi接口请求工具
 */
public class OpenAiApiUtil {

    public static String getAnswerByChat3(String question){
        StringBuilder sb = new StringBuilder();
        OpenAiService service = new OpenAiService(SettingConstant.TOKEN,Duration.ofSeconds(100));
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .prompt(question)
                .maxTokens(4000)
                .temperature(0D)
               // .topP(1D)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .stop(Arrays.asList("\n","\n","\n","\n"))
                .n(1)
                .echo(true)
                .build();
        try{
            CompletionResult completion = service.createCompletion(completionRequest);
            for (CompletionChoice choice : completion.getChoices()) {
                sb.append( choice.getText()).append("\n");
                System.out.print(choice.getText());
                System.out.print(choice);
            }
        }catch (Exception e){
            sb.append("出现错误，请稍候再试；\n");
            sb.append(e.getMessage());
        }
        return sb.toString();
    }


    /**
     * 消息记录
     */
    private static List<ChatMessage> messagesList = new ArrayList<>();
    public static void messagesListInit(){
        messagesList = new ArrayList<>();
        ChatMessage defaultMsg = new ChatMessage();
        defaultMsg.setRole("system");
        defaultMsg.setContent("You are a java developer");
        messagesList.add(defaultMsg);
    }
    public static void addAssistantMsg(String  text){
        ChatMessage defaultMsg = new ChatMessage();
        defaultMsg.setRole("assistant");
        defaultMsg.setContent(text);
        messagesList.add(defaultMsg);
    }

    static {
        messagesListInit();
    }

    /**
     * 基于ChatGPT3.5模型的流处理
     * @param chatMessage
     * @return
     */
    public static Flowable<ChatCompletionChunk> getAnswerByChat35(ChatMessage chatMessage){
        StringBuilder sb = new StringBuilder();
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
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messagesList)
                .maxTokens(2048)
                .temperature(0D)
                .stream(true)
                // .topP(1D)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .build();
        try{
            return service.streamChatCompletion(request);
        }catch (Exception e){
            sb.append("出现错误，请稍候再试；\n");
            sb.append(e.getMessage() + "\n");
        }
        return null;

    }



}
