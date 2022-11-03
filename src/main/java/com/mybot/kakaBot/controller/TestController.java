package com.mybot.kakaBot.controller;

import com.mybot.kakaBot.anotation.Event;
import com.mybot.kakaBot.enums.EventType;
import com.mybot.kakaBot.service.ImagesService;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;


/**
 * @Author xun
 * @create 2022/7/31 13:49
 */
@Slf4j
@Controller
public class TestController {

    @Resource
    ImagesService imagesService;

    @Event(type = EventType.User, command = "test")
    public void test(MessageEvent event) {

    }


//    @Event(type = EventType.Group, command = "test")
//    public void test(GroupMessageEvent event) throws IOException {
//        String tran = TranslateBaked.tran(event, event.getMessage().contentToString());
//        event.getSubject().sendMessage(tran);
//    }


    // (来\d{2}份??[a-zA-Z0-9\u4E00-\u9FA5]|[涩图]) 匹配 来xx份xxxx涩图 中的 来xx份涩图
    public static void main(String[] args) {

    }
}
