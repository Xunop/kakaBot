package com.mybot.kakaBot.api;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.FriendMessageEvent;

import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author xun
 * @create 2022/5/1 19:35
 */
public class SavePicture {
    public static void savePicture(FriendMessageEvent event, Contact contact)
    {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        //创建HttpGet对象
        HttpGet httpGet = new HttpGet("https://api.ixiaowai.cn/api/api.php");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            InputStream content = httpEntity.getContent();
            System.out.println(content);
            ExternalResource resource = ExternalResource.Companion.create(content);
            Image image = ExternalResource.uploadAsImage(resource, contact);
            event.getSubject().sendMessage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
