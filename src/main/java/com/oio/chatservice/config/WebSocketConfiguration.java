package com.oio.chatservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    /* SockJs Fallback을 이용해 노출할 STOMP endpoint를 설정 */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // registerStompEndpoints(): 클라이언트가 WebSocket 연결을 시작할 수 있는 엔드포인트를 정의

        // 엔드포인트 = /chats
        registry.addEndpoint("/chats").withSockJS();
        // withSockJS() = 클라이언트가 WebSocket을 지원하지 않을 경우 대체 옵션으로 SockJS를 사용
        // => SockJS는 WebSocket과 유사한 객체를 제공하는 브라우저 JavaScript 라이브러리

    } // registerStompEndpoints()

    /* 메시지 브로커에 관련된 설정 */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /app으로 시작하는 목적지를 가진 메시지는 애플리케이션 내의 메시지 핸들러로 라우팅
        registry.setApplicationDestinationPrefixes("/app");

        /*enableSimpleBroker("/topic")는 메모리 기반의 간단한 메시지 브로커를 활성화하여
        특정 토픽을 구독하는 클라이언트에게 메시지를 전송합니다.*/
        registry.enableSimpleBroker("/topic");
    } // configureMessageBroker()

} // end class
