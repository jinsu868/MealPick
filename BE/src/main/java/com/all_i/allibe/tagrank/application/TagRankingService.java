package com.all_i.allibe.tagrank.application;

import com.all_i.allibe.common.util.JsonUtil;
import com.all_i.allibe.post.domain.repository.TagHistoryRepositoryCustomImpl;
import com.all_i.allibe.post.dto.response.TagResponse;
import com.all_i.allibe.tagrank.dto.response.TagRankingResponse;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagRankingService {

    private static final int INIT_TAG_COUNT = 0;
    private static final String RANKING_CHANNEL_REDIS_PUB_SUB = "ranking-updates";
    private static final String RANKING_KEY = "post:ranking";
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonUtil jsonUtil;
    private final TagHistoryRepositoryCustomImpl tagHistoryRepositoryCustomImpl;

    public List<TagRankingResponse> getRealTimeRanking() {
        return getTopRankings();
    }

    public void updateTagRanking() {
        var tags = tagHistoryRepositoryCustomImpl.getTagCounts();
        var zSetOps = redisTemplate.opsForZSet();

        redisTemplate.delete(RANKING_KEY);

        tags.forEach(tag -> zSetOps.add(
                RANKING_KEY,
                jsonUtil.convertToJson(tag),
                INIT_TAG_COUNT)
        );

        var rankingResponses = getTopRankings();
        redisTemplate.convertAndSend(RANKING_CHANNEL_REDIS_PUB_SUB, rankingResponses);
    }

    private List<TagRankingResponse> getTopRankings() {
        var zSetOps = redisTemplate.opsForZSet();
        var rankings = zSetOps.reverseRangeWithScores(RANKING_KEY, 0, 9);

        List<TagRankingResponse> rankingResponses = new ArrayList<>();
        if (rankings != null) {
            for (var entry : rankings) {
                TagResponse tag = jsonUtil.convertToObject(
                        entry.getValue().toString(),
                        TagResponse.class
                );
                rankingResponses.add(new TagRankingResponse(
                        tag.name(),
                        entry.getScore().intValue()
                ));
            }
        }
        return rankingResponses;
    }
}


