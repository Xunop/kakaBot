package com.mybot.kakaBot.api;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

/**
 * 网易有道翻译接口
 * @Author xun
 * @create 2022/7/29 20:08
 */
@Component
@Slf4j
public class Translate {

    public static String translation (String sentence, Boolean toJp) throws IOException, NoSuchAlgorithmException, NullPointerException {
        URL url = new URL("https://fanyi.youdao.com/translate_o?smartresult=dict&smartresult=rule");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");

        httpConn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        httpConn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        httpConn.setRequestProperty("Connection", "keep-alive");
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpConn.setRequestProperty("Cookie", "OUTFOX_SEARCH_USER_ID=-1150633487@10.110.96.153; OUTFOX_SEARCH_USER_ID_NCOO=1163609727.4381664; P_INFO=13367749488|1655016423|1|dict_logon|00&99|null&null&null#jis&320100#10#0|&0||13367749488; ___rl__test__cookies=1659081975422");
        httpConn.setRequestProperty("Origin", "https://fanyi.youdao.com");
        httpConn.setRequestProperty("Referer", "https://fanyi.youdao.com/");
        httpConn.setRequestProperty("Sec-Fetch-Dest", "empty");
        httpConn.setRequestProperty("Sec-Fetch-Mode", "cors");
        httpConn.setRequestProperty("Sec-Fetch-Site", "same-origin");
        httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.134 Safari/537.36 Edg/103.0.1264.71");
        httpConn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        httpConn.setRequestProperty("sec-ch-ua", "\" Not;A Brand\";v=\"99\", \"Microsoft Edge\";v=\"103\", \"Chromium\";v=\"103\"");
        httpConn.setRequestProperty("sec-ch-ua-mobile", "?0");
        httpConn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");

        httpConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());

        // 对有道词典的一些处理
        String encode = URLEncoder.encode(sentence, StandardCharsets.UTF_8);
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        long ltsTemp = System.currentTimeMillis();
        String lts = URLEncoder.encode(String.valueOf(ltsTemp), StandardCharsets.UTF_8);

        String salt = lts + String.valueOf(new Random().nextInt(10));
        // 这里的 temp 是查询关键词本身 + salt + 一串字符串   其中查询词本身是没有编码前的
        String temp = "fanyideskweb" + sentence + salt + "Ygy_4c=r#e#4EX^NUGUc5";
        md5.update(temp.getBytes());

        String signTemp = new BigInteger(1, md5.digest()).toString(16);
        String sign = URLEncoder.encode(signTemp, StandardCharsets.UTF_8);
        if (!toJp) {
            writer.write("i=" + encode + "&from=AUTO&to=AUTO&smartresult=dict&client=fanyideskweb&salt=" + salt + "&sign=" + sign + "&lts=" + lts + "&bv=8b6c8845a74df59c1f2be6165a6104b5&doctype=json&version=2.1&keyfrom=fanyi.web&action=FY_BY_CLICKBUTTION");
        } else {
            // 中文转日文
            writer.write("i=" + encode + "&from=zh-CHS&to=ja&smartresult=dict&client=fanyideskweb&salt=" + salt + "&sign=" + sign + "&lts=" + lts + "&bv=8b6c8845a74df59c1f2be6165a6104b5&doctype=json&version=2.1&keyfrom=fanyi.web&action=FY_BY_CLICKBUTTION");
        }
        writer.flush();
        writer.close();
        httpConn.getOutputStream().close();

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";

        JSONObject parseObject = JSONObject.parseObject(response);

        if (parseObject != null){
            if (parseObject.getJSONObject("smartResult") == null) {
                JSONArray translateResult = parseObject.getJSONArray("translateResult");
                JSONArray jsonArray = translateResult.getJSONArray(0);
                StringBuilder result = new StringBuilder();
                for (Object o : jsonArray) {
                    result.append(JSONObject.parseObject(o.toString()).get("tgt"));
                }
                return result.toString();
            } else {
                JSONObject smartResult = parseObject.getJSONObject("smartResult");
                JSONArray entries = smartResult.getJSONArray("entries");
                return entries.get(1).toString();
            }
        }else {
            throw new  NullPointerException("未查到该词");
        }
    }
}
