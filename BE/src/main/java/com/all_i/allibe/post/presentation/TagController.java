package com.all_i.allibe.post.presentation;

import com.all_i.allibe.post.application.TagService;
import com.all_i.allibe.post.domain.Period;
import com.all_i.allibe.post.dto.response.PostCountDayPeriodResponse;
import java.util.List;

import com.all_i.allibe.post.dto.response.RookieMenuResponse;
import com.all_i.allibe.post.dto.response.TagSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping("/{tagName}/chart")
    public ResponseEntity<List<PostCountDayPeriodResponse>> getMonthlyPostCountInTag(
            @PathVariable(name = "tagName") String tagName,
            @RequestParam(required = false) Period period
    ) {
        var tagChart = tagService.findPostCountInTag(tagName, period);
        return ResponseEntity.ok(tagChart);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<RookieMenuResponse>> getTrendingTagMonth() {
        List<RookieMenuResponse> response = tagService.findTopTrendingTagMonth();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<TagSearchResponse>> searchTags(
            @PathVariable(name = "keyword") String keyword
    ) {
        List<TagSearchResponse> response = tagService.searhTags(keyword);
        return ResponseEntity.ok(response);
    }
}
