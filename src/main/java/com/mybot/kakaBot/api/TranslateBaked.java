package com.mybot.kakaBot.api;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.AudioSupported;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.OfflineAudio;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 有道智云接口
 * @Author xun
 * @create 2022/8/2 0:15
 */
@Slf4j
@Component
public class TranslateBaked {

    /**
     * 群聊中的翻译，可以发送语音
     * @param event 群聊消息
     * @param word 需要翻译的文本
     * @param toJp 为 true 翻译成日语， 为 false 翻译成英语
     * @return 翻译结果
     * @throws IOException
     */
    public static String tran(GroupMessageEvent event, String word, Boolean toJp) throws IOException {

        final ExecutorService threadPool = Executors.newCachedThreadPool();

        URL url = new URL("https://aidemo.youdao.com/trans");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("authority", "aidemo.youdao.com");
        httpConn.setRequestProperty("accept", "application/json, text/javascript, */*; q=0.01");
        httpConn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
        httpConn.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpConn.setRequestProperty("origin", "https://ai.youdao.com");
        httpConn.setRequestProperty("referer", "https://ai.youdao.com/");
        httpConn.setRequestProperty("sec-ch-ua",
                "\" Not;A Brand\";v=\"99\", \"Microsoft Edge\";v=\"103\", \"Chromium\";v=\"103\"");
        httpConn.setRequestProperty("sec-ch-ua-mobile", "?0");
        httpConn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
        httpConn.setRequestProperty("sec-fetch-dest", "empty");
        httpConn.setRequestProperty("sec-fetch-mode", "cors");
        httpConn.setRequestProperty("sec-fetch-site", "same-site");
        httpConn.setRequestProperty("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060" +
                        ".134 Safari/537.36 Edg/103.0.1264.77");
        httpConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        // zh-CHS&to=ja
        // Auto&to=Auto
        if (toJp) {
            writer.write("q=" + word + "&from=zh-CHS&to=ja");
        }else {
            writer.write("q=" + word + "&from=Auto&to=Auto");
        }
        writer.flush();
        writer.close();
        httpConn.getOutputStream().close();
        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";

        // 解析 JSON 数据
        JSONObject jsonObject = JSONObject.parseObject(response);
        // 为空抛出 NullPointerException
        if (jsonObject == null) throw new NullPointerException("未查到该词");

        log.info("解析到数据：" + jsonObject);

        // 如果是句子
        if (!jsonObject.getBoolean("isWord")) {
            JSONArray translation = jsonObject.getJSONArray("translation");
            String result = translation.getString(0);
            log.info("获取到翻译结果：" + result);

            String tSpeakUrl = jsonObject.getString("tSpeakUrl");
            log.info("获取到tSpeakUrl：" + tSpeakUrl);
            threadPool.submit(() -> {
                HttpGet httpGet = new HttpGet(tSpeakUrl);
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse execute = httpClient.execute(httpGet);
                try (InputStream stream = execute.getEntity().getContent();
                     ExternalResource resource = ExternalResource.Companion.create(stream)) {
                    OfflineAudio audio = event.getGroup().uploadAudio(resource);
                    event.getGroup().sendMessage(audio);
                    return result;
                }
            });
            if (jsonObject.getString("speakUrl") != null) {
                String speakUrl = jsonObject.getString("speakUrl");
                log.info("获取到speakUrl：" + speakUrl);
                threadPool.submit(() -> {
                    HttpGet httpGet = new HttpGet(speakUrl);
                    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                    CloseableHttpResponse execute = httpClient.execute(httpGet);
                    try (InputStream stream = execute.getEntity().getContent();
                         ExternalResource resource = ExternalResource.Companion.create(stream)) {
                        OfflineAudio audio = event.getGroup().uploadAudio(resource);
                        event.getGroup().sendMessage(audio);
                        return result;
                    }
                });
            }
            threadPool.shutdown();

            return result;
        } else {    // 是单词
            JSONObject basic = jsonObject.getJSONObject("basic");
            JSONArray explains = basic.getJSONArray("explains");
            String result = explains.getString(0);
            log.info("获取到翻译结果：" + result);

            String tSpeakUrl = jsonObject.getString("tSpeakUrl");
            log.info("获取到tSpeakUrl：" + tSpeakUrl);
            threadPool.submit(() -> {
                HttpGet httpGet = new HttpGet(tSpeakUrl);
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse execute = httpClient.execute(httpGet);
                try (InputStream stream = execute.getEntity().getContent();
                     ExternalResource resource = ExternalResource.Companion.create(stream)) {
                    OfflineAudio audio = event.getGroup().uploadAudio(resource);
                    event.getGroup().sendMessage(audio);
                    return result;
                }
            });
            if (jsonObject.getString("speakUrl") != null) {
                String speakUrl = jsonObject.getString("speakUrl");
                log.info("获取到speakUrl：" + speakUrl);
                threadPool.submit(() -> {
                    HttpGet httpGet = new HttpGet(speakUrl);
                    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                    CloseableHttpResponse execute = httpClient.execute(httpGet);
                    try (InputStream stream = execute.getEntity().getContent();
                         ExternalResource resource = ExternalResource.Companion.create(stream)) {
                        OfflineAudio audio = event.getGroup().uploadAudio(resource);
                        event.getGroup().sendMessage(audio);
                        return result;
                    }
                });
            }
            threadPool.shutdown();

            return result;
        }
    }

    /**
     * 私聊消息中的翻译，无法发送语音
     * @param event 群聊消息
     * @param word 需要翻译的文本
     * @param toJp 为 true 翻译成日语， 为 false 翻译成英语
     * @return 翻译结果
     * @throws IOException
     */
    public static String tran(FriendMessageEvent event, String word, Boolean toJp) throws IOException {

        final ExecutorService threadPool = Executors.newCachedThreadPool();

        URL url = new URL("https://aidemo.youdao.com/trans");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("authority", "aidemo.youdao.com");
        httpConn.setRequestProperty("accept", "application/json, text/javascript, */*; q=0.01");
        httpConn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
        httpConn.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpConn.setRequestProperty("origin", "https://ai.youdao.com");
        httpConn.setRequestProperty("referer", "https://ai.youdao.com/");
        httpConn.setRequestProperty("sec-ch-ua",
                "\" Not;A Brand\";v=\"99\", \"Microsoft Edge\";v=\"103\", \"Chromium\";v=\"103\"");
        httpConn.setRequestProperty("sec-ch-ua-mobile", "?0");
        httpConn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
        httpConn.setRequestProperty("sec-fetch-dest", "empty");
        httpConn.setRequestProperty("sec-fetch-mode", "cors");
        httpConn.setRequestProperty("sec-fetch-site", "same-site");
        httpConn.setRequestProperty("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060" +
                        ".134 Safari/537.36 Edg/103.0.1264.77");
        httpConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        // zh-CHS&to=ja
        // Auto&to=Auto
        if (toJp) {
            writer.write("q=" + word + "&from=zh-CHS&to=ja");
        }else {
            writer.write("q=" + word + "&from=Auto&to=Auto");
        }
        writer.flush();
        writer.close();
        httpConn.getOutputStream().close();
        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";

        // 解析 JSON 数据
        JSONObject jsonObject = JSONObject.parseObject(response);
        // 为空抛出 NullPointerException
        if (jsonObject == null) throw new NullPointerException("未查到该词");

        log.info("解析到数据：" + jsonObject);
        // 如果是句子
        if (!jsonObject.getBoolean("isWord")) {
            JSONArray translation = jsonObject.getJSONArray("translation");
            String result = translation.getString(0);
            log.info("获取到翻译结果：" + result);
            return result;
        } else {    // 是单词
            JSONObject basic = jsonObject.getJSONObject("basic");
            JSONArray explains = basic.getJSONArray("explains");
            String result = explains.getString(0);
            log.info("获取到翻译结果：" + result);
            return result;
        }
    }



//    public static void main(String[] args) throws IOException {
//        String Word = "Determines if the specified Object is assignment-compatible with the object represented by ";
//        URL url = new URL("https://aidemo.youdao.com/trans");
//        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//        httpConn.setRequestMethod("POST");
//
//        httpConn.setRequestProperty("authority", "aidemo.youdao.com");
//        httpConn.setRequestProperty("accept", "application/json, text/javascript, */*; q=0.01");
//        httpConn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
//        httpConn.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
//        httpConn.setRequestProperty("origin", "https://ai.youdao.com");
//        httpConn.setRequestProperty("referer", "https://ai.youdao.com/");
//        httpConn.setRequestProperty("sec-ch-ua",
//                "\" Not;A Brand\";v=\"99\", \"Microsoft Edge\";v=\"103\", \"Chromium\";v=\"103\"");
//        httpConn.setRequestProperty("sec-ch-ua-mobile", "?0");
//        httpConn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
//        httpConn.setRequestProperty("sec-fetch-dest", "empty");
//        httpConn.setRequestProperty("sec-fetch-mode", "cors");
//        httpConn.setRequestProperty("sec-fetch-site", "same-site");
//        httpConn.setRequestProperty("user-agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060" +
//                        ".134 Safari/537.36 Edg/103.0.1264.77");
//
//        httpConn.setDoOutput(true);
//        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
//        writer.write("q=" + Word + "&from=Auto&to=Auto");
//        writer.flush();
//        writer.close();
//        httpConn.getOutputStream().close();
//
//        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
//                ? httpConn.getInputStream()
//                : httpConn.getErrorStream();
//        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
//        String response = s.hasNext() ? s.next() : "";
//        JSONObject jsonObject = JSONObject.parseObject(response);
//        System.out.println(jsonObject);
//        if (!jsonObject.getBoolean("isWord")) {
//            JSONArray translation = jsonObject.getJSONArray("translation");
//            String result = translation.getString(0);
//            if (jsonObject.getString("speakUrl") != null) {
//                String speakUrl = jsonObject.getString("speakUrl");
//                System.out.println(speakUrl);
//            }
//            String tSpeakUrl = jsonObject.getString("tSpeakUrl");
//            System.out.println(tSpeakUrl);
//            System.out.println(result);
//        } else {
//            JSONObject basic = jsonObject.getJSONObject("basic");
//            JSONArray explains = basic.getJSONArray("explains");
//            String result = explains.getString(0);
//            System.out.println(result);
//        }
//
//
//    }
}
