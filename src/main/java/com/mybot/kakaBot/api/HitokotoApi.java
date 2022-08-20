package com.mybot.kakaBot.api;


import com.alibaba.fastjson.JSONObject;
import com.mybot.kakaBot.entity.Result;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 一言的接口
 * @Author xun
 * @create 2022/4/30 23:37
 */
@Component
public class HitokotoApi {
    public Result get() {
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        //创建HttpGet对象
        HttpGet httpGet = new HttpGet("https://v1.hitokoto.cn/");
        System.out.println("executing request " + httpGet.getURI());
        try{
            //执行Get请求
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());
            //获取响应实体
            HttpEntity entity = response.getEntity();
            //处理响应实体
            if(entity != null){
                System.out.println("长度：" + entity.getContentLength());
                //将json转换成可以用的数据
                JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
                Result result = new Result();
                result.setSen((String) jsonObject.get("hitokoto"));
                result.setSource((String) jsonObject.get("from"));
                return result;
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
