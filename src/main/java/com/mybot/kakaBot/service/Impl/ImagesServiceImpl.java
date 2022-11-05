//package com.mybot.kakaBot.service.Impl;
//
//import com.mybot.kakaBot.entity.Image;
//import com.mybot.kakaBot.mapper.ImagesMapper;
//import com.mybot.kakaBot.service.ImagesService;
//import com.mybot.kakaBot.util.FileUtil;
//import com.mybot.kakaBot.util.ImagesUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.io.InputStream;
//
///**
// * @Author xun
// * @create 2022/5/1 14:05
// */
//@Slf4j
//@Service
//public class ImagesServiceImpl implements ImagesService {
//
//    @Resource
//    ImagesMapper imagesMapper;
//    @Resource
//    ImagesUtil imagesUtil;
//    @Resource
//    FileUtil fileUtil;
//
//    /**
//     * 插入到数据库，上传到 minio
//     *
//     * @throws IOException
//     */
//    @Async("taskExecutor")
//    @Override
//    public void insertImage() throws IOException {
//        Image image = imagesUtil.getImage(true);
//        String tempFileName = image.getFileName();
//        String fileName = tempFileName.replace("/", "");
//        CloseableHttpClient client = HttpClientBuilder.create().build();
//        HttpGet httpGet = new HttpGet(image.getUrl());
//        CloseableHttpResponse response1 = null;
//        try {
//            response1 = client.execute(httpGet);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        HttpEntity entity1 = response1.getEntity();
//        try {
//            InputStream content = entity1.getContent();
//            fileUtil.uploadFile("images", fileName, content);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        imagesMapper.insert(image);
//        log.info("保存数据库成功");
//    }
//}
