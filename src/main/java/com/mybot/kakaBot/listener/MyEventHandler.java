package com.mybot.kakaBot.listener;

import com.mybot.kakaBot.anotation.Event;

import com.mybot.kakaBot.api.SavePicture;
import com.mybot.kakaBot.entity.QQBot;
import com.mybot.kakaBot.entity.Result;
import com.mybot.kakaBot.enums.EventType;
import com.mybot.kakaBot.util.EventReflectUtil;
import com.mybot.kakaBot.util.MessageUtil;
import com.mybot.kakaBot.util.SpringContextUtil;
import com.mybot.kakaBot.api.HitokotoApi;
import kotlin.coroutines.CoroutineContext;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.*;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author xun
 * @create 2022/4/30 22:11
 */
@Slf4j
@Component
public class MyEventHandler extends SimpleListenerHost {
    @Resource
    QQBot qqBot;

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        // 处理事件处理时抛出的异常
    }

    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) throws Exception { // 可以抛出任何异常, 将在 handleException 处理
/*        if (event.getMessage().contentToString().contains("/chui")) {
            MessageChain message = event.getMessage();
            String mess = message.contentToString();
            String id = mess.substring(7, mess.length() - 1);
            GifApi.sendChui(event, event.getSubject(), id);
        }
        if (event.getMessage().contentToString().contains("/zhua")) {
            MessageChain message = event.getMessage();
            String mess = message.contentToString();
            String id = mess.substring(7, mess.length() - 1);
            GifApi.sendZhui(event, event.getSubject(), id);
        }
        if (event.getMessage().contentToString().contains("/diu")) {
            MessageChain message = event.getMessage();
            String mess = message.contentToString();
            String id = mess.substring(6, mess.length() - 1);
            GifApi.sendDiu(event, event.getSubject(), id);
        }*/

        log.info("onGroupMessage");
        for (Object[] objects : EventReflectUtil.getEventMethods()) {
            Method method = (Method) objects[0];
            com.mybot.kakaBot.anotation.Event annotation = method.getAnnotation(Event.class);
            String content = event.getMessage().contentToString();
            if (content.equals(annotation.command())  || ((filter(content) || filterNum(content)) && "来点涩图".equals(annotation.command()))) {
                if (annotation.type() == EventType.Group || annotation.type() == EventType.ALl) {
                    invokeMethod((Class<?>) objects[1], method, event);
                }
            }
        }
        // 无返回值, 表示一直监听事件.
    }

    @NotNull
    @EventHandler
    public ListeningStatus onMessageStatus(@NotNull FriendMessageEvent event) throws Exception { // 可以抛出任何异常, 将在
        String message = event.getMessage().contentToString();
        switch (message) {
            case "/help":
                MessageUtil.help(event);
                break;
            case "/一言":
                HitokotoApi hitokotoApi = new HitokotoApi();
                Result result = hitokotoApi.get();
                String Sentence = result.getSen();
                String source = result.getSource();
                event.getSubject().sendMessage(Sentence + "----" + source);
                break;
            case "/看看图":
                SavePicture.savePicture(event, event.getSubject());
                break;
        }
        log.info("onFriendMessage");
        for (Object[] objects : EventReflectUtil.getEventMethods()) {
            Method method = (Method) objects[0];
            com.mybot.kakaBot.anotation.Event annotation = method.getAnnotation(Event.class);
            String content = event.getMessage().contentToString();

            if (content.equals(annotation.command()) || ((filter(content) || filterNum(content)) && "来点涩图".equals(annotation.command()))) {
                if (annotation.type() == EventType.User || annotation.type() == EventType.ALl) {
                    invokeMethod((Class<?>) objects[1], method, event);
                } else if (annotation.type() == EventType.Master) {
                    long eventId = event.getSubject().getId();
                    if (qqBot.getMaster() == eventId) {
                        invokeMethod((Class<?>) objects[1], method, event);
                    }
                }
            }
        }
        return ListeningStatus.LISTENING; // 表示继续监听事件
        // return ListeningStatus.STOPPED; // 表示停止监听事件
    }

    /**
     * 对传入的参数进行正则处理
     * 如果是格式：来份xxx涩图，则返回true
     *
     * @param content 用户发送的消息
     * @return 是否与指定内容相同，同则 true， 不同则 false
     */
    public Boolean filter(String content) {
        Pattern p = Pattern.compile("([来份].*?[a-zA-Z0-9\\u4E00-\\u9FA5]|[涩图])");
        Matcher matcher = p.matcher(content);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            builder.append(matcher.group());
        }
        return "来份涩图".equals(builder.toString());
    }

    /**
     * 对传入的参数进行正则处理
     * @param content 用户发送的消息
     * @return 符合 来5份xxx涩图 返回true，否则 false
     */
    public Boolean filterNum(String content) {
        // 这个正则限制了返回数量不会超过9，如果输入了10，则返回1.区间在[1-9]
        Pattern p = Pattern.compile("(?<=来)[0-9](?=份)");
        Matcher matcher = p.matcher(content);
        if (matcher.find()){
            String group = matcher.group(0);
            return Integer.parseInt(group) >= 1;
        }else {
            return false;
        }
    }

    private void invokeMethod(Class<?> clazz, Method method, MessageEvent event) throws Exception { //
        // 判断将要调用的方法所需要的参数，并传入
        log.info("Invoke " + method.getName() + "()");
        Parameter[] parameters = method.getParameters(); // 这是方法的参数的集合
        final int length = parameters.length;
        if (length > 0) {
            Object[] args = new Object[length];
            for (int index = 0; index < length; index++) {
                Parameter parameter = parameters[index];
                Class<?> aClazz = (Class<?>) parameter.getParameterizedType();
                if (MessageEvent.class.isAssignableFrom(aClazz)) { // 注意这行代码
                    args[index] = event;
                } else {
                    args[index] = null;
                }
            }
            method.invoke(SpringContextUtil.getBean(clazz), args); // 再注意这行代码
        } else {
            method.invoke(SpringContextUtil.getBean(clazz));
        }
    }

}