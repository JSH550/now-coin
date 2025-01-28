package com.coin.now_coin.common.kafka;


import com.coin.now_coin.common.websocket.CustomWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class KafkaMessageConsumer {


    // WebSocket 메시지를 처리하는 핸들러
    private final CustomWebSocketHandler customWebSocketHandler;


    public KafkaMessageConsumer(CustomWebSocketHandler customWebSocketHandler) {
        this.customWebSocketHandler = customWebSocketHandler;
    }

    /**
     * websocket-broadcast 토픽에 수신된 메시지를 처리하는 메서드
     * @param message 수신된 메시지 내용
     */
    @KafkaListener(topics = "websocket-broadcast", groupId = "websocket-group")
    public void handleBroadcastMessage(String message) {
        log.info("Received Kafka Message, topics = websocket-broadcast");
        // 수신된 메시지를 모든 연결된 WebSocket 세션에 브로드캐스트
        customWebSocketHandler.broadcastMessage(message);
    }

}
