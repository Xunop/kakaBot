package com.mybot.kakaBot.websocket;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @Author xun
 * @create 2022/11/2 16:03
 */
@Slf4j
public class MyWebSocketClient extends WebSocketClient {

    private static AtomicInteger reConnectTimes = new AtomicInteger(0);

    private static final Bot kakabot = Bot.getInstance(191416049);
    private static final Friend master = kakabot.getFriend(1919581623);
    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("------ MyWebSocket onOpen ------");
    }

    @Override
    public void onMessage(String s) {
        assert master != null;
        master.sendMessage(s);
        log.info("-------- 接收到服务端数据： " + s + "--------");
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        ThreadUtil.sleep(3, TimeUnit.MINUTES);
        int cul = reConnectTimes.incrementAndGet();
        if (cul > 3) {
            closeConnection(3, "real stop");
            try {
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
                URI.create("ws://localhost:8888/websocket/kakabot"));
        myClient.connect();
    }

    @Override
    public void onError(Exception e) {
        log.warn("------ MyWebSocket onError ------");
        master.sendMessage("WebSocket Error:" + '\n' + e);
    }

}
