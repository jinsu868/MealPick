package com.all_i.allibe.post.application;

import com.all_i.allibe.post.domain.Period;
import com.all_i.allibe.post.domain.repository.PostJdbcRepository;
import com.all_i.allibe.post.domain.repository.PostRepository;
import com.all_i.allibe.post.domain.repository.TagHistoryRepositoryCustomImpl;
import com.all_i.allibe.post.dto.response.PostCountDayPeriodResponse;
import com.all_i.allibe.post.dto.response.RookieMenuResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.all_i.allibe.post.dto.response.TagSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {

    private final PostRepository postRepository;
    private final PostJdbcRepository postJdbcRepository;
    private final TagHistoryRepositoryCustomImpl tagHistoryRepositoryCustomImpl;

    public List<PostCountDayPeriodResponse> findPostCountInTag(
            String tagName,
            Period period
    ) {
        LocalDateTime from = getStartTime(period);
        LocalDateTime now = LocalDateTime.now();

        return postRepository.getPostCountInPeriod(tagName, from, now)
                .stream()
                .map(postCount -> new PostCountDayPeriodResponse(
                        calculateDayDifferenceFromNow(postCount.date()),
                        postCount.tagName(),
                        postCount.count()))
                .toList();
    }

    public List<RookieMenuResponse> findTopTrendingTagMonth(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = getStartTime(Period.MONTH);
        LocalDateTime twoMonthsAgo = getStartTime(Period.TWO_MONTHS);

        List<RookieMenuResponse> response = postJdbcRepository.getTopTrendingTagsMonth(
                now,
                monthAgo,
                twoMonthsAgo
        );

        return response;
    }

    public List<TagSearchResponse> searhTags(String keyword){
        return tagHistoryRepositoryCustomImpl.searchTags(keyword);
    }

    private Long calculateDayDifferenceFromNow(String date) {
        LocalDate pastDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate today = LocalDate.now();

        return ChronoUnit.DAYS.between(pastDate, today);
    }


    private LocalDateTime getStartTime(Period period) {
        if (period == null) {
            return LocalDateTime.now().minusDays(Period.MONTH.getKey());
        }

        return LocalDateTime.now().minusDays(period.getKey());
    }
}
