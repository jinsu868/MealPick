package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.post.dto.response.TagResponse;
import com.all_i.allibe.post.dto.response.TagSearchResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.all_i.allibe.post.domain.QTagHistory.tagHistory;

@Repository
@RequiredArgsConstructor
public class TagHistoryRepositoryCustomImpl implements TagHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<TagResponse> getTagCounts() {
        return queryFactory
                .select(
                        Projections.constructor(
                                TagResponse.class,
                                tagHistory.tagName
                        )
                )
                .from(tagHistory)
                .groupBy(tagHistory.tagName)
                .orderBy(tagHistory.tagName.count().desc())
                .fetch();
    }

    @Override
    public List<TagSearchResponse> searchTags(String keyword) {
        return queryFactory
                .select(
                        Projections.constructor(
                                TagSearchResponse.class,
                                tagHistory.tagName
                        )
                )
                .from(tagHistory)
                .where(tagHistory.tagName.containsIgnoreCase(keyword))
                .groupBy(tagHistory.tagName)
                .orderBy(tagHistory.tagName.length().asc(),
                        tagHistory.tagName.count().desc(),
                        tagHistory.tagName.asc())
                .fetch();
    }
}
