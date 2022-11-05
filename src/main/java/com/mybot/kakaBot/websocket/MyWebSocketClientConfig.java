package com.mybot.kakaBot.websocket;

import cn.hutool.core.thread.ThreadUtil;
import com.mybot.kakaBot.entity.QQBot;
import com.mybot.kakaBot.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @Author xun
 * @create 2022/11/2 16:03
 */
@Configuration
@Slf4j
public class MyWebSocketClientConfig {
    @Value("${websocket.uri}")
    private String serverUri;


    @Resource
    private QQBot kakabot;

    @Resource
    RedisUtil redisUtil;

    private static AtomicInteger reConnectTimes = new AtomicInteger(0);


    @Bean
    public void webSocketClient(){
        new MyWebSocketClient(URI.create(serverUri)).connect();
    }

    class MyWebSocketClient extends WebSocketClient{

        public MyWebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
            super(serverUri, protocolDraft, httpHeaders, connectTimeout);
        }

        public MyWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            log.info("------ MyWebSocket onOpen ------");
        }

        @Override
        public void onMessage(String s) {
            Bot bot = kakabot.getBot();
            Object groups = redisUtil.lGet("Groups", 0, -1);
            List<String> groupList = new ArrayList<>();
            for (Object o : (List<?>) groups) {
                groupList.add(o.toString());
            }
            for (String groupId : groupList) {
                Group group = bot.getGroup(Long.parseLong(groupId));
                group.sendMessage(s);
            }
            Friend master = bot.getFriend(kakabot.getMaster());
            assert master != null;
            master.sendMessage(s);
            log.info("-------- 接收到服务端数据： " + s + "--------");
        }

        @Override
        public void onClose(int i, String s, boolean b) {
            Bot bot = kakabot.getBot();
            Friend master = bot.getFriend(kakabot.getMaster());
            ThreadUtil.sleep(3, TimeUnit.MINUTES);
            int cul = reConnectTimes.incrementAndGet();
            if (cul > 3) {
                closeConnection(3, "real stop");
                try {
                    assert master != null;
                    master.sendMessage("三次重连均失败，服务断连");
                    throw new Exception("服务端断连，3次重连均失败");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log.warn("第[{}]次断开重连", cul);
            if (isOpen()) {
                closeConnection(2, "reconnect stop");
            }
            MyWebSocketClient myClient = new MyWebSocketClient(
                    URI.create(serverUri));
            myClient.connect();
        }
        @Override
        public void onError(Exception e) {
            Bot bot = kakabot.getBot();
            Friend master = bot.getFriend(kakabot.getMaster());
            log.warn("------ MyWebSocket onError ------");
            assert master != null;
            master.sendMessage("WebSocket Error:" + '\n' + e);
        }
    }

}
