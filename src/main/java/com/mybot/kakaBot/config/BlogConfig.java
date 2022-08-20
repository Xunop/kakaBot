package com.mybot.kakaBot.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author xun
 * @create 2022/6/22 22:02
 */
@Data
@Configuration
@AllArgsConstructor
@NoArgsConstructor
public class BlogConfig {

    String comment;

    @Value("${blog.ip}")
    private String ip;

    @Value("${blog.link}")
    private String link;

    @Value("${blog.mail}")
    private String mail;

    @Value("${blog.mailMd5}")
    private String mailMd5;

    @Value("${blog.nick}")
    private String nick;

    @Value("${blog.ua}")
    private String ua;

    private String pid;
    private String rid;

    @Value("${blog.url}")
    private String url;

    @Value("${blog.id}")
    private String XlcId;

    @Value("${blog.key}")
    private String XlcKey;
}
