package com.mybot.kakaBot.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mybot.kakaBot.controller.ImageController;
import com.mybot.kakaBot.enums.ErrorEnum;
import com.mybot.kakaBot.exception.LocalRuntimeException;
import com.mybot.kakaBot.mapper.ImagesMapper;
import com.mybot.kakaBot.entity.Image;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author xun
 * @create 2022/6/5 19:30
 */
@Slf4j
@Component
public class ImagesUtil {

    @Resource
    ImagesMapper imagesMapper;

    @Resource
    FileUtil fileUtil;

    final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void savePicture(FriendMessageEvent event, Contact contact, HttpGet httpGet) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            InputStream content = httpEntity.getContent();
            ExternalResource resource = ExternalResource.Companion.create(content);
            net.mamoe.mirai.message.data.Image image = ExternalResource.uploadAsImage(resource, contact);
            event.getSubject().sendMessage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取图片
     *
     * @return image 获取到图片的信息
     * @throws IOException
     */

    public Image getImage(Boolean r18) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet;
        if (r18) {
            httpGet = new HttpGet("https://api.lolicon.app/setu/v2?r18=1");
        } else {
            httpGet = new HttpGet("https://api.lolicon.app/setu/v2");
        }
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
        String error = jsonObject.get("error").toString();
        System.out.println(error);
        if (error == null) {
            throw new RuntimeException("error");
        }

        JSONArray data = jsonObject.getJSONArray("data");
        JSONObject dataJSONObject = data.getJSONObject(0);
        return setImage(dataJSONObject);
    }

    /**
     * 带标签搜索
     *
     * @param tag 参数
     * @return
     * @throws IOException
     */

    public Image getImage(String tag, Integer r18, MessageEvent event) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        String encode = URLEncoder.encode(tag, StandardCharsets.UTF_8);
        log.info("r18：" + r18);
        log.info("param：" + tag);
        HttpGet httpGet = new HttpGet("https://api.lolicon.app/setu/v2?r18=" + r18 + "&" + "tag=" + encode);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
        String error = jsonObject.get("error").toString();

        if (!"".equals(error)) {
            throw new RuntimeException("error");
        }

        JSONArray data = jsonObject.getJSONArray("data");
        if (data.isEmpty()) {
            throw new NullPointerException("没有这个图");
        }
        JSONObject dataJSONObject = data.getJSONObject(0);
        return setImage(dataJSONObject);
    }


    static int i = 0;

    /**
     * 批量发送
     *
     * @param tag   关键词
     * @param r18   r18
     * @param event 事件
     * @param num   发送次数 最大为9
     */
    public void getImageList(String tag, Integer r18, MessageEvent event, Integer num) throws IOException,
            ExecutionException, InterruptedException {

        final ExecutorService threadPool = Executors.newCachedThreadPool();

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String encode = URLEncoder.encode(tag, StandardCharsets.UTF_8);
        log.info("r18：" + r18);
        log.info("param：" + tag);
        HttpGet httpGet = new HttpGet(
                "https://api.lolicon.app/setu/v2?r18=" + r18 + "&" + "tag=" + encode + "&" + "num=" + num);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
        String error = jsonObject.get("error").toString();

        if (!"".equals(error)) {
            throw new RuntimeException("error");
        }

        JSONArray data = jsonObject.getJSONArray("data");
        System.out.println(data);
        if (data.isEmpty()) {
            throw new LocalRuntimeException(ErrorEnum.IMAGE_NOT_EXIST);
        }

        // 解析出来data肯定是个数组
        for (Object datum : data) {

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                JSONObject object = JSONObject.parseObject(datum.toString());
                Long pid = object.getLong("pid");
                // 如果在数据库中查到这张图则从minio中取图片并发送
                if (imagesMapper.exists(new QueryWrapper<Image>().eq("pid", pid))) {
                    Image image = imagesMapper.selectOne(
                            new QueryWrapper<Image>().eq("pid", pid));
                    try (InputStream stream = fileUtil.getObject("images", image.getFileName())) {
                        sendMessage(event, stream, image);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new LocalRuntimeException(ErrorEnum.GET_OBJECT_ERROR);
                    }
                } else {
                    Image image = setImage(object);
                    try (InputStream stream = getImageStream(image.getUrl())) {
                        sendMessage(event, stream, image);
                        insertImage(image);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new LocalRuntimeException(ErrorEnum.GET_OBJECT_ERROR);
                    }
                }
            }, threadPool);
            future.get();
        }
        threadPool.shutdown();
    }


    /**
     * 将图片插入到数据库中
     *
     * @param image 图片
     */
    public void insertImage(Image image) {
        String tempFileName = image.getFileName();
        String fileName = tempFileName.replace("/", "");
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(image.getUrl());
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpEntity entity = response.getEntity();
        try {
            InputStream content = entity.getContent();
            fileUtil.uploadFile("images", fileName, content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        imagesMapper.insert(image);
        log.info("保存数据库成功");
    }


    /**
     * 解析JSON数据
     *
     * @param dataJSONObject JSON数据
     * @return image实体类
     */
    public Image setImage(JSONObject dataJSONObject) {

        Image image = new Image();
        Long pid = dataJSONObject.getLong("pid");
        JSONObject urls = dataJSONObject.getJSONObject("urls");
        String original = urls.get("original").toString();
        JSONArray tags = dataJSONObject.getJSONArray("tags");
        String ext = dataJSONObject.get("ext").toString();
        Boolean r18 = dataJSONObject.getBoolean("r18");
        String author = dataJSONObject.get("author").toString();
        String title = dataJSONObject.get("title").toString();
        Long uid = dataJSONObject.getLong("uid");

        image.setExt('.' + ext);
        // 文件名为 pid + 标题 + 后缀
        String fileName = pid + title + image.getExt();
        if (fileName.contains("/")) {
            fileName = fileName.replace("/", "");
        }
        image.setFileName(fileName);
        image.setTags(tags);
        image.setUrl(original);
        image.setPid(pid);
        image.setUid(uid);
        image.setTitle(title);
        image.setAuthor(author);
        image.setIsR18(r18);
        return image;
    }

    /**
     * 构建消息链并发送
     *
     * @param event  事件
     * @param stream 图片流
     * @param image  图片数据
     * @throws IOException
     */
    public void sendMessage(MessageEvent event, InputStream stream, Image image) throws IOException {
        try (stream; ExternalResource resource = ExternalResource.Companion.create(stream)) {
            net.mamoe.mirai.message.data.Image upload = ExternalResource.uploadAsImage(resource,
                    event.getSubject());
            List<String> tagList = image.getTags().toList(String.class);
            int size = tagList.size();
            List<String> resTags = tagList.subList(0, Math.min(8, size));

            MessageChain messages = new MessageChainBuilder()
                    .append(upload)
                    .append('\n')
                    .append("title：")
                    .append(image.getTitle())
                    .append(String.valueOf('\n'))
                    .append("author：")
                    .append(image.getAuthor())
                    .append(String.valueOf('\n'))
                    .append("r18：")
                    .append(image.getIsR18().toString())
                    .append(String.valueOf('\n'))
                    .append("tags：")
                    .append(resTags.toString())
                    .append(String.valueOf('\n'))
                    .append("url：")
                    .append(image.getUrl())
                    .build();
            event.getSubject().sendMessage(messages).recallIn(30 * 1000);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new LocalRuntimeException(ErrorEnum.STREAM_ERROR);
        }

    }

    /**
     * 通过访问图片地址获取图片流
     *
     * @param url 图片地址
     * @return InputStream
     * @throws IOException
     */
    public InputStream getImageStream(String url) throws IOException {

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpEntity entity = response.getEntity();
        try {
            return entity.getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
