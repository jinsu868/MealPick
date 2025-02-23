package com.all_i.allibe.tagrank.application;

import com.all_i.allibe.common.util.JsonUtil;
import com.all_i.allibe.post.dto.response.TagResponse;
import com.all_i.allibe.tagrank.dto.PostCreateEvent;
import com.all_i.allibe.tagrank.dto.response.TagRankingResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingEventHandler {
    private static final String RANKING_CHANNEL_REDIS_PUB_SUB = "ranking-updates";
    private static final String RANKING_KEY = "post:ranking";

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonUtil jsonUtil;

    @TransactionalEventListener
    public void handlePostCreated(PostCreateEvent event){
        try{
            event.tags()
                    .forEach(tag -> redisTemplate.opsForZSet().incrementScore(
                            RANKING_KEY,
                            jsonUtil.convertToJson(tag),
                            1)
                    );

            var zSetOps = redisTemplate.opsForZSet();
            var rankings = zSetOps.reverseRangeWithScores(RANKING_KEY, 0, 9);
            List<TagRankingResponse> rankingResponses = new ArrayList<>();
            if (rankings != null) {
                for (var entry : rankings) {
                    var tag = jsonUtil.convertToObject(
                            entry.getValue().toString(),
                            TagResponse.class
                    );
                    rankingResponses.add(new TagRankingResponse(
                            tag.name(),
                            entry.getScore().intValue()
                    ));
                }
            }
            redisTemplate.convertAndSend(RANKING_CHANNEL_REDIS_PUB_SUB, rankingResponses);
        } catch (Exception e){
            log.error("Failed to update ranking for post : {} ", event.postId(), e);
        }
    }
}

