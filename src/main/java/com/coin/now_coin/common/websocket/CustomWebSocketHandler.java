package com.coin.now_coin.common.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class CustomWebSocketHandler implements WebSocketHandler {


    // WebSocket 세션 정보가 저장되는 리스트  객체(스레드 안전)
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();


    /**
     * 클라이언트와 웹소켓 연결시 동작 정의 메서드
     *
     * @param session 연결된 클라이언트의 WebSocket 세션
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);  //리스트에 세션 추가
        log.info("WebSocket session established: {}", session.getId());//로그출력
    }

    /**
     * 클라이언트로부터 메시지를 수신했을때 동작 정의 메서드
     * 메시지 파싱하여 로그로 출력
     *
     * @param session 메시지를 보낸 클라이언트의 WebSocket 세션정보
     * @param message 클라이언트가 송신한 메시지
     * @throws Exception
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        String payload = (String) message.getPayload();//클라이언트에서 보낸 메시지 추출
        log.info("Received message: " + payload);//로그로 출력

    }

    /**
     * WebSocket 통신중 에러 발생시 동작 정의 메서드
     *
     * @param session   에러가 발생한 클라이언트의 웹소켓 세션 정보
     * @param exception 발생한 예외
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

        log.error("WebSocket Error : {}", exception.getMessage());//에러 메시지 로그로 출력
        session.close(CloseStatus.SERVER_ERROR);//웹소켓 세션 종료
    }

    /**
     * WebSocket 연결이 닫힐때 동작 정의 메서드
     *
     * @param session     닫힌 WebSocket 세션
     * @param closeStatus 연결 상태
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);//웹소켓 세션 리스트에서 세션 제거
        log.info("WebSocket connection closed ={}", session.getId());
    }

    /**
     * 메시지를 모든 연결된 클라이언트로 브로드캐스트
     *
     * @param message 브로드캐스트할 메시지
     */
    public void broadcastMessage(String message) {


        log.info("Broadcasting message to all local WebSocket sessions.");


        //웹소켓 세션 리스트를 돌며, 각세션에 메시지 전송
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("메시지 전송 중 에러 발생: 메시지 : {}, 웹소켓 세션 : {}", e.getMessage(), session.getId());
                }
            } else {
                log.warn("Session is closed: SessionId={}", session.getId());
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
