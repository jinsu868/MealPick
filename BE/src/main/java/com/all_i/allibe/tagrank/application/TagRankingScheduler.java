package com.all_i.allibe.tagrank.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagRankingScheduler {

    private final TagRankingService tagRankingService;

//    @Scheduled(cron = "0 0 1 * * ?")
//    public void updateTagRanking() {
//        tagRankingService.updateTagRanking();
//    }

}
