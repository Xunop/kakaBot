//package com.mybot.kakaBot.util;
//
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.mybot.kakaBot.enums.ErrorEnum;
//import com.mybot.kakaBot.exception.LocalRuntimeException;
//import com.mybot.kakaBot.mapper.ImagesMapper;
//import com.mybot.kakaBot.entity.Image;
//import lombok.extern.slf4j.Slf4j;
//import net.mamoe.mirai.contact.Contact;
//import net.mamoe.mirai.event.events.FriendMessageEvent;
//import net.mamoe.mirai.event.events.MessageEvent;
//import net.mamoe.mirai.message.data.MessageChain;
//import net.mamoe.mirai.message.data.MessageChainBuilder;
//import net.mamoe.mirai.utils.ExternalResource;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.util.EntityUtils;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.concurrent.*;
//
///**
// * @Author xun
// * @create 2022/6/5 19:30
// */
//@Slf4j
//@Component
//public class ImagesUtil {
//
//    @Resource
//    ImagesMapper imagesMapper;
//
//    @Resource
//    FileUtil fileUtil;
//
//    final ExecutorService threadPool = Executors.newCachedThreadPool();
//
//    public static void savePicture(FriendMessageEvent event, Contact contact, HttpGet httpGet) {
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//        try {
//            HttpResponse response = httpClient.execute(httpGet);
//            HttpEntity httpEntity = response.getEntity();
//            InputStream content = httpEntity.getContent();
//            ExternalResource resource = ExternalResource.Companion.create(content);
//            net.mamoe.mirai.message.data.Image image = ExternalResource.uploadAsImage(resource, contact);
//            event.getSubject().sendMessage(image);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * ????????????
//     * @return image ????????????????????????
//     */
//
//    public Image getImage(Boolean r18) throws IOException {
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//        HttpGet httpGet;
//        if (r18) {
//            httpGet = new HttpGet("https://api.lolicon.app/setu/v2?r18=1");
//        } else {
//            httpGet = new HttpGet("https://api.lolicon.app/setu/v2");
//        }
//        CloseableHttpResponse response = httpClient.execute(httpGet);
//        HttpEntity entity = response.getEntity();
//        JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
//        String error = jsonObject.get("error").toString();
//        System.out.println(error);
//        if (error == null) {
//            throw new RuntimeException("error");
//        }
//
//        JSONArray data = jsonObject.getJSONArray("data");
//        JSONObject dataJSONObject = data.getJSONObject(0);
//        return setImage(dataJSONObject);
//    }
//
//    /**
//     * ???????????????
//     *
//     * @param tag ??????
//     * @return
//     * @throws IOException
//     */
//
//    public Image getImage(String tag, Integer r18, MessageEvent event) throws IOException {
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//
//        String encode = URLEncoder.encode(tag, StandardCharsets.UTF_8);
//        log.info("r18???" + r18);
//        log.info("param???" + tag);
//        HttpGet httpGet = new HttpGet("https://api.lolicon.app/setu/v2?r18=" + r18 + "&" + "tag=" + encode);
//        CloseableHttpResponse response = httpClient.execute(httpGet);
//        HttpEntity entity = response.getEntity();
//        JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
//        String error = jsonObject.get("error").toString();
//
//        if (!"".equals(error)) {
//            throw new RuntimeException("error");
//        }
//
//        JSONArray data = jsonObject.getJSONArray("data");
//        if (data.isEmpty()) {
//            throw new NullPointerException("???????????????");
//        }
//        JSONObject dataJSONObject = data.getJSONObject(0);
//        return setImage(dataJSONObject);
//    }
//
//
//    static int i = 0;
//
//    /**
//     * ????????????
//     *
//     * @param tag   ?????????
//     * @param r18   r18
//     * @param event ??????
//     * @param num   ???????????? ?????????9
//     */
//    public void getImageList(String tag, Integer r18, MessageEvent event, Integer num) throws IOException,
//            ExecutionException, InterruptedException {
//
//        final ExecutorService threadPool = Executors.newCachedThreadPool();
//
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//        String encode = URLEncoder.encode(tag, StandardCharsets.UTF_8);
//        log.info("r18???" + r18);
//        log.info("param???" + tag);
//        HttpGet httpGet = new HttpGet(
//                "https://api.lolicon.app/setu/v2?r18=" + r18 + "&" + "tag=" + encode + "&" + "num=" + num);
//        CloseableHttpResponse response = httpClient.execute(httpGet);
//        HttpEntity entity = response.getEntity();
//        JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
//        String error = jsonObject.get("error").toString();
//
//        if (!"".equals(error)) {
//            throw new RuntimeException("error");
//        }
//
//        JSONArray data = jsonObject.getJSONArray("data");
//
//        if (data.isEmpty()) {
//            throw new LocalRuntimeException(ErrorEnum.IMAGE_NOT_EXIST);
//        }
//
//        // ????????????data??????????????????
//        for (Object datum : data) {
//
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                JSONObject object = JSONObject.parseObject(datum.toString());
//                Long pid = object.getLong("pid");
//                // ??????????????????????????????????????????minio?????????????????????
//                if (imagesMapper.exists(new QueryWrapper<Image>().eq("pid", pid))) {
//                    Image image = imagesMapper.selectOne(
//                            new QueryWrapper<Image>().eq("pid", pid));
//                    try (InputStream stream = fileUtil.getObject("images", image.getFileName())) {
//                        sendMessage(event, stream, image);
//                    } catch (IOException e) {
//                        log.error(e.getMessage());
//                        throw new LocalRuntimeException(ErrorEnum.GET_OBJECT_ERROR);
//                    }
//                } else {
//                    Image image = setImage(object);
//                    try (InputStream stream = getImageStream(image.getUrl())) {
//                        sendMessage(event, stream, image);
//                        insertImage(image);
//                    } catch (IOException e) {
//                        log.error(e.getMessage());
//                        throw new LocalRuntimeException(ErrorEnum.GET_OBJECT_ERROR);
//                    }
//                }
//            }, threadPool);
//            future.get();
//        }
//        threadPool.shutdown();
//    }
//
//
//    /**
//     * ??????????????????????????????
//     *
//     * @param image ??????
//     */
//    public void insertImage(Image image) {
//        String tempFileName = image.getFileName();
//        String fileName = tempFileName.replace("/", "");
//        CloseableHttpClient client = HttpClientBuilder.create().build();
//        HttpGet httpGet = new HttpGet(image.getUrl());
//        CloseableHttpResponse response = null;
//        try {
//            response = client.execute(httpGet);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        HttpEntity entity = response.getEntity();
//        try {
//            InputStream content = entity.getContent();
//            fileUtil.uploadFile("images", fileName, content);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        imagesMapper.insert(image);
//        log.info("?????????????????????");
//    }
//
//
//    /**
//     * ??????JSON??????
//     *
//     * @param dataJSONObject JSON??????
//     * @return image?????????
//     */
//    public Image setImage(JSONObject dataJSONObject) {
//
//        Image image = new Image();
//        Long pid = dataJSONObject.getLong("pid");
//        JSONObject urls = dataJSONObject.getJSONObject("urls");
//        String original = urls.get("original").toString();
//        JSONArray tags = dataJSONObject.getJSONArray("tags");
//        String ext = dataJSONObject.get("ext").toString();
//        Boolean r18 = dataJSONObject.getBoolean("r18");
//        String author = dataJSONObject.get("author").toString();
//        String title = dataJSONObject.get("title").toString();
//        Long uid = dataJSONObject.getLong("uid");
//
//        image.setExt('.' + ext);
//        // ???????????? pid + ?????? + ??????
//        String fileName = pid + title + image.getExt();
//        if (fileName.contains("/")) {
//            fileName = fileName.replace("/", "");
//        }
//        image.setFileName(fileName);
//        image.setTags(tags);
//        image.setUrl(original);
//        image.setPid(pid);
//        image.setUid(uid);
//        image.setTitle(title);
//        image.setAuthor(author);
//        image.setIsR18(r18);
//        return image;
//    }
//
//    /**
//     * ????????????????????????
//     *
//     * @param event  ??????
//     * @param stream ?????????
//     * @param image  ????????????
//     * @throws IOException
//     */
//    public void sendMessage(MessageEvent event, InputStream stream, Image image) throws IOException {
//        try (stream; ExternalResource resource = ExternalResource.Companion.create(stream)) {
//            net.mamoe.mirai.message.data.Image upload = ExternalResource.uploadAsImage(resource,
//                    event.getSubject());
//            List<String> tagList = image.getTags().toList(String.class);
//            int size = tagList.size();
//            List<String> resTags = tagList.subList(0, Math.min(8, size));
//
//            MessageChain messages = new MessageChainBuilder()
//                    .append(upload)
//                    .append('\n')
//                    .append("title???")
//                    .append(image.getTitle())
//                    .append(String.valueOf('\n'))
//                    .append("author???")
//                    .append(image.getAuthor())
//                    .append(String.valueOf('\n'))
//                    .append("r18???")
//                    .append(image.getIsR18().toString())
//                    .append(String.valueOf('\n'))
//                    .append("tags???")
//                    .append(resTags.toString())
//                    .append(String.valueOf('\n'))
//                    .append("url???")
//                    .append(image.getUrl())
//                    .build();
//            event.getSubject().sendMessage(messages).recallIn(30 * 1000);
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            throw new LocalRuntimeException(ErrorEnum.STREAM_ERROR);
//        }
//
//    }
//
//    /**
//     * ???????????????????????????????????????
//     *
//     * @param url ????????????
//     * @return InputStream
//     * @throws IOException
//     */
//    public InputStream getImageStream(String url) throws IOException {
//
//        CloseableHttpClient client = HttpClientBuilder.create().build();
//        HttpGet httpGet = new HttpGet(url);
//        CloseableHttpResponse response = null;
//        try {
//            response = client.execute(httpGet);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        HttpEntity entity = response.getEntity();
//        try {
//            return entity.getContent();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
