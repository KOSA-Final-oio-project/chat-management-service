package com.oio.chatservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    /* 메시지 브로커에 관련된 설정 */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 메시지를 발행하는 요청의 prefix는 /pub으로 시작하도록 설정
        // 메시지를 구독하는 요청의 prefix는 /sub으로 시작하도록 설정
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");

    } // configureMessageBroker()

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // registerStompEndpoints(): 클라이언트가 WebSocket 연결을 시작할 수 있는 엔드포인트를 정의

        // 개발 서버 접속 주소 = ws://~/ws-stomp
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
//                .setAllowedOrigins("*")
//                .setAllowedOrigins("http://loclahost:5173")
                .withSockJS();
        // withSockJS() = 클라이언트가 WebSocket을 지원하지 않을 경우 대체 옵션으로 SockJS를 사용
        // => SockJS는 WebSocket과 유사한 객체를 제공하는 브라우저 JavaScript 라이브러리

    } // registerStompEndpoints()

    // STOMP에서 64KB 이상의 데이터도 전송할 수 있도록 설정!
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(160 * 64 * 1024);
        registry.setSendTimeLimit(100 * 10000);
        registry.setSendBufferSizeLimit(3 * 512 * 1024);
    } // configureWebSocketTransport()

} // end class
