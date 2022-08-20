package com.mybot.kakaBot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class BotDemoApplicationTests {

    @Test
    void contextLoads() throws InterruptedException {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            strings.add("==" + i);
        }
        System.out.println(strings.toString());
    }

}
