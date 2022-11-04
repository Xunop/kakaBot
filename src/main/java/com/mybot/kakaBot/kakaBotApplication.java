package com.mybot.kakaBot;

import com.mybot.kakaBot.entity.QQBot;
import com.mybot.kakaBot.util.EventReflectUtil;
import com.mybot.kakaBot.websocket.MyWebSocketClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.URI;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.mybot.kakaBot.mapper")
public class kakaBotApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(kakaBotApplication.class, args);
        EventReflectUtil.scanReceiveMethods();
        QQBot bot = run.getBean(QQBot.class);
        bot.startBot();
        MyWebSocketClient myClient = new MyWebSocketClient(
                URI.create("ws://localhost:8888/websocket/kakabot"));
        myClient.connect();
    }

}
