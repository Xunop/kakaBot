package com.mybot.kakaBot.util;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import org.springframework.stereotype.Component;

/**
 * @Author xun
 * @create 2022/6/5 19:47
 */
@Component
public class MessageUtil {
    static String help = "/help---" + "获取指令" +"\n"
            + "/看看图---" + "随机图片" + "\n"
            + "/通知---"+"查看教务处通知" + "\n"
            + "/chui @某人---" +"锤他(群组中生效)" + "\n"
            + "/zhua @某人---" +"锤他(群组中生效)" + "\n"
            + "/diu @某人---" +"锤他(群组中生效)" + "\n"
            + "ᶘ ᵒᴥᵒᶅ";
    public static void help(FriendMessageEvent event) {
        event.getSender().sendMessage(help);
    }
}
