package com.mybot.kakaBot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mybot.kakaBot.anotation.Event;
import com.mybot.kakaBot.entity.Image;
import com.mybot.kakaBot.entity.QQBot;
import com.mybot.kakaBot.enums.ErrorEnum;
import com.mybot.kakaBot.enums.EventType;
import com.mybot.kakaBot.exception.LocalRuntimeException;
import com.mybot.kakaBot.mapper.ImagesMapper;
import com.mybot.kakaBot.service.ImagesService;
import com.mybot.kakaBot.util.FileUtil;
import com.mybot.kakaBot.util.ImagesUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.MessageEvent;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Author xun
 * @create 2022/7/31 17:43
 */
@Slf4j
@Controller
public class ImageController {

    @Resource
    ImagesMapper imagesMapper;
    @Resource
    FileUtil fileUtil;

    @Resource
    ImagesUtil imagesUtil;

    @Resource
    ImagesService imagesService;

    @Resource
    QQBot qqBot;

    private static int r18 = 0;

    @Event(type = EventType.ALl, command = "涩涩")
    public void sendImage(MessageEvent event) throws IOException {
        Image image = imagesMapper.selectOne();
        try (InputStream stream = fileUtil.getObject("images", image.getFileName())) {
            imagesUtil.sendMessage(event, stream, image);
        }
    }


    @Event(type = EventType.Master, command = "/istart")
    public void download() {

        final ExecutorService threadPool = Executors.newCachedThreadPool();
        Bot bot = Bot.getInstance(qqBot.account);
        Friend master = bot.getFriend(qqBot.getMaster());
        assert master != null;
        log.info("start");
        threadPool.submit(() -> {
            for (int i = 0; i < 250; i++) {
                try {
                    imagesService.insertImage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        threadPool.shutdown();
        master.sendMessage("success");
    }


    // 示例：来份宵宫|雷电将军 r18涩图
    // 此时 r18=1，tag=宵宫|雷电将军
    @Event(type = EventType.ALl, command = "来点涩图")
    public void searchImage(MessageEvent event) {
        String command = event.getMessage().contentToString();
        Integer num = getNum(command);
        if (num == 1){
            try {
                // 来份xxx涩图
                // 获取到tag，默认返回 r18
                String param = getParam(command);
                Image image = imagesUtil.getImage(param, r18, event);
                // 如果数据库中存在这种张图片则从minio中取，否则取url中的
                if (imagesMapper.exists(new QueryWrapper<Image>().eq("pid", image.getPid()))) {
                    try (InputStream stream = fileUtil.getObject("images", image.getFileName())) {
                        imagesUtil.sendMessage(event, stream, image);
                    }
                } else {
                    try (InputStream stream = imagesUtil.getImageStream(image.getUrl())) {
                        // 对于不存在数据库中的图片则存数据库中
                        imagesUtil.sendMessage(event, stream, image);
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage());
                event.getSubject().sendMessage("哎呀，没找到图");
                throw new LocalRuntimeException(ErrorEnum.IMAGE_NOT_EXIST);
            }
        } else {
            try {
                // 来xx份xxx涩图
                // 获取到tag，默认返回 r18
                // 必须规定 tag 不能不能含有多个值
                command = command.replace(String.valueOf(num), "");
                String param = getParam(command);
                log.info("r18：" + r18);
                log.info("param：" + param);
                // 关键函数在这里
                imagesUtil.getImageList(param, r18, event, num);
            } catch (Exception e) {
                log.error(e.getMessage());
                event.getSubject().sendMessage("哎呀，没找到图");
                throw new LocalRuntimeException(ErrorEnum.IMAGE_NOT_EXIST);
            }
        }
    }

    // 正则处理接收的消息
    // 获取关键词
    public String getParam(String input) {
        Pattern p = Pattern.compile("(?<=来份)(.*[a-zA-Z0-9\\u4E00-\\u9FA5]|)(?=涩图)");
        Matcher matcher = p.matcher(input);
        if (matcher.find()) {
            String param = matcher.group(0);
            if (param.contains("r18")) {
                r18 = 1;
                return param.replace("r18", "");
            }
            r18 = 0;
            return param;
        } else {
            log.warn("匹配失败");
            // 匹配失败默认返回 ""
            r18 = 1;
            return "";
        }
    }

    // 正则处理消息
    // 获取份数
    public Integer getNum(String input) {
        // 这个正则限制了返回数量不会超过9，如果输入了10，则返回1.区间在[1-9]
        Pattern p = Pattern.compile("(?<=来)[0-9](?=份)");
        Matcher matcher = p.matcher(input);
        if (matcher.find()){
            int num = Integer.parseInt(matcher.group(0));
            log.info("需要获取图片：" + num);
            return num;
        } else {
            log.warn("匹配失败");
            // 匹配失败默认返回 1
            return 1;
        }
    }

    /**
     * 判断数据库中是否有这张图
     *
     * @param pid 图片id
     * @return 有则 false 无则 true
     */
    public Boolean judge(Long pid) {
        List<Image> imageList = imagesMapper.selectList(new QueryWrapper<Image>().eq("pid", pid));
        return imageList.isEmpty();
    }
}
