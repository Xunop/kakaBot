package com.mybot.kakaBot;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static net.sf.jsqlparser.parser.feature.Feature.explain;

@SpringBootTest
class BotDemoApplicationTests {

    @Test
    public void testCommand() throws IOException {
        String command = "accept";
        String url = "https://qq.wdev.cn/c/ls.html";
        Connection connect = Jsoup.connect(url);
        Document document = connect.get();
        // System.out.println(document);
        Elements mdBody = document.getElementsByClass("markdown-body");
        System.out.println(document.getElementsByTag("p"));
        // System.out.println(mdBody);
        Elements content = mdBody.select("markdown-style");


        System.out.println(explain);
    }
}
