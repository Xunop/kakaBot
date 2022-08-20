package com.mybot.kakaBot.task;

import com.mybot.kakaBot.api.StudyApi;
import com.mybot.kakaBot.entity.QQBot;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author xun
 * @create 2022/6/7 22:41
 */
@Component
public class AnswerHandler {

    @Resource
    QQBot  qqBot;

    @Scheduled(cron = "0 0 15 * * 1,3")
    public void getAnwser() {
        String answer = StudyApi.getAnswer();
        Bot bot = Bot.getInstance(qqBot.account);
        Friend wo = bot.getFriend(qqBot.getMaster());
        Group group = bot.getGroup(705562661);
        assert group != null;
        group.sendMessage(answer);
        assert wo != null;
        wo.sendMessage(answer);
    }
}
