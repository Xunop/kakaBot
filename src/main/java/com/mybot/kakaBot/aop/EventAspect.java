package com.mybot.kakaBot.aop;

import com.mybot.kakaBot.anotation.Event;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Author xun
 * @create 2022/8/2 16:47
 */
@Slf4j
@Aspect
@Component
public class EventAspect {
/*    @Pointcut("@annotation(com.mybot.kakaBot.anotation.Event)")
    public void eventPointCut() {
        log.info("pointCut");
    }

    *//**
     * 将在在 ImageController 文件下带有 event 注解的方法下执行
     * @param point
     * @param event
     * @return
     * @throws Throwable
     *//*
    @Around("eventPointCut() && @annotation(event)")
    public Object imageAround(@NotNull ProceedingJoinPoint point, @NotNull Event event) throws Throwable {

        Object[] args = point.getArgs();
        MessageEvent messageEvent = (MessageEvent) args[0];

        for (Object arg : args) {
            System.out.println(arg);
        }

        Object proceed = point.proceed();
        return proceed;
    }

    @AfterReturning(value = "eventPointCut()")
    public void afterReturning(JoinPoint point) {
        System.out.println("============");
        Object[] args = point.getArgs();
        MessageEvent event = (MessageEvent) args[0];
        System.out.println("======>");
        System.out.println(event.getMessage());
    }

    @After("eventPointCut()")
    public Object xxx(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            System.out.println(arg);
        }
        System.out.println(joinPoint.getTarget());
        System.out.println(joinPoint.getKind());
        Object[] args = joinPoint.getArgs();
        MessageEvent event = (MessageEvent) args[0];
        System.out.println(event.getMessage().contentToString());
        return ListeningStatus.LISTENING;
    }*/
}
