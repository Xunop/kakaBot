package com.mybot.kakaBot.task;

import com.mybot.kakaBot.service.ImagesService;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import okhttp3.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author xun
 * @create 2022/7/29 23:33
 */
@Component
public class ImagesHandle {

    @Resource
    ImagesService imagesService;

/*    public static String getData () throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://api.lolicon.app/setu/v2")
                .method("GET", body)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response);
        return null;
    }*/


/*    @Scheduled(cron = "0 0/20 0-2 * * ?")
    public void test() {
        Bot bot = Bot.getInstance(191416049);
        Friend master = bot.getFriend(1919581623);
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    imagesService.insertImage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    imagesService.insertImage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        assert master != null;
        master.sendMessage("success");
    }*/
}
