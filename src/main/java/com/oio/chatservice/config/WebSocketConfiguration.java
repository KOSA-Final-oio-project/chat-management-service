package com.oio.chatservice.config;

import com.oio.chatservice.handler.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSocket
//@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketConfigurer {
//public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    // Handler를 사용해서 WebSocket을 활성화하기 위한 Config 파일!
    // @EnableWebSocket -> WebSocket을 활성화

    private final WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket에 접속하기 위한 end point = /ws/chat
        // 도메인이 다른 서버에서도 접속 가능하도록 CORS 설정 추가
        registry.addHandler(webSocketHandler, "/ws/chat").setAllowedOriginPatterns("*");
    } // registerWebSocketHandlers()

    /*
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // registerStompEndpoints(): 클라이언트가 WebSocket 연결을 시작할 수 있는 엔드포인트를 정의

        // 개발 서버 접속 주소 = ws://~/chats
        registry.addEndpoint("/chats")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        // withSockJS() = 클라이언트가 WebSocket을 지원하지 않을 경우 대체 옵션으로 SockJS를 사용
        // => SockJS는 WebSocket과 유사한 객체를 제공하는 브라우저 JavaScript 라이브러리

    } // registerStompEndpoints()
     */

    /* 메시지 브로커에 관련된 설정 */
    /*
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 메시지를 발행하는 요청의 prefix는 /pub으로 시작하도록 설정
        // 메시지를 구독하는 요청의 prefix는 /sub으로 시작하도록 설정
        registry.setApplicationDestinationPrefixes("/sub");
        registry.enableSimpleBroker("/pub");

    } // configureMessageBroker()
    */

} // end class
