package com.mybot.kakaBot.task;

import com.mybot.kakaBot.api.Notice;
import com.mybot.kakaBot.entity.QQBot;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author xun
 * @create 2022/6/5 12:16
 */
@Component
public class NoticeHandler {

    @Resource
    QQBot qqBot;
    @Scheduled(cron = "0 30 8,23 * * ?")
    public void timingSendNotice() throws IOException {
        Bot bot = Bot.getInstance(qqBot.account);
        Friend xyr = bot.getFriend(qqBot.getFriend1());
        Friend master = bot.getFriend(qqBot.getMaster());
        Friend bt = bot.getFriend(qqBot.getFriend2());
        Friend zym = bot.getFriend(qqBot.getFriend3());
        List<Object[]> notice = Notice.getNotice();
        List<Friend> friends = new ArrayList<>();
        friends.add(master);
        friends.add(xyr);
        friends.add(bt);
        friends.add(zym);
        SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String currentTime = fomat.format(date);
        // 好友
        for (Friend friend : friends) {
            // 计数：用于判断消息是不是当天的
            int index = 0;
            for (Object[] objects : notice) {
                if (currentTime.equals(objects[2])) {
                    String news = "ฅ^•ﻌ•^ฅ时间--->" + objects[2] + "\n" + objects[1] + objects[0];
                    friend.sendMessage(news);
                    index++;
                }
            }
        }
        // 群组
//        int index1 = 0;
/*        for (Object[] objects : notice) {
            if (currentTime.equals(objects[2])) {
                String news = "ฅ^•ﻌ•^ฅ时间--->" + objects[2] + "\n" + objects[1] + objects[0];
                if (cjr != null) {
                    cjr.sendMessage(news);
                    index1++;
                }
            }
        }*/
/*        if (index1 == 0) {
            cjr.sendMessage("今天没有新通知 ◔.̮◔✧，你可以发送/通知查看以往通知噢");
            cjr.sendMessage("可能会错过通知 /ᐠ｡ꞈ｡ᐟ\\，可以输入 /通知 查看");
        }*/
    }
}
