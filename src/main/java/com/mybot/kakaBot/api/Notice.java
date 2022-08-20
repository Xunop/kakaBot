package com.mybot.kakaBot.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xun
 * @create 2022/6/5 10:53
 */
@Component
public class Notice {
    public static List<Object[]> getNotice () throws IOException {
        List<Object[]> newsList = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            int j = 0;
            String url = "http://jwc.njupt.edu.cn/1594/list" + i + ".htm";
            Document document = Jsoup.parse(new URL(url), 30000);
            Element news = document.getElementById("wp_news_w4");
            Elements times = news.select("div:not(#wp_news_w4)");
            Elements links = news.select("a");
            for (Element link : links) {
                String text = link.text();
                String href = "http://jwc.njupt.edu.cn/" + link.attr("href");
                String time = times.get(j++).text();
                newsList.add(new Object[]{href, text, time});
            }
        }
        return newsList;
    }
}
