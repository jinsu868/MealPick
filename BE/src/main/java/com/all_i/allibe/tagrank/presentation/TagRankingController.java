package com.all_i.allibe.tagrank.presentation;

import com.all_i.allibe.tagrank.application.TagRankingService;
import com.all_i.allibe.tagrank.dto.response.TagRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rankings")
public class TagRankingController {
    private final TagRankingService tagRankingService;

    @GetMapping
    public ResponseEntity<List<TagRankingResponse>> getRealTimeRanking() {
        return ResponseEntity.ok(tagRankingService.getRealTimeRanking());
    }
}