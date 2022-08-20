package com.mybot.kakaBot.controller;

import com.mybot.kakaBot.anotation.Event;
import com.mybot.kakaBot.api.Notice;
import com.mybot.kakaBot.api.Translate;
import com.mybot.kakaBot.api.TranslateBaked;
import com.mybot.kakaBot.enums.EventType;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author xun
 * @create 2022/7/31 14:03
 */

@Slf4j
@Controller
public class TranslateController {

    /**
     * 翻译
     * 群聊中的翻译，不然的话是无法发送语音的
     * @param event 消息
     */
    @Event(type = EventType.Group, command = "翻译")
    public void translate(GroupMessageEvent event) {

        final ExecutorService threadPool = Executors.newCachedThreadPool();
        Bot bot = Bot.getInstance(191416049);
        event.getSubject().sendMessage("进入翻译功能，输入 exit 退出");
        threadPool.submit(() -> {
            bot.getEventChannel().subscribe(GroupMessageEvent.class, event1 -> {
                if (event1.getMessage().contentToString().equals("exit")) {
                    log.info("翻译停止");
                    event.getSubject().sendMessage("bye~");
                    return ListeningStatus.STOPPED;
                }

                String sentence = event1.getMessage().contentToString();
                String resWord = "";
                String result = "";
                // 这里的机制是这样的：
                // 如果 tran 方法报错则调用 translation 这个接口不支持发送语音
                try {
                    result = TranslateBaked.tran(event1, sentence, false);
                } catch (NullPointerException | IOException e) {
                    log.error(e.getMessage());
                    try {
                        resWord = Translate.translation(sentence, false);
                        event.getSubject().sendMessage(resWord);
                    } catch (IOException | NoSuchAlgorithmException | NullPointerException e1) {
                        event.getSubject().sendMessage("哎呀，没找到这个单词");
                        log.error(e1.getMessage());
                        throw new RuntimeException(e1);
                    }
                }
                event.getSubject().sendMessage(result);
                return ListeningStatus.LISTENING;
            });
        });

        threadPool.shutdown();
    }


    /**
     * 翻译
     * 私聊中的翻译，不发送语音
     * @param event 消息
     */
    @Event(type = EventType.User, command = "翻译")
    public void translate(FriendMessageEvent event) {

        final ExecutorService threadPool = Executors.newCachedThreadPool();
        Bot bot = Bot.getInstance(191416049);
        event.getSubject().sendMessage("进入翻译功能，输入 exit 退出");
        threadPool.submit(() -> {
            bot.getEventChannel().subscribe(FriendMessageEvent.class, event1 -> {
                if (event1.getMessage().contentToString().equals("exit")) {
                    log.info("翻译停止");
                    event1.getSubject().sendMessage("bye~");
                    return ListeningStatus.STOPPED;
                }
                String sentence = event1.getMessage().contentToString();
                String resWord = "";
                String result = "";
                // 这里的机制是这样的：
                // 如果 tran 方法报错则调用 translation 这个接口不支持发送语音
                try {
                    result = TranslateBaked.tran(event1, sentence, false);
                } catch (NullPointerException | IOException e) {
                    log.error(e.getMessage());
                    try {
                        resWord = Translate.translation(sentence, false);
                        event1.getSubject().sendMessage(resWord);
                    } catch (IOException | NoSuchAlgorithmException | NullPointerException e1) {
                        event1.getSubject().sendMessage("哎呀，没找到这个单词");
                        log.error(e1.getMessage());
                        throw new RuntimeException(e1);
                    }
                }
                event1.getSubject().sendMessage(result);
                return ListeningStatus.LISTENING;
            });
        });

        threadPool.shutdown();
    }

    /**
     * 翻译中转日
     * 群聊中的翻译，不然的话是无法发送语音的
     * @param event 消息
     */
    @Event(type = EventType.Group, command = "翻译中转日")
    public void translateToJp(GroupMessageEvent event) {

        final ExecutorService threadPool = Executors.newCachedThreadPool();
        Bot bot = Bot.getInstance(191416049);
        event.getSubject().sendMessage("进入翻译功能，输入 exit 退出");
        threadPool.submit(() -> {
            bot.getEventChannel().subscribe(GroupMessageEvent.class, event1 -> {
                if (event1.getMessage().contentToString().equals("exit")) {
                    log.info("翻译停止");
                    event1.getSubject().sendMessage("bye~");
                    return ListeningStatus.STOPPED;
                }
                String sentence = event1.getMessage().contentToString();
                String resWord = "";
                String result = "";
                // 这里的机制是这样的：
                // 如果 tran 方法报错则调用 translation 这个接口不支持发送语音
                try {
                    result = TranslateBaked.tran(event1, sentence, true);
                } catch (NullPointerException | IOException e) {
                    log.error(e.getMessage());
                    try {
                        resWord = Translate.translation(sentence, true);
                        event1.getSubject().sendMessage(resWord);
                    } catch (IOException | NoSuchAlgorithmException | NullPointerException e1) {
                        event1.getSubject().sendMessage("哎呀，没找到这个单词");
                        log.error(e1.getMessage());
                        throw new RuntimeException(e1);
                    }
                }
                event1.getSubject().sendMessage(result);
                return ListeningStatus.LISTENING;
            });
        });

        threadPool.shutdown();
    }




    @Event(type = EventType.User, command = "查看通知")
    public void getNotice(MessageEvent event) throws IOException {
        List<Object[]> notice = Notice.getNotice();
        for (Object[] objects : notice) {
            String news = "ฅ^•ﻌ•^ฅ时间--->" + objects[2] + "\n" + objects[1] + objects[0];
            event.getSubject().sendMessage(news);
        }
    }

}
