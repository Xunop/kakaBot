package com.mybot.kakaBot.anotation;

import com.mybot.kakaBot.enums.EventType;
import net.mamoe.mirai.event.events.MessageEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author xun
 * @create 2022/7/30 12:09
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
    EventType type() default EventType.None;
    String command() default "";
}
