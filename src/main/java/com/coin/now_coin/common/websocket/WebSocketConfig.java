package com.coin.now_coin.common.websocket;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {



    private final CustomWebSocketHandler customWebSocketHandler;

    public WebSocketConfig(CustomWebSocketHandler customWebSocketHandler) {
        this.customWebSocketHandler = customWebSocketHandler;
    }

    /**
     * WebSocket 핸들러 등록 메서드, /ws로 들어오는 요청에 대해 핸들러 지정
     * @param registry WebSocket 등록시킬 레지스트리 객체
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customWebSocketHandler, "/ws")//  핸들러를 /ws 경로에 매핑, /ws 접속시 웹소켓 연결
                .setAllowedOrigins("*");// CORS 모든도메인 허용
    }


}
