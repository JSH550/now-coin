package com.coin.now_coin.common.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaMessageProducer {
    // Kafka 메시지를 전송하기 위한 KafkaTemplate
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 특정 클라이언트에게 메시지를 발행
     * @param sessionId 웹소켓에 연결된 유저의 웹소켓 session id
     * @param message 발행할 메시지
     */
    public void sendDirectMessage(String sessionId, String message) {
        log.info("Message sent to websocket-direct topic. SessionId=" + sessionId + ", Message=" + message);
        kafkaTemplate.send("websocket-direct", sessionId, message); // 토픽의 특정 웹 소켓 세션에 메시지 발행

    }


    /**
     * websocket-broadcast 토픽에 메시지를 발행하는 메서드
     * @param message
     */
    public void sendBroadcastMessage(String message) {
        log.info("Kafka Message Send");
        // "websocket-broadcast" 토픽에 메시지를 발행하여 모든 구독자가 메시지를 받을 수 있도록 브로드캐스트
        kafkaTemplate.send("websocket-broadcast", null, message);
    }
}
