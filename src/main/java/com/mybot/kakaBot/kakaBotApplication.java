package com.mybot.kakaBot;

import com.mybot.kakaBot.entity.QQBot;
import com.mybot.kakaBot.util.EventReflectUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.mybot.kakaBot.mapper")
public class kakaBotApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(kakaBotApplication.class, args);
        EventReflectUtil.scanReceiveMethods();
        QQBot bot = run.getBean(QQBot.class);
        bot.startBot();
    }

}
