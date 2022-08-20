package com.mybot.kakaBot.util;

import com.mybot.kakaBot.anotation.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author xun
 * @create 2022/7/30 12:46
 */
@Component
@Slf4j
public class EventReflectUtil {
    private static List<Object[]> eventMethods;

    public static void scanReceiveMethods() {
        log.info("Start to scan Receive");
        eventMethods = new ArrayList<>();
        Map<String, Object> controllers = SpringContextUtil.getApplicationContext().getBeansWithAnnotation(
                Controller.class);
        // 获取带有Controller注解的Bean
        for (Map.Entry<String, Object> entry : controllers.entrySet()) {
            Object value = entry.getValue();
            Class<?> aClass = AopUtils.getTargetClass(value);
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Event.class)) {
                    log.info("Scanned " + method.getName() + "()");
                    eventMethods.add(new Object[]{method, aClass});
                }
            }
        }
    }

    public static List<Object[]> getEventMethods() throws Exception {
        if (eventMethods == null) {
            throw new Exception("反射调用列表为null");
        }
        return eventMethods;
    }
}
