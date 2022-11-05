package com.mybot.kakaBot;

import com.mybot.kakaBot.util.EventReflectUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class kakaBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(kakaBotApplication.class, args);
        EventReflectUtil.scanReceiveMethods();
//        QQBot bot = run.getBean(QQBot.class);
//        bot.startBot();
    }

}
