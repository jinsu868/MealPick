package com.all_i.allibe.tagrank.application;


import com.all_i.allibe.common.util.JsonUtil;
import com.all_i.allibe.tagrank.dto.response.TagRankingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankingMessageListener implements MessageListener {
    private static final String RANKING_CHANNEL_WEBSOCKET = "/topic/rankings/real-time";

    private final JsonUtil jsonUtil;
    private final SimpMessagingTemplate brokerMessagingTemplate;

    @Override
    public void onMessage(
        Message message,
        byte[] pattern
    ) {
        try {
            String messageBody = new String(message.getBody());
            var rankingResponse = jsonUtil.convertToObjects(
                    messageBody,
                    TagRankingResponse.class
            );

            brokerMessagingTemplate.convertAndSend(
                    RANKING_CHANNEL_WEBSOCKET,
                    rankingResponse
            );
        } catch (Exception e) {
            log.error("Error processing Redis message", e);
        }
    }
}
