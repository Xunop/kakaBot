package com.mybot.kakaBot.entity;

import com.mybot.kakaBot.listener.MyEventHandler;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author xun
 * @create 2022/4/30 21:38
 */
@Component
@Slf4j
public class QQBot {
    @Value("${Account}")
    public Long account;

    @Value("${password}")
    private String password;

    @Value("${account.master}")
    private Long master;

    @Value("${account.friend1}")
    private Long friend1;

    @Value("${account.friend2}")
    private Long friend2;

    @Value("${account.friend3}")
    private Long friend3;

    private static Bot bot;

    private static Bot getBot() {
        return bot;
    }

    //设备认证信息文件
    private static final String deviceInfo = "device.json";

    /**
     * 启动Bot
     */
    public void startBot(){
        if (null == account || null == password) {
            System.err.println("*****未配置账号或密码*****");
            log.warn("*****未配置账号或密码*****");
        }

        bot = BotFactory.INSTANCE.newBot(account, password, new BotConfiguration() {
            {
                //保存设备信息到文件deviceInfo.json文件里相当于是个设备认证信息
                fileBasedDeviceInfo(deviceInfo);
                setProtocol(MiraiProtocol.ANDROID_PHONE); // 切换协议
            }
        });
        bot.getEventChannel().registerListenerHost(new MyEventHandler());
        bot.login();
    }

    public Long getMaster() {
        return master;
    }
    public Long getFriend1() {
        return friend1;
    }
    public Long getFriend2() {
        return friend2;
    }
    public Long getFriend3() {
        return friend3;
    }
}
