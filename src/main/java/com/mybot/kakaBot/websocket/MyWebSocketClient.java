package com.mybot.kakaBot.websocket;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;


/**
 * @Author xun
 * @create 2022/11/2 16:03
 */
@Slf4j
public class MyWebSocketClient extends WebSocketClient {

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("------ MyWebSocket onOpen ------");
    }

    @Override
    public void onMessage(String s) {
        Bot kakabot = Bot.getInstance(191416049);
        Friend master = kakabot.getFriend(1919581623);
        assert master != null;
        master.sendMessage(s);
        log.info("-------- 接收到服务端数据： " + s + "--------");
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("------ MyWebSocket onClose ------");
    }

    @Override
    public void onError(Exception e) {
        log.info("------ MyWebSocket onError ------");
    }
}
