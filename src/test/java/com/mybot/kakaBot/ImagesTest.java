package com.mybot.kakaBot;

import com.mybot.kakaBot.mapper.ImagesMapper;
import com.mybot.kakaBot.service.ImagesService;
import com.mybot.kakaBot.util.FileUtil;
import com.mybot.kakaBot.util.ImagesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author xun
 * @create 2022/7/29 23:39
 */
@Slf4j
@Component
@SpringBootTest
public class ImagesTest {

    @Resource
    FileUtil fileUtil;
    @Resource
    ImagesUtil imagesUtil;
    @Resource
    ImagesMapper imagesMapper;

    @Autowired
    ImagesService imagesService;


//    @Async("taskExecutor")
//    public void test01() throws IOException {
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//        HttpGet httpGet = new HttpGet("https://api.lolicon.app/setu/v2");
//        CloseableHttpResponse response = httpClient.execute(httpGet);
//        HttpEntity entity = response.getEntity();
//        JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
//        String error = jsonObject.get("error").toString();
//        System.out.println(error);
//        if (error == null) {
//            throw new RuntimeException("error");
//        }
//        Image image = new Image();
//        JSONArray data = jsonObject.getJSONArray("data");
//        JSONObject dataJSONObject = data.getJSONObject(0);
//        System.out.println(dataJSONObject);
//        Long pid = dataJSONObject.getLong("pid");
//        image.setPid(pid);
//        Long uid = dataJSONObject.getLong("uid");
//        image.setUid(uid);
//        String title = dataJSONObject.get("title").toString();
//        image.setTitle(title);
//        String author = dataJSONObject.get("author").toString();
//        image.setAuthor(author);
//        Boolean r18 = dataJSONObject.getBoolean("r18");
//        System.out.println(r18);
//        image.setIsR18(r18);
//        JSONArray tags = dataJSONObject.getJSONArray("tags");
//        image.setTags(tags);
//        JSONObject urls = dataJSONObject.getJSONObject("urls");
//        String original = urls.get("original").toString();
//        image.setUrl(original);
//        String ext = dataJSONObject.get("ext").toString();
//        image.setExt('.' + ext);
//        image.setFileName(pid + tags.get(0).toString());
//
//
//        CloseableHttpClient client = HttpClientBuilder.create().build();
//        HttpGet httpGet1 = new HttpGet(original);
//        CloseableHttpResponse response1 = null;
//        try {
//            response1 = client.execute(httpGet1);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        HttpEntity entity1 = response1.getEntity();
//        try {
//            InputStream content = entity1.getContent();
//            fileUtil.uploadFile("images", image.getFileName() + image.getExt(), content);
//            System.out.println("success");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        imagesMapper.insert(image);
//    }
//
//
//
//    @Async("taskExecutor")
//    public void test02(int i) throws Exception {
//        log.info("start" + i);
//
//    }
//
//    @Test
//    public void test03() throws Exception {
//        for (int i = 0; i < 10; i++) {
//            test02(i);
//            System.out.println(i);
//            imagesService.InsertImage();
//        }
//    }


    public static void main(String[] args) {
        List<Integer> res = new ArrayList<>();
        res.add(1);
        res.add(2);
        res.add(3);
        for (Integer re : res) {

        }
    }
}
