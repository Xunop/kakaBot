package com.mybot.kakaBot.api;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author xun
 * @create 2022/6/8 0:38
 */
@Component
public class GifApi {
    // 锤gif
    public static void sendChui(GroupMessageEvent event, Contact contact, String id) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String httpurl = "https://api.klizi.cn/API/gif/hammer.php?qq=" + id;
        HttpGet httpGet = new HttpGet(httpurl);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            InputStream content = httpEntity.getContent();
            ExternalResource resource = ExternalResource.Companion.create(content);
            Image image = ExternalResource.uploadAsImage(resource, contact);
            event.getSubject().sendMessage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 抓
    public static void sendZhui(GroupMessageEvent event, Contact contact, String id) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String httpurl = "https://api.klizi.cn/API/gif/tightly.php?qq=" + id;
        HttpGet httpGet = new HttpGet(httpurl);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            InputStream content = httpEntity.getContent();
            ExternalResource resource = ExternalResource.Companion.create(content);
            Image image = ExternalResource.uploadAsImage(resource, contact);
            event.getSubject().sendMessage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 丢gif
    public static void sendDiu(GroupMessageEvent event, Contact contact, String id) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String httpurl = "https://api.klizi.cn/API/ce/diu.php?qq=" + id;
        HttpGet httpGet = new HttpGet(httpurl);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            InputStream content = httpEntity.getContent();
            ExternalResource resource = ExternalResource.Companion.create(content);
            Image image = ExternalResource.uploadAsImage(resource, contact);
            event.getSubject().sendMessage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
