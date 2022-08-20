package com.mybot.kakaBot.api;

import com.mybot.kakaBot.entity.Image;
import com.mybot.kakaBot.service.ImagesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author xun
 * @create 2022/5/1 15:34
 */
@Component
@Slf4j
public class ImageApi {

    @Autowired
    ImagesService imagesService;
    public static String get() {
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        //创建HttpGet对象
        HttpGet httpGet = new HttpGet("https://api.ixiaowai.cn/api/api.php");
        try{
            //执行Get请求
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());
            //获取响应实体
            HttpEntity entity = response.getEntity();
            //处理响应实体
            if(entity != null){
                System.out.println("长度：" + entity.getContentLength());
                System.out.println(entity);
                Image image = new Image();
                System.out.println("===============");
            }
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                httpClient.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
