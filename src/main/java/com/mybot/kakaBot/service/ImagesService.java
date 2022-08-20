package com.mybot.kakaBot.service;

import com.mybot.kakaBot.entity.Image;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author xun
 * @create 2022/5/1 19:01
 */
public interface ImagesService {

    @Async("taskExecutor")
    void insertImage() throws IOException;

}
